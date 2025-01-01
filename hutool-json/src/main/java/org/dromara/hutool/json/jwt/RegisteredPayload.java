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

import java.util.Date;

/**
 * 注册的标准载荷（Payload）声明
 *
 * @param <T> 实现此接口的类的类型
 * @author Looly
 * @since 5.7.2
 */
public interface RegisteredPayload<T extends RegisteredPayload<T>> {

	/**
	 * jwt签发者
	 */
	String ISSUER = "iss";
	/**
	 * jwt所面向的用户
	 */
	String SUBJECT = "sub";
	/**
	 * 接收jwt的一方
	 */
	String AUDIENCE = "aud";
	/**
	 * jwt的过期时间，这个过期时间必须要大于签发时间
	 */
	String EXPIRES_AT = "exp";
	/**
	 * 生效时间，定义在什么时间之前，该jwt都是不可用的.
	 */
	String NOT_BEFORE = "nbf";
	/**
	 * jwt的签发时间
	 */
	String ISSUED_AT = "iat";
	/**
	 * jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。
	 */
	String JWT_ID = "jti";

	/**
	 * 设置 jwt签发者("iss")的Payload值
	 *
	 * @param issuer jwt签发者
	 * @return this
	 */
	default T setIssuer(final String issuer) {
		return setPayload(ISSUER, issuer);
	}

	/**
	 * 设置jwt所面向的用户("sub")的Payload值
	 *
	 * @param subject jwt所面向的用户
	 * @return this
	 */
	default T setSubject(final String subject) {
		return setPayload(SUBJECT, subject);
	}

	/**
	 * 设置接收jwt的一方("aud")的Payload值
	 *
	 * @param audience 接收jwt的一方
	 * @return this
	 */
	default T setAudience(final String... audience) {
		return setPayload(AUDIENCE, audience);
	}

	/**
	 * 设置jwt的过期时间("exp")的Payload值，这个过期时间必须要大于签发时间
	 *
	 * @param expiresAt jwt的过期时间
	 * @return this
	 * @see #setIssuedAt(Date)
	 */
	default T setExpiresAt(final Date expiresAt) {
		return setPayload(EXPIRES_AT, expiresAt);
	}

	/**
	 * 设置不可用时间点界限("nbf")的Payload值
	 *
	 * @param notBefore 不可用时间点界限，在这个时间点之前，jwt不可用
	 * @return this
	 */
	default T setNotBefore(final Date notBefore) {
		return setPayload(NOT_BEFORE, notBefore);
	}

	/**
	 * 设置jwt的签发时间("iat")
	 *
	 * @param issuedAt 签发时间
	 * @return this
	 */
	default T setIssuedAt(final Date issuedAt) {
		return setPayload(ISSUED_AT, issuedAt);
	}

	/**
	 * 设置jwt的唯一身份标识("jti")
	 *
	 * @param jwtId 唯一身份标识
	 * @return this
	 */
	default T setJWTId(final String jwtId) {
		return setPayload(JWT_ID, jwtId);
	}

	/**
	 * 设置Payload值
	 *
	 * @param name  payload名
	 * @param value payload值
	 * @return this
	 */
	T setPayload(String name, Object value);
}
