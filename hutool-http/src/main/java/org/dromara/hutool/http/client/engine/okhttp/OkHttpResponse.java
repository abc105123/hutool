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

package org.dromara.hutool.http.client.engine.okhttp;

import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.io.stream.EmptyInputStream;
import org.dromara.hutool.core.util.ObjUtil;
import org.dromara.hutool.http.GlobalCompressStreamRegister;
import org.dromara.hutool.http.HttpUtil;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.dromara.hutool.http.client.body.ResponseBody;
import org.dromara.hutool.http.meta.HeaderName;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * OkHttp3的{@link okhttp3.Response} 响应包装
 *
 * @author Looly
 */
public class OkHttpResponse implements Response {

	private final okhttp3.Response rawRes;
	/**
	 * 请求时的默认编码
	 */
	private final Charset requestCharset;
	private final ResponseBody body;

	/**
	 * @param rawRes         {@link okhttp3.Response}
	 * @param message       请求对象
	 */
	public OkHttpResponse(final okhttp3.Response rawRes, final Request message) {
		this.rawRes = rawRes;
		this.requestCharset = message.charset();
		this.body = message.method().isIgnoreBody() ? null : new ResponseBody(this, bodyStream());
	}

	@Override
	public int getStatus() {
		return rawRes.code();
	}

	@Override
	public String header(final String name) {
		return rawRes.header(name);
	}

	@Override
	public Map<String, List<String>> headers() {
		return rawRes.headers().toMultimap();
	}

	@Override
	public Charset charset() {
		return ObjUtil.defaultIfNull(Response.super.charset(), requestCharset);
	}

	@Override
	public InputStream bodyStream() {
		final okhttp3.ResponseBody body = rawRes.body();
		if(null == body){
			return EmptyInputStream.INSTANCE;
		}

		return GlobalCompressStreamRegister.INSTANCE.wrapStream(body.byteStream(),
			this.rawRes.header(HeaderName.CONTENT_ENCODING.getValue()));
	}

	@Override
	public OkHttpResponse sync() {
		if (null != this.body) {
			this.body.sync();
		}
		IoUtil.closeQuietly(this.rawRes);
		return this;
	}

	@Override
	public ResponseBody body() {
		return this.body;
	}

	@Override
	public void close() {
		IoUtil.closeQuietly(this.rawRes);
	}

	@Override
	public String toString() {
		return HttpUtil.toString(this);
	}
}
