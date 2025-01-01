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

package org.dromara.hutool.http;

import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.map.CaseInsensitiveMap;
import org.dromara.hutool.core.map.MapUtil;
import org.dromara.hutool.core.net.url.UrlQueryUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.http.client.ClientConfig;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.dromara.hutool.http.client.engine.ClientEngine;
import org.dromara.hutool.http.client.engine.ClientEngineFactory;
import org.dromara.hutool.http.meta.Method;
import org.dromara.hutool.http.server.engine.sun.SimpleServer;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

/**
 * Http请求工具类
 *
 * @author Looly
 */
public class HttpUtil {

	/**
	 * 检测是否https
	 *
	 * @param url URL
	 * @return 是否https
	 */
	public static boolean isHttps(final String url) {
		return StrUtil.startWithIgnoreCase(url, "https:");
	}

	/**
	 * 检测是否http
	 *
	 * @param url URL
	 * @return 是否http
	 * @since 5.3.8
	 */
	public static boolean isHttp(final String url) {
		return StrUtil.startWithIgnoreCase(url, "http:");
	}

	/**
	 * 创建Http请求对象
	 *
	 * @param method 方法枚举{@link Method}
	 * @param url    请求的URL，可以使HTTP或者HTTPS
	 * @return {@link Request}
	 */
	public static Request createRequest(final String url, final Method method) {
		return Request.of(url).method(method);
	}

	/**
	 * 创建Http GET请求对象
	 *
	 * @param url 请求的URL，可以使HTTP或者HTTPS
	 * @return {@link Request}
	 */
	public static Request createGet(final String url) {
		return createRequest(url, Method.GET);
	}

	/**
	 * 创建Http POST请求对象
	 *
	 * @param url 请求的URL，可以使HTTP或者HTTPS
	 * @return {@link Request}
	 */
	public static Request createPost(final String url) {
		return createRequest(url, Method.POST);
	}

	/**
	 * 发送get请求
	 *
	 * @param urlString     网址
	 * @param customCharset 自定义请求字符集，如果字符集获取不到，使用此字符集
	 * @return 返回内容，如果只检查状态码，正常只返回 ""，不正常返回 null
	 */
	@SuppressWarnings("resource")
	public static String get(final String urlString, final Charset customCharset) {
		return send(Request.of(urlString).charset(customCharset)).bodyStr();
	}

	/**
	 * 发送get请求
	 *
	 * @param urlString 网址
	 * @return 返回内容，如果只检查状态码，正常只返回 ""，不正常返回 null
	 */
	@SuppressWarnings("resource")
	public static String get(final String urlString) {
		return ClientEngineFactory.getEngine().send(Request.of(urlString)).bodyStr();
	}

	/**
	 * 发送get请求
	 *
	 * @param urlString 网址
	 * @param timeout   超时时长，-1表示默认超时，单位毫秒
	 * @return 返回内容，如果只检查状态码，正常只返回 ""，不正常返回 null
	 * @since 3.2.0
	 */
	@SuppressWarnings("resource")
	public static String get(final String urlString, final int timeout) {
		// 创建自定义客户端
		return ClientEngineFactory.createEngine()
			.init(ClientConfig.of().setConnectionTimeout(timeout).setReadTimeout(timeout))
			.send(Request.of(urlString)).bodyStr();
	}

	/**
	 * 发送get请求
	 *
	 * @param urlString 网址
	 * @param paramMap  post表单数据
	 * @return 返回数据
	 */
	@SuppressWarnings("resource")
	public static String get(final String urlString, final Map<String, Object> paramMap) {
		return send(Request.of(urlString).form(paramMap))
			.bodyStr();
	}

	/**
	 * 发送post请求
	 *
	 * @param urlString 网址
	 * @param paramMap  post表单数据
	 * @return 返回数据
	 */
	@SuppressWarnings("resource")
	public static String post(final String urlString, final Map<String, Object> paramMap) {
		return send(Request.of(urlString).method(Method.POST).form(paramMap))
			.bodyStr();
	}

	/**
	 * 发送post请求<br>
	 * 请求体body参数支持两种类型：
	 *
	 * <pre>
	 * 1. 标准参数，例如 a=1&amp;b=2 这种格式
	 * 2. Rest模式，此时body需要传入一个JSON或者XML字符串，Hutool会自动绑定其对应的Content-Type
	 * </pre>
	 *
	 * @param urlString 网址
	 * @param body      post表单数据
	 * @return 返回数据
	 */
	@SuppressWarnings("resource")
	public static String post(final String urlString, final String body) {
		return send(Request.of(urlString).method(Method.POST).body(body))
			.bodyStr();
	}

	/**
	 * 发送post请求<br>
	 * 请求体body参数支持两种类型：
	 *
	 * <pre>
	 * 1. 标准参数，例如 a=1&amp;b=2 这种格式
	 * 2. Rest模式，此时body需要传入一个JSON或者XML字符串，Hutool会自动绑定其对应的Content-Type
	 * </pre>
	 *
	 * @param urlString 网址
	 * @param body      post表单数据
	 * @param timeout   超时时长，-1表示默认超时，单位毫秒
	 * @return 返回数据
	 */
	@SuppressWarnings("resource")
	public static String post(final String urlString, final String body, final int timeout) {
		// 创建自定义客户端
		return ClientEngineFactory.createEngine()
			.init(ClientConfig.of().setConnectionTimeout(timeout).setReadTimeout(timeout))
			.send(Request.of(urlString).method(Method.POST).body(body)).bodyStr();
	}

	/**
	 * 使用默认HTTP引擎，发送HTTP请求
	 *
	 * @param request HTTP请求
	 * @return HTTP响应
	 */
	public static Response send(final Request request) {
		return ClientEngineFactory.getEngine().send(request);
	}

