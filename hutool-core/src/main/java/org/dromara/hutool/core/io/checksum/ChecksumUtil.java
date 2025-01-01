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

package org.dromara.hutool.core.io.checksum;

import org.dromara.hutool.core.io.IORuntimeException;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.io.stream.EmptyOutputStream;
import org.dromara.hutool.core.lang.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

/**
 * 校验码工具
 *
 * @author Looly
 */
public class ChecksumUtil {

	/**
	 * 计算文件CRC32校验码
	 *
	 * @param file 文件，不能为目录
	 * @return CRC32值
	 * @throws IORuntimeException IO异常
	 */
	public static long checksumCRC32(final File file) throws IORuntimeException {
		return checksum(file, new CRC32()).getValue();
	}

	/**
	 * 计算流CRC32校验码，计算后关闭流
	 *
	 * @param in 文件，不能为目录
	 * @return CRC32值
	 * @throws IORuntimeException IO异常
	 * @since 4.0.6
	 */
	public static long checksumCRC32(final InputStream in) throws IORuntimeException {
		return checksum(in, new CRC32()).getValue();
	}

	/**
	 * 计算文件校验码
	 *
	 * @param file     文件，不能为目录
	 * @param checksum {@link Checksum}
	 * @return Checksum
	 * @throws IORuntimeException IO异常
	 */
	public static Checksum checksum(final File file, final Checksum checksum) throws IORuntimeException {
		Assert.notNull(file, "File is null !");
		if (file.isDirectory()) {
			throw new IllegalArgumentException("Checksums can't be computed on directories");
		}
		try {
			return checksum(Files.newInputStream(file.toPath()), checksum);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 计算流的校验码，计算后关闭流
	 *
	 * @param in       流
	 * @param checksum {@link Checksum}
	 * @return Checksum
	 * @throws IORuntimeException IO异常
	 * @since 4.0.10
	 */
	public static Checksum checksum(InputStream in, Checksum checksum) throws IORuntimeException {
		Assert.notNull(in, "InputStream is null !");
		if (null == checksum) {
			checksum = new CRC32();
		}
		try {
			in = new CheckedInputStream(in, checksum);
			IoUtil.copy(in, EmptyOutputStream.INSTANCE);
		} finally {
			IoUtil.closeQuietly(in);
		}
		return checksum;
	}

	/**
	 * 计算流的校验码，计算后关闭流
	 *
	 * @param in       流
	 * @param checksum {@link Checksum}
	 * @return Checksum
	 * @throws IORuntimeException IO异常
	 * @since 5.4.0
	 */
	public static long checksumValue(final InputStream in, final Checksum checksum) {
		return checksum(in, checksum).getValue();
	}
}
