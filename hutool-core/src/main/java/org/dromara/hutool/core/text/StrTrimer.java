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

import java.io.Serializable;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * 字符串头尾指定字符去除器<br>
 * 按照断言，除去字符串头尾部的断言为真的字符，如果字符串是{@code null}，依然返回{@code null}。
 *
 * @author Looly
 * @since 6.0.0
 */
public class StrTrimer implements UnaryOperator<CharSequence>, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 去除两边空白符
	 */
	public static final StrTrimer TRIM_BLANK = new StrTrimer(TrimMode.BOTH, CharUtil::isBlankChar);
	/**
	 * 去除头部空白符
	 */
	public static final StrTrimer TRIM_PREFIX_BLANK = new StrTrimer(TrimMode.PREFIX, CharUtil::isBlankChar);
	/**
	 * 去除尾部空白符
	 */
	public static final StrTrimer TRIM_SUFFIX_BLANK = new StrTrimer(TrimMode.SUFFIX, CharUtil::isBlankChar);

	private final TrimMode mode;
	private final Predicate<Character> predicate;

	/**
	 * 构造
	 *
	 * @param mode      去除模式，可选去除头部、尾部、两边
	 * @param predicate 断言是否过掉字符，返回{@code true}表述过滤掉，{@code false}表示不过滤
	 */
	public StrTrimer(final TrimMode mode, final Predicate<Character> predicate) {
		this.mode = mode;
		this.predicate = predicate;
	}

	@Override
	public String apply(final CharSequence str) {
		if (StrUtil.isEmpty(str)) {
			return StrUtil.toStringOrNull(str);
		}

		final int length = str.length();
		int begin = 0;
		int end = length;// 扫描字符串头部

		if (mode == TrimMode.PREFIX || mode == TrimMode.BOTH) {
			// 扫描字符串头部
			while ((begin < end) && (predicate.test(str.charAt(begin)))) {
				begin++;
			}
		}
		if (mode == TrimMode.SUFFIX || mode == TrimMode.BOTH) {
			// 扫描字符串尾部
			while ((begin < end) && (predicate.test(str.charAt(end - 1)))) {
				end--;
			}
		}

		final String result;
		if ((begin > 0) || (end < length)) {
			result = str.toString().substring(begin, end);
		} else {
			result = str.toString();
		}

		return result;
	}

	/**
	 * 去除模式
	 *
	 * @author Looly
	 * @since 6.0.0
	 */
	public enum TrimMode {
		/**
		 * 字符串头部
		 */
		PREFIX,
		/**
		 * 字符串尾部
		 */
		SUFFIX,
		/**
		 * 字符串两边
		 */
		BOTH
	}
}
