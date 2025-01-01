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

package org.dromara.hutool.core.io.stream;

import org.dromara.hutool.core.io.IORuntimeException;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.io.buffer.FastByteBuffer;
import org.dromara.hutool.core.util.CharsetUtil;
import org.dromara.hutool.core.util.ObjUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * 基于快速缓冲FastByteBuffer的OutputStream，随着数据的增长自动扩充缓冲区
 * <p>
 * 可以通过{@link #toByteArray()}和 {@link #toString()}来获取数据
 * <p>
 * {@link #close()}方法无任何效果，当流被关闭后不会抛出IOException
 * <p>
 * 这种设计避免重新分配内存块而是分配新增的缓冲区，缓冲区不会被GC，数据也不会被拷贝到其他缓冲区。
 *
 * @author biezhi
 */
public class FastByteArrayOutputStream extends OutputStream {

	/**
	 * 根据输入流的总长度创建一个{@code FastByteArrayOutputStream}对象<br>
	 * 如果输入流的长度不确定，且
	 *
	 * @param in    输入流
	 * @param limit 限制大小
	 * @return {@code FastByteArrayOutputStream}
	 */
	public static FastByteArrayOutputStream of(final InputStream in, final int limit) {
		int length = IoUtil.length(in);
		if (length < 0 || length > limit) {
			length = limit;
		}
		if (length < 0) {
			length = IoUtil.DEFAULT_BUFFER_SIZE;
		}
		return new FastByteArrayOutputStream(length);
	}

	private final FastByteBuffer buffer;

	/**
	 * 构造
	 */
	public FastByteArrayOutputStream() {
		this(IoUtil.DEFAULT_BUFFER_SIZE);
	}

	/**
	 * 构造
	 *
	 * @param size 预估大小
	 */
	public FastByteArrayOutputStream(final int size) {
		buffer = new FastByteBuffer(size);
	}

	@Override
	public void write(final byte[] b, final int off, final int len) {
		buffer.append(b, off, len);
	}

	@Override
	public void write(final int b) {
		buffer.append((byte) b);
	}

	/**
	 * 长度
	 *
	 * @return 长度
	 */
	public int size() {
		return buffer.length();
	}

	/**
	 * 此方法无任何效果，当流被关闭后不会抛出IOException
	 */
	@Override
	public void close() {
		// nop
	}

	/**
	 * 复位
	 */
	public void reset() {
		buffer.reset();
	}

	/**
	 * 写出
	 *
	 * @param out 输出流
	 * @throws IORuntimeException IO异常
	 */
	public void writeTo(final OutputStream out) throws IORuntimeException {
		final int index = buffer.index();
		if (index < 0) {
			// 无数据写出
			return;
		}
		byte[] buf;
		try {
			for (int i = 0; i < index; i++) {
				buf = buffer.array(i);
				out.write(buf);
			}
			out.write(buffer.array(index), 0, buffer.offset());
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
	}


	/**
	 * 转为Byte数组
	 *
	 * @return Byte数组
	 */
	public byte[] toByteArray() {
		return buffer.toArray();
	}

	/**
	 * 转为Byte数组
	 *
	 * @param start 起始位置（包含）
	 * @param len   长度
	 * @return Byte数组
	 */
	public byte[] toByteArray(final int start, final int len) {
		return buffer.toArray(start, len);
	}

	/**
	 * 转为Byte数组，如果缓冲区中的数据长度固定，则直接返回原始数组<br>
	 * 注意此方法共享数组，不能修改数组内容！
	 *
	 * @return Byte数组
	 */
	public byte[] toByteArrayZeroCopyIfPossible() {
		return buffer.toArrayZeroCopyIfPossible();
	}

	@Override
	public String toString() {
		return toString(CharsetUtil.defaultCharset());
	}

	/**
	 * 转为字符串
	 *
	 * @param charset 编码,null表示默认编码
	 * @return 字符串
	 */
	public String toString(final Charset charset) {
		return new String(toByteArray(),
			ObjUtil.defaultIfNull(charset, CharsetUtil::defaultCharset));
	}

	/**
	 * 获取指定位置的字节
	 * @param index 位置
	 * @return 字节
	 */
	public byte get(final int index){
		return buffer.get(index);
	}
}
