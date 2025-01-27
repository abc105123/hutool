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

package org.dromara.hutool.core.text;

import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.func.FunctionPool;
import org.dromara.hutool.core.text.split.SplitUtil;
import org.dromara.hutool.core.util.CharsetUtil;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * 字符串工具类<br>
 * 此工具主要针对单个字符串的操作
 *
 * <p>本工具类，v6.x进行了拆分。
 * 字符串分割<strong>split</strong>参考：{@link SplitUtil} &nbsp;&nbsp;<br>
 * 多字符串判空<strong>hasBlank</strong>参考：{@link ArrayUtil}
 * </p>
 *
 * @author Looly
 * @see SplitUtil#split(CharSequence, CharSequence)  对字符串分割
 * @see ArrayUtil#hasBlank(CharSequence...) 对多个字符串判空
 */
public class StrUtil extends CharSequenceUtil implements StrPool {

	// ------------------------------------------------------------------------ Blank

	/**
	 * <p>如果对象是字符串是否为空白，空白的定义如下：</p>
	 * <ol>
	 *     <li>{@code null}</li>
	 *     <li>空字符串：{@code ""}</li>
	 *     <li>空格、全角空格、制表符、换行符，等不可见字符</li>
	 * </ol>
	 *
	 * <p>例：</p>
	 * <ul>
	 *     <li>{@code StrUtil.isBlankIfStr(null)     // true}</li>
	 *     <li>{@code StrUtil.isBlankIfStr("")       // true}</li>
	 *     <li>{@code StrUtil.isBlankIfStr(" \t\n")  // true}</li>
	 *     <li>{@code StrUtil.isBlankIfStr("abc")    // false}</li>
	 * </ul>
	 *
	 * <p>注意：该方法与 {@link #isEmptyIfStr(Object)} 的区别是：
	 * 该方法会校验空白字符，且性能相对于 {@link #isEmptyIfStr(Object)} 略慢。</p>
	 *
	 * @param obj 对象
	 * @return 如果为字符串是否为空串
	 * @see StrUtil#isBlank(CharSequence)
	 * @since 3.3.0
	 */
	public static boolean isBlankIfStr(final Object obj) {
		if (null == obj) {
			return true;
		} else if (obj instanceof CharSequence) {
			return isBlank((CharSequence) obj);
		}
		return false;
	}
	// ------------------------------------------------------------------------ Empty

	/**
	 * <p>如果对象是字符串是否为空串，空的定义如下：</p><br>
	 * <ol>
	 *     <li>{@code null}</li>
	 *     <li>空字符串：{@code ""}</li>
	 * </ol>
	 *
	 * <p>例：</p>
	 * <ul>
	 *     <li>{@code StrUtil.isEmptyIfStr(null)     // true}</li>
	 *     <li>{@code StrUtil.isEmptyIfStr("")       // true}</li>
	 *     <li>{@code StrUtil.isEmptyIfStr(" \t\n")  // false}</li>
	 *     <li>{@code StrUtil.isEmptyIfStr("abc")    // false}</li>
	 * </ul>
	 *
	 * <p>注意：该方法与 {@link #isBlankIfStr(Object)} 的区别是：该方法不校验空白字符。</p>
	 *
	 * @param obj 对象
	 * @return 如果为字符串是否为空串
	 * @since 3.3.0
	 */
	public static boolean isEmptyIfStr(final Object obj) {
		if (null == obj) {
			return true;
		} else if (obj instanceof CharSequence) {
			return 0 == ((CharSequence) obj).length();
		}
		return false;
	}

	// ------------------------------------------------------------------------ Trim

	/**
	 * 给定字符串数组全部做去首尾空格
	 *
	 * @param strs 字符串数组
	 */
	public static void trim(final String[] strs) {
		if (null == strs) {
			return;
		}
		String str;
		for (int i = 0; i < strs.length; i++) {
			str = strs[i];
			if (null != str) {
				strs[i] = trim(str);
			}
		}
	}

