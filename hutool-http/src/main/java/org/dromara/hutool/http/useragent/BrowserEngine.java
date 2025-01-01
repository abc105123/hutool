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

import org.dromara.hutool.core.collection.ListUtil;
import org.dromara.hutool.core.regex.ReUtil;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 浏览器引擎对象
 *
 * @author Looly
 * @since 4.2.1
 */
public class BrowserEngine extends UserAgentInfo {
	private static final long serialVersionUID = 1L;

	/**
	 * 未知
	 */
	public static final BrowserEngine Unknown = new BrowserEngine(NameUnknown, null);

	/**
	 * 支持的引擎类型
	 */
	public static final List<BrowserEngine> engines = ListUtil.view(
			new BrowserEngine("Trident", "trident"),
			new BrowserEngine("Webkit", "webkit"),
			new BrowserEngine("Chrome", "chrome"),
			new BrowserEngine("Opera", "opera"),
			new BrowserEngine("Presto", "presto"),
			new BrowserEngine("Gecko", "gecko"),
			new BrowserEngine("KHTML", "khtml"),
			new BrowserEngine("Konqueror", "konqueror"),
			new BrowserEngine("MIDP", "MIDP")
	);

	private final Pattern versionPattern;

	/**
	 * 构造
	 *
	 * @param name  引擎名称
	 * @param regex 关键字或表达式
	 */
	public BrowserEngine(final String name, final String regex) {
		super(name, regex);
		this.versionPattern = Pattern.compile(name + "[/\\- ]([\\w.\\-]+)", Pattern.CASE_INSENSITIVE);
	}

	/**
	 * 获取引擎版本
	 *
	 * @param userAgentString User-Agent字符串
	 * @return 版本
	 * @since 5.7.4
	 */
	public String getVersion(final String userAgentString) {
		if (isUnknown()) {
			return null;
		}
		return ReUtil.getGroup1(this.versionPattern, userAgentString);
	}
}
