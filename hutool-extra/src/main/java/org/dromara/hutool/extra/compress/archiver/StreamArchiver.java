/*
 * Copyright (c) 2013-2025 Hutool Team and hutool.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hutool.extra.compress.archiver;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.io.IORuntimeException;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.extra.compress.CompressException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 数据归档封装，归档即将几个文件或目录打成一个压缩包<br>
 * 支持的归档文件格式为：
 * <ul>
 *     <li>{@link ArchiveStreamFactory#AR}</li>
 *     <li>{@link ArchiveStreamFactory#CPIO}</li>
 *     <li>{@link ArchiveStreamFactory#JAR}</li>
 *     <li>{@link ArchiveStreamFactory#TAR}</li>
 *     <li>{@link ArchiveStreamFactory#ZIP}</li>
 * </ul>
 *
 * @author Looly
 */
public class StreamArchiver implements Archiver {

	/**
	 * 创建归档器
	 *
	 * @param charset      编码
	 * @param archiverName 归档类型名称，见{@link ArchiveStreamFactory}
	 * @param file         归档输出的文件
	 * @return StreamArchiver
	 */
	public static StreamArchiver of(final Charset charset, final String archiverName, final File file) {
		return new StreamArchiver(charset, archiverName, file);
	}

	/**
	 * 创建归档器
	 *
	 * @param charset      编码
	 * @param archiverName 归档类型名称，见{@link ArchiveStreamFactory}
	 * @param out          归档输出的流
	 * @return StreamArchiver
	 */
	public static StreamArchiver of(final Charset charset, final String archiverName, final OutputStream out) {
		return new StreamArchiver(charset, archiverName, out);
	}

	private final ArchiveOutputStream<? extends ArchiveEntry> out;

	/**
	 * 构造
	 *
	 * @param charset      编码
	 * @param archiverName 归档类型名称，见{@link ArchiveStreamFactory}
	 * @param file         归档输出的文件
	 */
	public StreamArchiver(final Charset charset, final String archiverName, final File file) {
		this(charset, archiverName, FileUtil.getOutputStream(file));
	}

	/**
	 * 构造
	 *
	 * @param charset      编码
	 * @param archiverName 归档类型名称，见{@link ArchiveStreamFactory}
	 * @param targetStream 归档输出的流
	 */
	public StreamArchiver(final Charset charset, final String archiverName, final OutputStream targetStream) {
		if ("tgz".equalsIgnoreCase(archiverName) || "tar.gz".equalsIgnoreCase(archiverName)) {
			//issue#I5J33E，支持tgz格式解压
			try {
				this.out = new TarArchiveOutputStream(new GzipCompressorOutputStream(targetStream));
			} catch (final IOException e) {
				throw new IORuntimeException(e);
			}
		} else {
			final ArchiveStreamFactory factory = new ArchiveStreamFactory(charset.name());
			try {
				this.out = factory.createArchiveOutputStream(archiverName, targetStream);
			} catch (final ArchiveException e) {
				throw new CompressException(e);
			}
		}


		//特殊设置
		if (this.out instanceof TarArchiveOutputStream) {
			((TarArchiveOutputStream) out).setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
		} else if (this.out instanceof ArArchiveOutputStream) {
			((ArArchiveOutputStream) out).setLongFileMode(ArArchiveOutputStream.LONGFILE_BSD);
		}
	}
	@Override
	public StreamArchiver add(final File file, final String path, final Function<String, String> fileNameEditor, final Predicate<File> predicate) throws IORuntimeException {
		try {
			addInternal(file, path, fileNameEditor, predicate);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
		return this;
	}

	/**
	 * 结束已经增加的文件归档，此方法不会关闭归档流，可以继续添加文件
	 *
	 * @return this
	 */
	@Override
	public StreamArchiver finish() {
		try {
			this.out.finish();
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
		return this;
	}

	@SuppressWarnings("resource")
	@Override
	public void close() {
		try {
			finish();
		} catch (final Exception ignore) {
			//ignore
		}
		IoUtil.closeQuietly(this.out);
	}

	/**
	 * 将文件或目录加入归档包，目录采取递归读取方式按照层级加入
	 *
	 * @param file           文件或目录
	 * @param path           文件或目录的初始路径，{@code null}表示位于根路径
	 * @param fileNameEditor 文件名编辑器
	 * @param predicate      文件过滤器，指定哪些文件或目录可以加入，当{@link Predicate#test(Object)}为{@code true}加入。
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	private void addInternal(final File file, final String path, final Function<String, String> fileNameEditor, final Predicate<File> predicate) throws IOException {
		if (null != predicate && !predicate.test(file)) {
			return;
		}
		final ArchiveOutputStream out = this.out;

		String entryName = (fileNameEditor == null) ? file.getName() : fileNameEditor.apply(file.getName());
		if (StrUtil.isNotEmpty(path)) {
			// 非空拼接路径，格式为：path/name
			entryName = StrUtil.addSuffixIfNot(path, StrUtil.SLASH) + entryName;
		}
		out.putArchiveEntry(out.createArchiveEntry(file, entryName));

		if (file.isDirectory()) {
			// 目录遍历写入
			final File[] files = file.listFiles();
			if (ArrayUtil.isNotEmpty(files)) {
				for (final File childFile : files) {
					addInternal(childFile, entryName, fileNameEditor, predicate);
				}
			} else {
				// 空文件夹也需要关闭
				out.closeArchiveEntry();
			}
		} else {
			if (file.isFile()) {
				// 文件直接写入
				FileUtil.copy(file, out);
			}
			out.closeArchiveEntry();
		}
	}
}
