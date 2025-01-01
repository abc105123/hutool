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

package org.dromara.hutool.http.ssl;

import org.dromara.hutool.core.net.ssl.SSLProtocols;
import org.dromara.hutool.core.net.ssl.SSLContextUtil;
import org.dromara.hutool.core.net.ssl.SSLContextBuilder;
import org.dromara.hutool.core.net.ssl.TrustAnyHostnameVerifier;
import org.dromara.hutool.core.net.ssl.TrustAnyTrustManager;
import org.dromara.hutool.core.text.StrUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * HTTP请求中SSL相关信息，包括：
 * <ul>
 *     <li>{@link HostnameVerifier}</li>
 *     <li>{@link SSLContext}</li>
 *     <li>{@link X509TrustManager}</li>
 * </ul>
 *
 * @author Looly
 * @since 6.0.0
 */
public class SSLInfo {

	/**
	 * 默认{@code SSLInfo}，全部为{@code null}，使用客户端引擎默认配置
	 */
	public static final SSLInfo DEFAULT = SSLInfo.of()
		// issue#3582
		.setSslContext(SSLContextUtil.getDefault());

	/**
	 * 信任所有的{@code SSLInfo}
	 */
	public static final SSLInfo TRUST_ANY = SSLInfo.of()
			.setHostnameVerifier(TrustAnyHostnameVerifier.INSTANCE)
			.setSslContext(SSLContextUtil.createTrustAnySSLContext())
			.setTrustManager(TrustAnyTrustManager.INSTANCE);

	/**
	 * 构建{@code SSLInfo}
	 *
	 * @return {@code SSLInfo}
	 */
	public static SSLInfo of() {
		return new SSLInfo();
	}

	/**
	 * 支持的协议类型
	 */
	private String[] protocols;
	/**
	 * HostnameVerifier，用于HTTPS安全连接
	 */
	private HostnameVerifier hostnameVerifier;
	/**
	 * SSLContext，用于HTTPS安全连接
	 */
	private SSLContext sslContext;
	/**
	 * 信任管理器
	 */
	private X509TrustManager trustManager;

	/**
	 * 构造
	 */
	public SSLInfo() {
		if (StrUtil.equalsIgnoreCase("dalvik", System.getProperty("java.vm.name"))) {
			//兼容android低版本SSL连接
			this.protocols = new String[]{
					SSLProtocols.SSLv3,
					SSLProtocols.TLSv1,
					SSLProtocols.TLSv11,
					SSLProtocols.TLSv12};
		}
	}

	/**
	 * 获取所有支持的协议
	 *
	 * @return 协议列表
	 */
	public String[] getProtocols() {
		return protocols;
	}

	/**
	 * 设置协议列表
	 *
	 * @param protocols 协议列表
	 * @return this
	 */
	public SSLInfo setProtocols(final String... protocols) {
		this.protocols = protocols;
		return this;
	}

	/**
	 * 获取{@link HostnameVerifier}
	 *
	 * @return {@link HostnameVerifier}
	 */
	public HostnameVerifier getHostnameVerifier() {
		return hostnameVerifier;
	}

	/**
	 * 设置{@link HostnameVerifier}，信任所有则使用{@link TrustAnyHostnameVerifier}
	 *
	 * @param hostnameVerifier {@link HostnameVerifier}
	 * @return this
	 */
	public SSLInfo setHostnameVerifier(final HostnameVerifier hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
		return this;
	}

	/**
	 * 获取{@link SSLContext}
	 *
	 * @return {@link SSLContext}
	 */
	public SSLContext getSslContext() {
		return sslContext;
	}

	/**
	 * 设置{@link SSLContext}，可以使用{@link SSLContextBuilder}构建
	 *
	 * @param sslContext {@link SSLContext}
	 * @return this
	 */
	public SSLInfo setSslContext(final SSLContext sslContext) {
		this.sslContext = sslContext;
		return this;
	}

	/**
	 * 获取{@link X509TrustManager}
	 *
	 * @return {@link X509TrustManager}
	 */
	public X509TrustManager getTrustManager() {
		return trustManager;
	}

	/**
	 * 设置{@link X509TrustManager}，新人所有则使用{@link TrustAnyTrustManager}
	 *
	 * @param trustManager {@link X509TrustManager}
	 * @return this
	 */
	public SSLInfo setTrustManager(final X509TrustManager trustManager) {
		this.trustManager = trustManager;
		return this;
	}

	/**
	 * 获取{@link SSLSocketFactory}
	 *
	 * @return {@link SSLSocketFactory}
	 */
	public SSLSocketFactory getSocketFactory() {
		if(null == this.sslContext){
			return null;
		}
		final SSLSocketFactory factory = this.sslContext.getSocketFactory();
		return new CustomProtocolsSSLFactory(factory, this.protocols);
	}
}