	// region ----- str

	/**
	 * 将对象转为字符串<br>
	 *
	 * <pre>
	 * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组
	 * 2、对象数组会调用Arrays.toString方法
	 * </pre>
	 *
	 * @param obj 对象
	 * @return 字符串
	 */
	public static String utf8Str(final Object obj) {
		return str(obj, CharsetUtil.UTF_8);
	}

	/**
	 * 将对象转为字符串
	 * <pre>
	 * 	 1、Byte数组和ByteBuffer会被转换为对应字符串的数组
	 * 	 2、char[]会直接构造String
	 * 	 3、对象数组会调用Arrays.toString方法
	 * </pre>
	 *
	 * @param obj     对象
	 * @param charset 字符集
	 * @return 字符串
	 */
	public static String str(final Object obj, final Charset charset) {
		if (null == obj) {
			return null;
		}

		if (obj instanceof String) {
			return (String) obj;
		} else if (obj instanceof char[]) {
			return new String((char[]) obj);
		} else if (obj instanceof byte[]) {
			return str((byte[]) obj, charset);
		} else if (obj instanceof Byte[]) {
			return str((Byte[]) obj, charset);
		} else if (obj instanceof ByteBuffer) {
			return str((ByteBuffer) obj, charset);
		} else if (ArrayUtil.isArray(obj)) {
			return ArrayUtil.toString(obj);
		}

		return obj.toString();
	}

	/**
	 * 解码字节码
	 *
	 * @param data    字符串
	 * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
	 * @return 解码后的字符串
	 */
	public static String str(final byte[] data, final Charset charset) {
		if (data == null) {
			return null;
		}

		if (null == charset) {
			return new String(data);
		}
		return new String(data, charset);
	}

	/**
	 * 解码字节码
	 *
	 * @param data    字符串
	 * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
	 * @return 解码后的字符串
	 */
	public static String str(final Byte[] data, final Charset charset) {
		if (data == null) {
			return null;
		}

		final byte[] bytes = new byte[data.length];
		Byte dataByte;
		for (int i = 0; i < data.length; i++) {
			dataByte = data[i];
			bytes[i] = (null == dataByte) ? -1 : dataByte;
		}

		return str(bytes, charset);
	}

	/**
	 * 将编码的byteBuffer数据转换为字符串
	 *
	 * @param data    数据
	 * @param charset 字符集，如果为空使用当前系统字符集
	 * @return 字符串
	 */
	public static String str(final ByteBuffer data, Charset charset) {
		if (null == charset) {
			charset = Charset.defaultCharset();
		}
		return charset.decode(data).toString();
	}

	/**
	 * JDK8中，通过{@code String(char[] value, boolean share)}这个内部构造创建String对象。<br>
	 * 此函数通过传入char[]，实现zero-copy的String创建，效率很高。但是要求传入的char[]不可以在其他地方修改。
	 *
	 * @param value char[]值，注意这个数组不可修改！！
	 * @return String
	 */
	public static String strFast(final char[] value) {
		return FunctionPool.createString(value);
	}
	// endregion

	/**
	 * 创建StringBuilder对象
	 *
	 * @return StringBuilder对象
	 */
	public static StringBuilder builder() {
		return new StringBuilder();
	}

	/**
	 * 创建StringBuilder对象
	 *
	 * @param capacity 初始大小
	 * @return StringBuilder对象
	 */
	public static StringBuilder builder(final int capacity) {
		return new StringBuilder(capacity);
	}

	/**
	 * 获得StringReader
	 *
	 * @param str 字符串
	 * @return StringReader
	 */
	public static StringReader getReader(final CharSequence str) {
		if (null == str) {
			return null;
		}
		return new StringReader(str.toString());
	}

	/**
	 * 获得StringWriter
	 *
	 * @return StringWriter
	 */
	public static StringWriter getWriter() {
		return new StringWriter();
	}

