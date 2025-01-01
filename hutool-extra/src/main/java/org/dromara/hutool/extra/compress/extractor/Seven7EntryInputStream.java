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

package org.dromara.hutool.extra.compress.extractor;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 7z解压中文件流读取的封装
 *
 * @author Looly
 * @since 5.5.0
 */
public class Seven7EntryInputStream extends InputStream {

	private final SevenZFile sevenZFile;
	private final long size;
	private long readSize = 0;

	/**
	 * 构造
	 *
	 * @param sevenZFile {@link SevenZFile}
	 * @param entry      {@link SevenZArchiveEntry}
	 */
	public Seven7EntryInputStream(final SevenZFile sevenZFile, final SevenZArchiveEntry entry) {
		this(sevenZFile, entry.getSize());
	}

	/**
	 * 构造
	 *
	 * @param sevenZFile {@link SevenZFile}
	 * @param size       读取长度
	 */
	public Seven7EntryInputStream(final SevenZFile sevenZFile, final long size) {
		this.sevenZFile = sevenZFile;
		this.size = size;
	}

	@Override
	public int available() throws IOException {
		try {
			return Math.toIntExact(this.size);
		} catch (final ArithmeticException e) {
			throw new IOException("Entry size is too large!(max than Integer.MAX)", e);
		}
	}

	/**
	 * 获取读取的长度（字节数）
	 *
	 * @return 读取的字节数
	 * @since 5.7.14
	 */
	public long getReadSize() {
		return this.readSize;
	}

	@Override
	public int read() throws IOException {
		this.readSize++;
		return this.sevenZFile.read();
	}
}
