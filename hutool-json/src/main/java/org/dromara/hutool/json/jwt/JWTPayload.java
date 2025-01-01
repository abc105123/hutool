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

package org.dromara.hutool.json.jwt;

import java.util.Map;

/**
 * JWT载荷信息<br>
 * 载荷就是存放有效信息的地方。这个名字像是特指飞机上承载的货品，这些有效信息包含三个部分:
 *
 * <ul>
 *     <li>标准中注册的声明</li>
 *     <li>公共的声明</li>
 *     <li>私有的声明</li>
 * </ul>
 * <p>
 * 详细介绍见：<a href="https://www.jianshu.com/p/576dbf44b2ae">https://www.jianshu.com/p/576dbf44b2ae</a>
 *
 * @author Looly
 * @since 5.7.0
 */
public class JWTPayload extends Claims implements RegisteredPayload<JWTPayload>{
	private static final long serialVersionUID = 1L;

	/**
	 * 增加自定义JWT认证载荷信息
	 *
	 * @param payloadClaims 载荷信息
	 * @return this
	 */
	public JWTPayload addPayloads(final Map<String, ?> payloadClaims) {
		putAll(payloadClaims);
		return this;
	}

	@Override
	public JWTPayload setPayload(final String name, final Object value) {
		setClaim(name, value);
		return this;
	}
}
