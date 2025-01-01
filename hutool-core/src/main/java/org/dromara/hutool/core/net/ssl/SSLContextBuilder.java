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

package org.dromara.hutool.core.net.ssl;

import org.dromara.hutool.core.lang.builder.Builder;
import org.dromara.hutool.core.io.IORuntimeException;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.text.StrUtil;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.*;


/**
 * {@link SSLContext}构建器，可以自定义：<br>
 * <ul>
 *     <li>协议（protocol），默认TLS</li>
 *     <li>{@link KeyManager}，默认空</li>
 *     <li>{@link TrustManager}，默认{@code null}</li>
 *     <li>{@link SecureRandom}，默认{@code null}</li>
 * </ul>
 * <p>
 * 构建后可获得{@link SSLContext}，通过调用{@link SSLContext#getSocketFactory()}获取{@link javax.net.ssl.SSLSocketFactory}
 *
 * @author Looly
 * @since 5.5.2
 */
public class SSLContextBuilder implements SSLProtocols, Builder<SSLContext> {
	private static final long serialVersionUID = 1L;

	private String protocol = TLS;
	private KeyManager[] keyManagers;
	private TrustManager[] trustManagers;
	private SecureRandom secureRandom;
	private Provider provider;


	/**
	 * 创建 SSLContextBuilder
	 *
	 * @return SSLContextBuilder
	 */
	public static SSLContextBuilder of() {
		return new SSLContextBuilder();
	}

	/**
	 * 设置协议。例如TLS等
	 *
	 * @param protocol 协议
	 * @return 自身
	 */
	public SSLContextBuilder setProtocol(final String protocol) {
		if (StrUtil.isNotBlank(protocol)) {
			this.protocol = protocol;
		}
		return this;
	}

	/**
	 * 设置信任信息
	 *
	 * @param trustManagers TrustManager列表
	 * @return 自身
	 */
	public SSLContextBuilder setTrustManagers(final TrustManager... trustManagers) {
		if (ArrayUtil.isNotEmpty(trustManagers)) {
			this.trustManagers = trustManagers;
		}
		return this;
	}

	/**
	 * 设置 JSSE key managers
	 *
	 * @param keyManagers JSSE key managers
	 * @return 自身
	 */
	public SSLContextBuilder setKeyManagers(final KeyManager... keyManagers) {
		if (ArrayUtil.isNotEmpty(keyManagers)) {
			this.keyManagers = keyManagers;
		}
		return this;
	}

	/**
	 * 设置 SecureRandom
	 *
	 * @param secureRandom SecureRandom
	 * @return 自己
	 */
	public SSLContextBuilder setSecureRandom(final SecureRandom secureRandom) {
		if (null != secureRandom) {
			this.secureRandom = secureRandom;
		}
		return this;
	}

	/**
	 * 设置 Provider
	 *
	 * @param provider Provider，{@code null}表示使用默认或全局Provider
	 * @return this
	 */
	public SSLContextBuilder setProvider(final Provider provider) {
		this.provider = provider;
		return this;
	}

	/**
	 * 构建{@link SSLContext}
	 *
	 * @return {@link SSLContext}
	 */
	@Override
	public SSLContext build() {
		return buildQuietly();
	}

	/**
	 * 构建{@link SSLContext}需要处理异常
	 *
	 * @return {@link SSLContext}
	 * @throws NoSuchAlgorithmException 无此算法异常
	 * @throws KeyManagementException   密钥管理异常
	 * @since 5.7.22
	 */
	public SSLContext buildChecked() throws NoSuchAlgorithmException, KeyManagementException {
		final SSLContext sslContext = null != this.provider ?
			SSLContext.getInstance(protocol, provider) : SSLContext.getInstance(protocol);
		sslContext.init(this.keyManagers, this.trustManagers, this.secureRandom);
		return sslContext;
	}

	/**
	 * 构建{@link SSLContext}
	 *
	 * @return {@link SSLContext}
	 * @throws IORuntimeException 包装 GeneralSecurityException异常
	 */
	public SSLContext buildQuietly() throws IORuntimeException {
		try {
			return buildChecked();
		} catch (final GeneralSecurityException e) {
			throw new IORuntimeException(e);
		}
	}
}
