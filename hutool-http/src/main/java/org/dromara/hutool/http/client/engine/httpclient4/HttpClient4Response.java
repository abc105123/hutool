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

package org.dromara.hutool.http.client.engine.httpclient4;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.io.IORuntimeException;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.lang.wrapper.SimpleWrapper;
import org.dromara.hutool.core.util.ObjUtil;
import org.dromara.hutool.http.HttpException;
import org.dromara.hutool.http.HttpUtil;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.dromara.hutool.http.client.body.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * HttpClient响应包装<br>
 * 通过包装{@link CloseableHttpResponse}，实现获取响应状态码、响应头、响应体等内容
 *
 * @author Looly
 */
public class HttpClient4Response extends SimpleWrapper<HttpResponse> implements Response {

	/**
	 * 响应主体
	 */
	private final HttpEntity entity;
	/**
	 * 请求时的默认编码
	 */
	private final Charset requestCharset;
	private final ResponseBody body;

	/**
	 * 构造<br>
	 * 通过传入一个请求时的编码，当无法获取响应内容的编码时，默认使用响应时的编码
	 *
	 * @param rawRes  {@link HttpResponse}
	 * @param message 请求消息
	 */
	public HttpClient4Response(final HttpResponse rawRes, final Request message) {
		super(rawRes);
		this.entity = rawRes.getEntity();
		this.requestCharset = message.charset();
		this.body = message.method().isIgnoreBody() ? null : new ResponseBody(this, bodyStream());
	}


	@Override
	public int getStatus() {
		return this.raw.getStatusLine().getStatusCode();
	}

	@Override
	public String header(final String name) {
		final Header[] headers = this.raw.getHeaders(name);
		if (ArrayUtil.isNotEmpty(headers)) {
			return headers[0].getValue();
		}

		return null;
	}

	@Override
	public Map<String, List<String>> headers() {
		final Header[] headers = this.raw.getAllHeaders();
		final HashMap<String, List<String>> result = new LinkedHashMap<>(headers.length, 1);
		for (final Header header : headers) {
			final List<String> valueList = result.computeIfAbsent(header.getName(), k -> new ArrayList<>());
			valueList.add(header.getValue());
		}
		return result;
	}

	@Override
	public long contentLength() {
		return this.entity.getContentLength();
	}

	@Override
	public Charset charset() {
		return ObjUtil.defaultIfNull(Response.super.charset(), requestCharset);
	}

	@Override
	public InputStream bodyStream() {
		try {
			return this.entity.getContent();
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
	}

	@Override
	public HttpClient4Response sync() {
		final ResponseBody body = this.body;
		if(null != body){
			body.sync();
		}
		IoUtil.closeIfPossible(this.raw);
		return this;
	}

	@Override
	public ResponseBody body() {
		return this.body;
	}

	@Override
	public String bodyStr() throws HttpException {
		try {
			return EntityUtils.toString(this.entity, charset());
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		} catch (final ParseException e) {
			throw new HttpException(e);
		}
	}

	@Override
	public void close() {
		IoUtil.closeIfPossible(this.raw);
	}

	@Override
	public String toString() {
		return HttpUtil.toString(this);
	}

}