	/**
	 * 将表单数据加到URL中（用于GET表单提交）
	 * 表单的键值对会被url编码，但是url中原参数不会被编码
	 * 且对form参数进行  FormUrlEncoded ，x-www-form-urlencoded模式，此模式下空格会编码为'+'
	 *
	 * @param url     URL
	 * @param form    表单数据
	 * @param charset 编码   null表示不encode键值对
	 * @return 合成后的URL
	 */
	public static String urlWithFormUrlEncoded(final String url, final Map<String, Object> form, final Charset charset) {
		return urlWithForm(url, form, charset, true);
	}

	/**
	 * 将表单数据加到URL中（用于GET表单提交）<br>
	 * 表单的键值对会被url编码，但是url中原参数不会被编码
	 *
	 * @param url            URL
	 * @param form           表单数据
	 * @param charset        编码
	 * @param isEncodeParams 是否对键和值做转义处理
	 * @return 合成后的URL
	 */
	public static String urlWithForm(final String url, final Map<String, Object> form, final Charset charset, final boolean isEncodeParams) {
		return urlWithForm(url, UrlQueryUtil.toQuery(form, null), charset, isEncodeParams);
	}

	/**
	 * 将表单数据字符串加到URL中（用于GET表单提交）
	 *
	 * @param url         URL
	 * @param queryString 表单数据字符串
	 * @param charset     编码
	 * @param isEncode    是否对键和值做转义处理
	 * @return 拼接后的字符串
	 */
	public static String urlWithForm(final String url, final String queryString, final Charset charset, final boolean isEncode) {
		if (StrUtil.isBlank(queryString)) {
			// 无额外参数
			if (StrUtil.contains(url, '?')) {
				// url中包含参数
				return isEncode ? UrlQueryUtil.encodeQuery(url, charset) : url;
			}
			return url;
		}

		// 始终有参数
		final StringBuilder urlBuilder = new StringBuilder(url.length() + queryString.length() + 16);
		final int qmIndex = url.indexOf('?');
		if (qmIndex > 0) {
			// 原URL带参数，则对这部分参数单独编码（如果选项为进行编码）
			urlBuilder.append(isEncode ? UrlQueryUtil.encodeQuery(url, charset) : url);
			if (!StrUtil.endWith(url, '&')) {
				// 已经带参数的情况下追加参数
				urlBuilder.append('&');
			}
		} else {
			// 原url无参数，则不做编码
			urlBuilder.append(url);
			if (qmIndex < 0) {
				// 无 '?' 追加之
				urlBuilder.append('?');
			}
		}
		urlBuilder.append(isEncode ? UrlQueryUtil.encodeQuery(queryString, charset) : queryString);
		return urlBuilder.toString();
	}

	/**
	 * 创建客户端引擎
	 *
	 * @param engineName 引擎名称
	 * @return {@link ClientEngine}
	 */
	public static ClientEngine createClient(final String engineName) {
		return ClientEngineFactory.createEngine(engineName);
	}

	/**
	 * 创建简易的Http服务器
	 *
	 * @param port 端口
	 * @return {@link SimpleServer}
	 * @since 5.2.6
	 */
	public static SimpleServer createServer(final int port) {
		return new SimpleServer(port);
	}

	/**
	 * 打印{@link Response} 为可读形式
	 *
	 * @param response {@link Response}
	 * @return 字符串
	 */
	public static String toString(final Response response) {
		final StringBuilder sb = StrUtil.builder();
		sb.append("Response Status: ").append(response.getStatus()).append(StrUtil.CRLF);

		// header
		sb.append("Response Headers: ").append(StrUtil.CRLF);
		response.headers().forEach((key, value) -> sb.append("    ").append(key).append(": ").append(CollUtil.join(value, ",")).append(StrUtil.CRLF));

		// body
		sb.append("Response Body: ").append(StrUtil.CRLF);
		sb.append("    ").append(response.bodyStr()).append(StrUtil.CRLF);

		return sb.toString();
	}

	/**
	 * 打印 {@link Request} 为可读形式
	 *
	 * @param request {@link Request}
	 * @return 字符串
	 */
	public static String toString(final Request request) {
		final StringBuilder sb = StrUtil.builder();
		sb.append("Request Url: ").append(request.url()).append(StrUtil.CRLF);

		// header
		sb.append("Request Headers: ").append(StrUtil.CRLF);
		request.headers().forEach((key, value) -> sb.append("    ").append(key).append(": ").append(CollUtil.join(value, ",")).append(StrUtil.CRLF));

		// body
		sb.append("Request Body: ").append(StrUtil.CRLF);
		sb.append("    ").append(request.bodyStr()).append(StrUtil.CRLF);
		return sb.toString();
	}

	/**
	 * 获取指定的Header值，如果不存在返回{@code null}<br>
	 * 根据RFC2616规范，header的name不区分大小写，因此首先get值，不存在则遍历匹配不区分大小写的key。
	 *
	 * @param headers 头信息的Map
	 * @param name    header名
	 * @return header值
	 * @since 6.0.0
	 */
	public static String header(final Map<String, ? extends Collection<String>> headers, final String name) {
		Collection<String> values = headers.get(name);
		if (null == values && !(headers instanceof CaseInsensitiveMap)) {
			// issue#I96U4T，根据RFC2616规范，header的name不区分大小写
			values = MapUtil.firstMatchValue(headers, entry -> StrUtil.equalsIgnoreCase(name, entry.getKey()));
		}
		if (CollUtil.isNotEmpty(values)) {
			return CollUtil.getFirst(values);
		}

		return null;
	}
}
