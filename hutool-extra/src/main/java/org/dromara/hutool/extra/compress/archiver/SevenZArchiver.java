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

import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.io.IORuntimeException;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.array.ArrayUtil;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 7zip格式的归档封装
 *
 * @author Looly
 */
public class SevenZArchiver implements Archiver {

	private final SevenZOutputFile sevenZOutputFile;

	private SeekableByteChannel channel;
	private OutputStream out;

	/**
	 * 构造
	 *
	 * @param file 归档输出的文件
	 */
	public SevenZArchiver(final File file) {
		try {
			this.sevenZOutputFile = new SevenZOutputFile(file);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 构造
	 *
	 * @param out 归档输出的流
	 */
	public SevenZArchiver(final OutputStream out) {
		this.out = out;
		this.channel = new SeekableInMemoryByteChannel();
		try {
			this.sevenZOutputFile = new SevenZOutputFile(channel);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 构造
	 *
	 * @param channel 归档输出的文件
	 */
	public SevenZArchiver(final SeekableByteChannel channel) {
		try {
			this.sevenZOutputFile = new SevenZOutputFile(channel);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 获取{@link SevenZOutputFile}以便自定义相关设置
	 *
	 * @return {@link SevenZOutputFile}
	 */
	public SevenZOutputFile getSevenZOutputFile() {
		return this.sevenZOutputFile;
	}

	@Override
	public SevenZArchiver add(final File file, final String path, final Function<String, String> fileNameEditor, final Predicate<File> filter) {
		try {
			addInternal(file, path, fileNameEditor, filter);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
		return this;
	}

	@Override
	public SevenZArchiver finish() {
		try {
			this.sevenZOutputFile.finish();
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
		return this;
	}

	@Override
	public void close() {
		try {
			finish();
		} catch (final Exception ignore) {
			//ignore
		}
		if (null != out && this.channel instanceof SeekableInMemoryByteChannel) {
			try {
				out.write(((SeekableInMemoryByteChannel) this.channel).array());
			} catch (final IOException e) {
				throw new IORuntimeException(e);
			}
		}
		IoUtil.closeQuietly(this.sevenZOutputFile);
	}

	/**
	 * 将文件或目录加入归档包，目录采取递归读取方式按照层级加入
	 *
	 * @param file           文件或目录
	 * @param path           文件或目录的初始路径，null表示位于根路径
	 * @param fileNameEditor 文件名编辑器
	 * @param filter         文件过滤器，指定哪些文件或目录可以加入，当{@link Predicate#test(Object)}为{@code true}保留，null表示保留全部
	 */
	private void addInternal(final File file, final String path, final Function<String, String> fileNameEditor, final Predicate<File> filter) throws IOException {
		if (null != filter && !filter.test(file)) {
			return;
		}
		final SevenZOutputFile out = this.sevenZOutputFile;

		String entryName = (null == fileNameEditor) ? file.getName() : fileNameEditor.apply(file.getName());
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
					addInternal(childFile, entryName, fileNameEditor, filter);
				}
			}
		} else {
			if (file.isFile()) {
				// 文件直接写入
				out.write(FileUtil.readBytes(file));
			}
			out.closeArchiveEntry();
		}
	}
}
