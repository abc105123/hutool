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

package org.dromara.hutool.http.client.body;

import org.dromara.hutool.core.io.resource.HttpResource;
import org.dromara.hutool.core.io.resource.Resource;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * {@link Resource}类型的Http request body，主要发送资源文件中的内容
 *
 * @author Looly
 * @since 6.0.0
 */
public class ResourceBody implements HttpBody {

	private final HttpResource resource;

	/**
	 * 创建 Http request body
	 *
	 * @param resource body内容
	 * @return BytesBody
	 */
	public static ResourceBody of(final HttpResource resource) {
		return new ResourceBody(resource);
	}

	/**
	 * 构造
	 *
	 * @param resource Body内容
	 */
	public ResourceBody(final HttpResource resource) {
		this.resource = resource;
	}

	/**
	 * 获取资源
	 *
	 * @return 资源
	 */
	public Resource getResource() {
		return this.resource;
	}

	@Override
	public void write(final OutputStream out) {
		resource.writeTo(out);
	}

	@Override
	public InputStream getStream() {
		return resource.getStream();
	}

	@Override
	public String contentType() {
		return this.resource.getContentType();
	}

	@Override
	public long contentLength() {
		return resource.size();
	}
}
