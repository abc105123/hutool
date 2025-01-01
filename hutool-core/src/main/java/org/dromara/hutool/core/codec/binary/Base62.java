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

package org.dromara.hutool.core.codec.binary;

import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.util.ByteUtil;
import org.dromara.hutool.core.util.CharsetUtil;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Base62工具类，提供Base62的编码和解码方案<br>
 *
 * @author Looly
 * @since 4.5.9
 */
public class Base62 {

	private static final Charset DEFAULT_CHARSET = CharsetUtil.UTF_8;

	// -------------------------------------------------------------------- encode
	/**
	 * Base62编码
	 *
	 * @param source 被编码的Base62字符串
	 * @return 被加密后的字符串
	 */
	public static String encode(final CharSequence source) {
		return encode(source, DEFAULT_CHARSET);
	}

	/**
	 * Base62编码
	 *
	 * @param source 被编码的Base62字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 */
	public static String encode(final CharSequence source, final Charset charset) {
		return encode(ByteUtil.toBytes(source, charset));
	}

	/**
	 * Base62编码
	 *
	 * @param source 被编码的Base62字符串
	 * @return 被加密后的字符串
	 */
	public static String encode(final byte[] source) {
		return new String(Base62Codec.INSTANCE.encode(source));
	}

	/**
	 * Base62编码
	 *
	 * @param in 被编码Base62的流（一般为图片流或者文件流）
	 * @return 被加密后的字符串
	 */
	public static String encode(final InputStream in) {
		return encode(IoUtil.readBytes(in));
	}

	/**
	 * Base62编码
	 *
	 * @param file 被编码Base62的文件
	 * @return 被加密后的字符串
	 */
	public static String encode(final File file) {
		return encode(FileUtil.readBytes(file));
	}

	/**
	 * Base62编码（反转字母表模式）
	 *
	 * @param source 被编码的Base62字符串
	 * @return 被加密后的字符串
	 */
	public static String encodeInverted(final CharSequence source) {
		return encodeInverted(source, DEFAULT_CHARSET);
	}

	/**
	 * Base62编码（反转字母表模式）
	 *
	 * @param source 被编码的Base62字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 */
	public static String encodeInverted(final CharSequence source, final Charset charset) {
		return encodeInverted(ByteUtil.toBytes(source, charset));
	}

	/**
	 * Base62编码（反转字母表模式）
	 *
	 * @param source 被编码的Base62字符串
	 * @return 被加密后的字符串
	 */
	public static String encodeInverted(final byte[] source) {
		return new String(Base62Codec.INSTANCE.encode(source, true));
	}

	/**
	 * Base62编码
	 *
	 * @param in 被编码Base62的流（一般为图片流或者文件流）
	 * @return 被加密后的字符串
	 */
	public static String encodeInverted(final InputStream in) {
		return encodeInverted(IoUtil.readBytes(in));
	}

	/**
	 * Base62编码（反转字母表模式）
	 *
	 * @param file 被编码Base62的文件
	 * @return 被加密后的字符串
	 */
	public static String encodeInverted(final File file) {
		return encodeInverted(FileUtil.readBytes(file));
	}

	// -------------------------------------------------------------------- decode
	/**
	 * Base62解码
	 *
	 * @param source 被解码的Base62字符串
	 * @return 密文解密的结果
	 */
	public static String decodeStrGbk(final CharSequence source) {
		return decodeStr(source, CharsetUtil.GBK);
	}

	/**
	 * Base62解码
	 *
	 * @param source 被解码的Base62字符串
	 * @return 密文解密的结果
	 */
	public static String decodeStr(final CharSequence source) {
		return decodeStr(source, DEFAULT_CHARSET);
	}

	/**
	 * Base62解码
	 *
	 * @param source 被解码的Base62字符串
	 * @param charset 字符集
	 * @return 密文解密的结果
	 */
	public static String decodeStr(final CharSequence source, final Charset charset) {
		return StrUtil.str(decode(source), charset);
	}

	/**
	 * Base62解码
	 *
	 * @param Base62 被解码的Base62字符串
	 * @param destFile 目标文件
	 * @return 目标文件
	 */
	public static File decodeToFile(final CharSequence Base62, final File destFile) {
		return FileUtil.writeBytes(decode(Base62), destFile);
	}

	/**
	 * Base62解码
	 *
	 * @param base62Str 被解码的Base62字符串
	 * @param out 写出到的流
	 * @param isCloseOut 是否关闭输出流
	 */
	public static void decodeToStream(final CharSequence base62Str, final OutputStream out, final boolean isCloseOut) {
		IoUtil.write(out, isCloseOut, decode(base62Str));
	}

	/**
	 * Base62解码
	 *
	 * @param base62Str 被解码的Base62字符串
	 * @return 被加密后的字符串
	 */
	public static byte[] decode(final CharSequence base62Str) {
		return decode(ByteUtil.toBytes(base62Str, DEFAULT_CHARSET));
	}

	/**
	 * 解码Base62
	 *
	 * @param base62bytes Base62输入
	 * @return 解码后的bytes
	 */
	public static byte[] decode(final byte[] base62bytes) {
		return Base62Codec.INSTANCE.decode(base62bytes);
	}

	/**
	 * Base62解码（反转字母表模式）
	 *
	 * @param source 被解码的Base62字符串
	 * @return 被加密后的字符串
	 */
	public static String decodeStrInverted(final CharSequence source) {
		return decodeStrInverted(source, DEFAULT_CHARSET);
	}

	/**
	 * Base62解码（反转字母表模式）
	 *
	 * @param source 被解码的Base62字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 */
	public static String decodeStrInverted(final CharSequence source, final Charset charset) {
		return StrUtil.str(decodeInverted(source), charset);
	}

	/**
	 * Base62解码（反转字母表模式）
	 *
	 * @param Base62 被解码的Base62字符串
	 * @param destFile 目标文件
	 * @return 目标文件
	 */
	public static File decodeToFileInverted(final CharSequence Base62, final File destFile) {
		return FileUtil.writeBytes(decodeInverted(Base62), destFile);
	}

	/**
	 * Base62解码（反转字母表模式）
	 *
	 * @param base62Str 被解码的Base62字符串
	 * @param out 写出到的流
	 * @param isCloseOut 是否关闭输出流
	 */
	public static void decodeToStreamInverted(final CharSequence base62Str, final OutputStream out, final boolean isCloseOut) {
		IoUtil.write(out, isCloseOut, decodeInverted(base62Str));
	}

	/**
	 * Base62解码（反转字母表模式）
	 *
	 * @param base62Str 被解码的Base62字符串
	 * @return 被加密后的字符串
	 */
	public static byte[] decodeInverted(final CharSequence base62Str) {
		return decodeInverted(ByteUtil.toBytes(base62Str, DEFAULT_CHARSET));
	}

	/**
	 * 解码Base62（反转字母表模式）
	 *
	 * @param base62bytes Base62输入
	 * @return 解码后的bytes
	 */
	public static byte[] decodeInverted(final byte[] base62bytes) {
		return Base62Codec.INSTANCE.decode(base62bytes, true);
	}
}