	/**
	 * 反转字符串<br>
	 * 例如：abcd =》dcba
	 *
	 * @param str 被反转的字符串
	 * @return 反转后的字符串
	 * @since 3.0.9
	 */
	public static String reverse(final String str) {
		return new String(ArrayUtil.reverse(str.toCharArray()));
	}

	// ------------------------------------------------------------------------ fill

	/**
	 * 将已有字符串填充为规定长度，如果已有字符串超过这个长度则返回这个字符串<br>
	 * 字符填充于字符串前
	 *
	 * @param str        被填充的字符串
	 * @param filledChar 填充的字符
	 * @param len        填充长度
	 * @return 填充后的字符串
	 * @since 3.1.2
	 */
	public static String fillBefore(final String str, final char filledChar, final int len) {
		return fill(str, filledChar, len, true);
	}

	/**
	 * 将已有字符串填充为规定长度，如果已有字符串超过这个长度则返回这个字符串<br>
	 * 字符填充于字符串后
	 *
	 * @param str        被填充的字符串
	 * @param filledChar 填充的字符
	 * @param len        填充长度
	 * @return 填充后的字符串
	 * @since 3.1.2
	 */
	public static String fillAfter(final String str, final char filledChar, final int len) {
		return fill(str, filledChar, len, false);
	}

	/**
	 * 将已有字符串填充为规定长度，如果已有字符串超过这个长度则返回这个字符串
	 *
	 * @param str        被填充的字符串
	 * @param filledChar 填充的字符
	 * @param len        填充长度
	 * @param isPre      是否填充在前
	 * @return 填充后的字符串
	 * @since 3.1.2
	 */
	public static String fill(final String str, final char filledChar, final int len, final boolean isPre) {
		final int strLen = str.length();
		if (strLen > len) {
			return str;
		}

		final String filledStr = StrUtil.repeat(filledChar, len - strLen);
		return isPre ? filledStr.concat(str) : str.concat(filledStr);
	}

	/**
	 * 计算两个字符串的相似度
	 *
	 * @param str1 字符串1
	 * @param str2 字符串2
	 * @return 相似度
	 * @since 3.2.3
	 */
	public static double similar(final String str1, final String str2) {
		return TextSimilarity.similar(str1, str2);
	}

	/**
	 * 计算两个字符串的相似度百分比
	 *
	 * @param str1  字符串1
	 * @param str2  字符串2
	 * @param scale 相似度
	 * @return 相似度百分比
	 * @since 3.2.3
	 */
	public static String similar(final String str1, final String str2, final int scale) {
		return TextSimilarity.similar(str1, str2, scale);
	}

	/**
	 * 字符串填充
	 * @param str 被填充的字符串(原始字符串)
	 * @param left 往原始字符串的左边填充
	 * @param right 往原始字符串的右边填充
	 * @param middle 往原始字符串的中间填充
	 * @param middlePos 填充的索引位置
	 * @return flexibleConcat
	 */
	public static String flexibleConcat(String str, String left, String right, String middle, int middlePos) {
		// 使用 StringBuilder 来提高拼接性能
		StringBuilder sb = new StringBuilder();

		// 如果原始字符串不为 null，添加到 StringBuilder
		if (str != null) {
			sb.append(str);
		}

		// 左边拼接字符串（如果有
		Optional.ofNullable(left)
			.filter(s -> !s.isEmpty())
			.ifPresent(l -> sb.insert(0, l.toCharArray()));

		// 右边拼接字符串（如果有）
		Optional.ofNullable(right)
			.filter(s -> !s.isEmpty())
			.ifPresent(sb::append);

		// 中间拼接字符串（如果有且位置有效）
		Optional.ofNullable(middle)
			.filter(s -> !s.isEmpty())
			.filter(s -> middlePos >= 0 && middlePos <= sb.length())
			.ifPresent(m -> sb.insert(middlePos, m.toCharArray()));

		return sb.toString();
	}
}
