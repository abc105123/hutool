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

package org.dromara.hutool.core.text.replacer;

import org.dromara.hutool.core.text.StrUtil;

/**
 * 区间字符串替换，指定区间，将区间中的所有字符去除，替换为指定的字符串，字符串只重复一次<br>
 * 此方法使用{@link String#codePoints()}完成拆分替换
 *
 * @author Looly
 */
public class RangeReplacerByStr extends StrReplacer {
	private static final long serialVersionUID = 1L;

	private final int beginInclude;
	private final int endExclude;
	private final CharSequence replacedStr;
	private final boolean isCodePoint;

	/**
	 * 构造
	 *
	 * @param beginInclude 开始位置（包含）
	 * @param endExclude   结束位置（不包含）
	 * @param replacedStr  被替换的字符串
	 * @param isCodePoint  是否code point模式，此模式下emoji等会被作为单独的字符
	 */
	public RangeReplacerByStr(final int beginInclude, final int endExclude, final CharSequence replacedStr, final boolean isCodePoint) {
		this.beginInclude = beginInclude;
		this.endExclude = endExclude;
		this.replacedStr = replacedStr;
		this.isCodePoint = isCodePoint;
	}

	@Override
	public String apply(final CharSequence str) {
		if (StrUtil.isEmpty(str)) {
			return StrUtil.toStringOrNull(str);
		}

		final String originalStr = str.toString();
		final int[] chars = StrUtil.toChars(originalStr, this.isCodePoint);
		final int strLength = chars.length;

		final int beginInclude = this.beginInclude;
		if (beginInclude > strLength) {
			return originalStr;
		}
		int endExclude = this.endExclude;
		if (endExclude > strLength) {
			endExclude = strLength;
		}
		if (beginInclude > endExclude) {
			// 如果起始位置大于结束位置，不替换
			return originalStr;
		}

		// 新字符串长度 <= 旧长度 - (被替换区间codePoints数量) + 替换字符串长度
		final StringBuilder stringBuilder = new StringBuilder(originalStr.length() - (endExclude - beginInclude) + replacedStr.length());
		for (int i = 0; i < beginInclude; i++) {
			append(stringBuilder, chars[i]);
		}
		replace(originalStr, beginInclude, stringBuilder);
		for (int i = endExclude; i < strLength; i++) {
			append(stringBuilder, chars[i]);
		}
		return stringBuilder.toString();
	}

	@Override
	protected int replace(final CharSequence str, final int pos, final StringBuilder out) {
		// 由于区间替换，因此区间已确定，直接替换即可
		out.append(this.replacedStr);

		// 无意义的返回
		return endExclude;
	}

	/**
	 * 追加字符
	 *
	 * @param stringBuilder {@link StringBuilder}
	 * @param c             字符
	 */
	private void append(final StringBuilder stringBuilder, final int c) {
		if (isCodePoint) {
			stringBuilder.appendCodePoint(c);
		} else {
			stringBuilder.append((char) c);
		}
	}
}
