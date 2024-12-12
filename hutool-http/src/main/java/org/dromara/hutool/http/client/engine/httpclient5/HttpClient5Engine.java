/*
 * Copyright (c) 2013-2024 Hutool Team and hutool.cn
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

package org.dromara.hutool.http.client.engine.httpclient5;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.message.BasicHeader;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.util.ObjUtil;
import org.dromara.hutool.http.GlobalHeaders;
import org.dromara.hutool.http.HttpException;
import org.dromara.hutool.http.client.ApacheHttpClientConfig;
import org.dromara.hutool.http.client.ClientConfig;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.dromara.hutool.http.client.cookie.InMemoryCookieStore;
import org.dromara.hutool.http.client.engine.AbstractClientEngine;
import org.dromara.hutool.http.proxy.ProxyInfo;
import org.dromara.hutool.http.ssl.SSLInfo;

import java.io.IOException;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Apache HttpClient5的HTTP请求引擎
 *
 * @author looly
 * @since 6.0.0
 */
public class HttpClient5Engine extends AbstractClientEngine {

	private CloseableHttpClient engine;

	/**
	 * 构造
	 */
	public HttpClient5Engine() {
		// issue#IABWBL JDK8下，在IDEA旗舰版加载Spring boot插件时，启动应用不会检查字段类是否存在
		// 此处构造时调用下这个类，以便触发类是否存在的检查
		Assert.notNull(CloseableHttpClient.class);
	}

	@Override
	public Response send(final Request message) {
		initEngine();

		final ClassicHttpRequest request = ClassicHttpRequestBuilder.INSTANCE.build(message);
		final ClassicHttpResponse response;
		try {
			//return this.engine.execute(request, (response -> new HttpClient5Response(response, message)));
			response = this.engine.executeOpen(null, request, null);
		} catch (final IOException e) {
			throw new HttpException(e);
		}
		return new HttpClient5Response(response, message);
	}

	@Override
	public Object getRawEngine() {
		return this.engine;
	}

	@Override
	public void close() throws IOException {
		IoUtil.nullSafeClose(this.engine);
	}

	@Override
	protected void reset() {
		// 重置客户端
		IoUtil.closeQuietly(this.engine);
		this.engine = null;
	}

	@Override
	protected void initEngine() {
		if (null != this.engine) {
			return;
		}

		final HttpClientBuilder clientBuilder = HttpClients.custom();
		final ClientConfig config = ObjUtil.defaultIfNull(this.config, ApacheHttpClientConfig::of);

		// 连接配置，包括SSL配置等
		clientBuilder.setConnectionManager(buildConnectionManager(config));

		// 实例级别默认请求配置
		clientBuilder.setDefaultRequestConfig(buildDefaultRequestConfig(config));

		// 缓存
		if (config.isDisableCache()) {
			clientBuilder.disableAuthCaching();
		}

		// 设置默认头信息
		clientBuilder.setDefaultHeaders(toHeaderList(GlobalHeaders.INSTANCE.headers()));

		// 设置代理
		setProxy(clientBuilder, config);

		// Cookie管理
		if(config.isUseCookieManager()){
			this.cookieStore = new InMemoryCookieStore();
			clientBuilder.setDefaultCookieStore(new HttpClient5CookieStore(this.cookieStore));
		}

		this.engine = clientBuilder.build();
	}

	/**
	 * 获取默认头列表
	 *
	 * @return 默认头列表
	 */
	private static List<Header> toHeaderList(final Map<String, ? extends Collection<String>> headersMap) {
		final List<Header> result = new ArrayList<>();
		headersMap.forEach((k, v1) -> v1.forEach((v2) -> result.add(new BasicHeader(k, v2))));
		return result;
	}

	/**
	 * 构建连接管理器，包括SSL配置和连接超时配置
	 *
	 * @param config {@link ClientConfig}
	 * @return {@link PoolingHttpClientConnectionManager}
	 */
	private static PoolingHttpClientConnectionManager buildConnectionManager(final ClientConfig config) {
		final PoolingHttpClientConnectionManagerBuilder connectionManagerBuilder = PoolingHttpClientConnectionManagerBuilder.create();
		// SSL配置
		final SSLInfo sslInfo = config.getSslInfo();
		if (null != sslInfo) {
//			connectionManagerBuilder.setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
//				.setTlsVersions(sslInfo.getProtocols())
//				.setSslContext(sslInfo.getSslContext())
//				.setHostnameVerifier(sslInfo.getHostnameVerifier())
//				.build());
			connectionManagerBuilder.setTlsSocketStrategy(TlsSocketStrategyBuilder.of()
				.setSslContext(sslInfo.getSslContext())
				.setSupportedProtocols(sslInfo.getProtocols())
				.setHostnameVerifier(sslInfo.getHostnameVerifier())
				.build()
			);
		}
		// 连接超时配置
		final int connectionTimeout = config.getConnectionTimeout();
		if (connectionTimeout > 0) {
			connectionManagerBuilder.setDefaultConnectionConfig(ConnectionConfig.custom()
				.setSocketTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
				.setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS).build());
		}

		// 连接池配置
		if (config instanceof ApacheHttpClientConfig) {
			final ApacheHttpClientConfig apacheHttpClientConfig = (ApacheHttpClientConfig) config;
			final int maxTotal = apacheHttpClientConfig.getMaxTotal();
			if (maxTotal > 0) {
				connectionManagerBuilder.setMaxConnTotal(maxTotal);
			}
			final int maxPerRoute = apacheHttpClientConfig.getMaxPerRoute();
			if (maxPerRoute > 0) {
				connectionManagerBuilder.setMaxConnPerRoute(maxPerRoute);
			}
		}

		return connectionManagerBuilder.build();
	}

	/**
	 * 构建默认请求配置，包括连接请求超时和响应（读取）超时
	 *
	 * @param config {@link ClientConfig}
	 * @return {@link RequestConfig}
	 */
	private static RequestConfig buildDefaultRequestConfig(final ClientConfig config) {
		final int connectionTimeout = config.getConnectionTimeout();

		// 请求配置
		final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
		if (connectionTimeout > 0) {
			requestConfigBuilder.setConnectionRequestTimeout(connectionTimeout, TimeUnit.MILLISECONDS);
		}
		final int readTimeout = config.getReadTimeout();
		if (readTimeout > 0) {
			requestConfigBuilder.setResponseTimeout(readTimeout, TimeUnit.MILLISECONDS);
		}
		if (config instanceof ApacheHttpClientConfig) {
			requestConfigBuilder.setMaxRedirects(((ApacheHttpClientConfig) config).getMaxRedirects());
		}

		return requestConfigBuilder.build();
	}

	/**
	 * 设置代理信息
	 *
	 * @param clientBuilder {@link HttpClientBuilder}
	 * @param config        配置
	 */
	private static void setProxy(final HttpClientBuilder clientBuilder, final ClientConfig config) {
		if (null == config) {
			return;
		}

		final ProxyInfo proxy = config.getProxy();
		if (null != proxy) {
			clientBuilder.setProxySelector(proxy.getProxySelector());
			final PasswordAuthentication auth = proxy.getAuth();
			if (null != auth) {
				// 代理验证
				final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials(
					new AuthScope(new HttpHost(proxy.getAuthHost(), proxy.getAuthPort())),
					new UsernamePasswordCredentials(auth.getUserName(), auth.getPassword()));
				clientBuilder.setDefaultCredentialsProvider(credsProvider);
			}
		}
	}
}
