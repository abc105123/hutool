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

package org.dromara.hutool.http.client.engine.jdk;

import org.dromara.hutool.core.lang.Opt;
import org.dromara.hutool.core.net.url.UrlUtil;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.http.HttpException;
import org.dromara.hutool.http.client.HeaderOperation;
import org.dromara.hutool.http.meta.Method;
import org.dromara.hutool.http.ssl.SSLInfo;

import javax.net.ssl.HttpsURLConnection;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * http连接对象，对HttpURLConnection的包装
 *
 * @author Looly
 */
public class JdkHttpConnection implements HeaderOperation<JdkHttpConnection>, Closeable {

	private final URL url;
	private final Proxy proxy;
	private final HttpURLConnection conn;

	/**
	 * 创建HttpConnection
	 *
	 * @param urlStr URL
	 * @param proxy  代理，无代理传{@code null}
	 * @return HttpConnection
	 */
	public static JdkHttpConnection of(final String urlStr, final Proxy proxy) {
		return of(UrlUtil.toUrlForHttp(urlStr), proxy);
	}

	/**
	 * 创建HttpConnection
	 *
	 * @param url   URL
	 * @param proxy 代理，无代理传{@code null}
	 * @return HttpConnection
	 */
	public static JdkHttpConnection of(final URL url, final Proxy proxy) {
		return new JdkHttpConnection(url, proxy);
	}

	/**
	 * 构造HttpConnection
	 *
	 * @param url   URL
	 * @param proxy 代理
	 */
	public JdkHttpConnection(final URL url, final Proxy proxy) {
		this.url = url;
		this.proxy = proxy;

		// 初始化Http连接
		this.conn = HttpUrlConnectionUtil.openHttp(url, proxy);
		// 默认读取响应内容
		this.conn.setDoInput(true);
	}

	// region ----- Getters And Setters

	/**
	 * 获取请求方法,GET/POST
	 *
	 * @return 请求方法, GET/POST
	 */
	public Method getMethod() {
		return Method.valueOf(this.conn.getRequestMethod());
	}

	/**
	 * 设置请求方法
	 *
	 * @param method 请求方法
	 * @return 自己
	 */
	public JdkHttpConnection setMethod(final Method method) {
		if (Method.POST.equals(method) //
			|| Method.PUT.equals(method)//
			|| Method.PATCH.equals(method)//
			|| Method.DELETE.equals(method)) {
			this.conn.setUseCaches(false);

			// 增加PATCH方法支持
			if (Method.PATCH.equals(method)) {
				HttpUrlConnectionUtil.allowPatch();
			}
		}

		// method
		try {
			this.conn.setRequestMethod(method.toString());
		} catch (final ProtocolException e) {
			throw new HttpException(e);
		}

		return this;
	}

	/**
	 * 获取请求URL
	 *
	 * @return 请求URL
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * 获得代理
	 *
	 * @return {@link Proxy}
	 */
	public Proxy getProxy() {
		return proxy;
	}

	/**
	 * 获取HttpURLConnection对象
	 *
	 * @return HttpURLConnection
	 */
	public HttpURLConnection getHttpURLConnection() {
		return conn;
	}

	/**
	 * 是否禁用缓存
	 *
	 * @param isDisableCache 是否禁用缓存
	 * @return this
	 * @see HttpURLConnection#setUseCaches(boolean)
	 */
	public JdkHttpConnection setDisableCache(final boolean isDisableCache) {
		this.conn.setUseCaches(!isDisableCache);
		return this;
	}

	/**
	 * 设置连接超时
	 *
	 * @param timeout 超时
	 * @return this
	 */
	public JdkHttpConnection setConnectTimeout(final int timeout) {
		if (timeout > 0 && null != this.conn) {
			this.conn.setConnectTimeout(timeout);
		}

		return this;
	}

	/**
	 * 设置读取超时
	 *
	 * @param timeout 超时
	 * @return this
	 */
	public JdkHttpConnection setReadTimeout(final int timeout) {
		if (timeout > 0 && null != this.conn) {
			this.conn.setReadTimeout(timeout);
		}

		return this;
	}

	/**
	 * 设置连接和读取的超时时间
	 *
	 * @param timeout 超时时间
	 * @return this
	 */
	@SuppressWarnings("resource")
	public JdkHttpConnection setConnectionAndReadTimeout(final int timeout) {
		setConnectTimeout(timeout);
		setReadTimeout(timeout);

		return this;
	}

	/**
	 * 设置https中SSL相关请求参数<br>
	 * 有些时候htts请求会出现com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl的实现，此为sun内部api，按照普通http请求处理
	 *
	 * @param sslInfo {@link SSLInfo}
	 * @return this
	 * @throws HttpException KeyManagementException和NoSuchAlgorithmException异常包装
	 * @since 6.0.0
	 */
	public JdkHttpConnection setSSLInfo(final SSLInfo sslInfo) throws HttpException {
		final HttpURLConnection conn = this.conn;

		if (conn instanceof HttpsURLConnection) {
			// Https请求
			final HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
			// 验证域
			Opt.ofNullable(sslInfo.getHostnameVerifier()).ifPresent(httpsConn::setHostnameVerifier);
			Opt.ofNullable(sslInfo.getSocketFactory()).ifPresent(httpsConn::setSSLSocketFactory);
		}

		return this;
	}

