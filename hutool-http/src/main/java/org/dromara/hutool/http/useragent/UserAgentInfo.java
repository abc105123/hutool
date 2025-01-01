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

package org.dromara.hutool.http.useragent;

import org.dromara.hutool.core.regex.ReUtil;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * User-agent信息
 *
 * @author Looly
 * @since 4.2.1
 */
public class UserAgentInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 未知类型
	 */
	public static final String NameUnknown = "Unknown";

	/** 信息名称 */
	private final String name;
	/** 信息匹配模式 */
	private final Pattern pattern;

	/**
	 * 构造
	 *
	 * @param name 名字
	 * @param regex 表达式
	 */
	public UserAgentInfo(final String name, final String regex) {
		this(name, (null == regex) ? null : Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
	}

	/**
	 * 构造
	 *
	 * @param name 名字
	 * @param pattern 匹配模式
	 */
	public UserAgentInfo(final String name, final Pattern pattern) {
		this.name = name;
		this.pattern = pattern;
	}

	/**
	 * 获取信息名称
	 *
	 * @return 信息名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 获取匹配模式
	 *
	 * @return 匹配模式
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * 指定内容中是否包含匹配此信息的内容
	 *
	 * @param content User-Agent字符串
	 * @return 是否包含匹配此信息的内容
	 */
	public boolean isMatch(final String content) {
		return ReUtil.contains(this.pattern, content);
	}

	/**
	 * 是否为Unknown
	 *
	 * @return 是否为Unknown
	 */
	public boolean isUnknown() {
		return NameUnknown.equals(this.name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final UserAgentInfo other = (UserAgentInfo) obj;
		if (name == null) {
			return other.name == null;
		} else return name.equals(other.name);
	}

	@Override
	public String toString() {
		return this.name;
	}
}
