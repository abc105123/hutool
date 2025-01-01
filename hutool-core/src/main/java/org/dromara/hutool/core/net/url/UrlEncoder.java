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

package org.dromara.hutool.core.net.url;

import org.dromara.hutool.core.exception.HutoolException;
import org.dromara.hutool.core.text.CharUtil;
import org.dromara.hutool.core.util.CharsetUtil;

import java.nio.charset.Charset;

/**
 * URL编码器，提供百分号编码实现
 *
 * @author Looly
 * @since 6.0.0
 */
public class UrlEncoder {

	private static final Charset DEFAULT_CHARSET = CharsetUtil.UTF_8;

	/**
	 * 编码URL，默认使用UTF-8编码<br>
	 * 将需要转换的内容（ASCII码形式之外的内容），用十六进制表示法转换出来，并在之前加上%开头。。不参与编码的字符：<br>
	 * <pre>
	 *     unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"
	 * </pre>
	 *
	 * @param url URL
	 * @return 编码后的URL
	 * @throws HutoolException UnsupportedEncodingException
	 */
	public static String encodeAll(final String url) {
		return encodeAll(url, DEFAULT_CHARSET);
	}

	/**
	 * 编码URL<br>
	 * 将需要转换的内容（ASCII码形式之外的内容），用十六进制表示法转换出来，并在之前加上%开头。不参与编码的字符：<br>
	 * <pre>
	 *     unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"
	 * </pre>
	 *
	 * @param url     URL
	 * @param charset 编码，为null表示不编码
	 * @return 编码后的URL
	 * @throws HutoolException UnsupportedEncodingException
	 */
	public static String encodeAll(final String url, final Charset charset) throws HutoolException {
		return RFC3986.UNRESERVED.encode(url, charset);
	}

	/**
	 * 编码URL，默认使用UTF-8编码<br>
	 * 将需要转换的内容（ASCII码形式之外的内容），用十六进制表示法转换出来，并在之前加上%开头。<br>
	 * 此方法用于POST请求中的请求体自动编码，转义大部分特殊字符
	 *
	 * @param url URL
	 * @return 编码后的URL
	 */
	public static String encodeQuery(final String url) {
		return encodeQuery(url, DEFAULT_CHARSET);
	}

	/**
	 * 编码字符为URL中查询语句<br>
	 * 将需要转换的内容（ASCII码形式之外的内容），用十六进制表示法转换出来，并在之前加上%开头。<br>
	 * 此方法用于POST请求中的请求体自动编码，转义大部分特殊字符
	 *
	 * @param url     被编码内容
	 * @param charset 编码
	 * @return 编码后的字符
	 */
	public static String encodeQuery(final String url, final Charset charset) {
		return RFC3986.QUERY.encode(url, charset);
	}

	/**
	 * 单独编码URL中的空白符，空白符编码为%20
	 *
	 * @param urlStr URL字符串
	 * @return 编码后的字符串
	 * @since 4.5.14
	 */
	public static String encodeBlank(final CharSequence urlStr) {
		if (urlStr == null) {
			return null;
		}

		final int len = urlStr.length();
		final StringBuilder sb = new StringBuilder(len);
		char c;
		for (int i = 0; i < len; i++) {
			c = urlStr.charAt(i);
			if (CharUtil.isBlankChar(c)) {
				sb.append("%20");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