	/**
	 * 设置固定长度的流模式，会设置HTTP请求头中的Content-Length字段，告知服务器整个请求体的精确字节大小。<br>
	 * 这在上传文件或大数据量时非常有用，因为它允许服务器准确地知道何时接收完所有的请求数据，而不需要依赖于连接的关闭来判断数据传输的结束。
	 *
	 * @param contentLength 请求体的长度。如果内容长度大于0，则启用固定长度流模式。
	 * @return this
	 */
	public JdkHttpConnection setFixedLengthStreamingMode(final long contentLength) {
		// 当内容长度大于0时，启用固定长度流模式
		if (contentLength > 0) {
			conn.setFixedLengthStreamingMode(contentLength);
		}
		// 返回当前实例，支持方法链式调用
		return this;
	}

	/**
	 * 采用流方式上传数据，无需本地缓存数据。<br>
	 * HttpUrlConnection默认是将所有数据读到本地缓存，然后再发送给服务器，这样上传大文件时就会导致内存溢出。
	 *
	 * @param blockSize 块大小（bytes数），0或小于0表示不设置Chuncked模式
	 * @return this
	 */
	public JdkHttpConnection setChunkedStreamingMode(final int blockSize) {
		if (blockSize > 0) {
			conn.setChunkedStreamingMode(blockSize);
		}
		return this;
	}

	/**
	 * 设置自动HTTP 30X跳转
	 *
	 * @param isInstanceFollowRedirects 是否自定跳转
	 * @return this
	 */
	public JdkHttpConnection setInstanceFollowRedirects(final boolean isInstanceFollowRedirects) {
		conn.setInstanceFollowRedirects(isInstanceFollowRedirects);
		return this;
	}

	// endregion ----- Getters And Setters

	// region ------ Headers

	/**
	 * 设置请求头<br>
	 * 当请求头存在时，覆盖之
	 *
	 * @param header     头名
	 * @param value      头值
	 * @param isOverride 是否覆盖旧值
	 * @return HttpConnection
	 */
	@Override
	public JdkHttpConnection header(final String header, final String value, final boolean isOverride) {
		if (null != this.conn) {
			if (isOverride) {
				this.conn.setRequestProperty(header, value);
			} else {
				this.conn.addRequestProperty(header, value);
			}
		}

		return this;
	}

	/**
	 * 获取Http请求头
	 *
	 * @param name Header名
	 * @return Http请求头值
	 */
	@Override
	public String header(final String name) {
		// getHeaderField已经实现忽略大小写
		return this.conn.getHeaderField(name);
	}

	/**
	 * 获取所有Http请求头
	 *
	 * @return Http请求头Map
	 */
	@Override
	public Map<String, List<String>> headers() {
		return this.conn.getHeaderFields();
	}

	// endregion------ Headers

	/**
	 * 连接
	 *
	 * @return this
	 * @throws IOException IO异常
	 */
	public JdkHttpConnection connect() throws IOException {
		if (null != this.conn) {
			this.conn.connect();
		}
		return this;
	}

	/**
	 * 获得输入流对象<br>
	 * 输入流对象用于读取数据
	 *
	 * @return 输入流对象
	 * @throws IOException IO异常
	 */
	public InputStream getInputStream() throws IOException {
		if (null != this.conn) {
			return this.conn.getInputStream();
		}
		return null;
	}

	/**
	 * 当返回错误代码时，获得错误内容流
	 *
	 * @return 错误内容
	 */
	public InputStream getErrorStream() {
		if (null != this.conn) {
			return this.conn.getErrorStream();
		}
		return null;
	}

	/**
	 * 获取输出流对象 输出流对象用于发送数据
	 *
	 * @return OutputStream
	 * @throws IOException IO异常
	 */
	public OutputStream getOutputStream() throws IOException {
		if (null == this.conn) {
			throw new IOException("HttpURLConnection has not been initialized.");
		}

		final Method method = getMethod();

		// 当有写出需求时，自动打开之
		this.conn.setDoOutput(true);
		final OutputStream out = this.conn.getOutputStream();

		// 解决在Rest请求中，GET请求附带body导致GET请求被强制转换为POST
		// 在sun.net.www.protocol.http.HttpURLConnection.getOutputStream0方法中，会把GET方法
		// 修改为POST，而且无法调用setRequestMethod方法修改，因此此处使用反射强制修改字段属性值
		// https://stackoverflow.com/questions/978061/http-get-with-request-body/983458
		if (method == Method.GET && method != getMethod()) {
			try {
				FieldUtil.setFieldValue(this.conn, "method", Method.GET.name());
			} catch (final RuntimeException ignore) {
				// JDK9+中可能反射失败，忽略之
			}
		}

		return out;
	}

	/**
	 * 获取响应码
	 *
	 * @return 响应码
	 * @throws IOException IO异常
	 */
	public int getCode() throws IOException {
		if (null != this.conn) {
			return this.conn.getResponseCode();
		}
		return 0;
	}

	@Override
	public String toString() {
		final StringBuilder sb = StrUtil.builder();
		sb.append("Request URL: ").append(this.url).append(StrUtil.CRLF);
		sb.append("Request Method: ").append(this.getMethod()).append(StrUtil.CRLF);

		return sb.toString();
	}

	/**
	 * 静默断开连接。不抛出异常
	 *
	 * @return this
	 * @since 4.6.0
	 */
	public JdkHttpConnection closeQuietly() {
		try {
			close();
		} catch (final Throwable e) {
			// ignore
		}

		return this;
	}

	/**
	 * 断开连接
	 */
	@Override
	public void close() {
		if (null != this.conn) {
			this.conn.disconnect();
		}
	}

}
