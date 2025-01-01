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

import org.dromara.hutool.core.convert.ConvertUtil;
import org.dromara.hutool.core.io.IORuntimeException;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.io.resource.*;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.http.HttpGlobalConfig;
import org.dromara.hutool.http.meta.ContentType;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * Multipart/form-data输出流封装<br>
 * 遵循RFC2387规范，见：https://www.rfc-editor.org/rfc/rfc2387
 *
 * @author Looly
 * @since 5.7.17
 */
public class MultipartOutputStream extends OutputStream {

	private static final String CONTENT_DISPOSITION_TEMPLATE = "Content-Disposition: form-data; name=\"{}\"\r\n";
	private static final String CONTENT_DISPOSITION_FILE_TEMPLATE = "Content-Disposition: form-data; name=\"{}\"; filename=\"{}\"\r\n";

	private static final String CONTENT_TYPE_FILE_TEMPLATE = "Content-Type: {}\r\n";

	private final OutputStream out;
	private final Charset charset;
	private final String boundary;

	private boolean isFinish;

	/**
	 * 构造，使用全局默认的边界字符串
	 *
	 * @param out     HTTP写出流
	 * @param charset 编码
	 */
	public MultipartOutputStream(final OutputStream out, final Charset charset) {
		this(out, charset, HttpGlobalConfig.getBoundary());
	}

	/**
	 * 构造
	 *
	 * @param out      HTTP写出流
	 * @param charset  编码
	 * @param boundary 边界符
	 * @since 5.7.17
	 */
	public MultipartOutputStream(final OutputStream out, final Charset charset, final String boundary) {
		this.out = out;
		this.charset = charset;
		this.boundary = boundary;
	}

	/**
	 * 添加Multipart表单的数据项<br>
	 * <pre>
	 *     --分隔符(boundary)[换行]
	 *     Content-Disposition: form-data; name="参数名"[换行]
	 *     [换行]
	 *     参数值[换行]
	 * </pre>
	 * <p>
	 * 或者：
	 *
	 * <pre>
	 *     --分隔符(boundary)[换行]
	 *     Content-Disposition: form-data; name="表单名"; filename="文件名"[换行]
	 *     Content-Type: MIME类型[换行]
	 *     [换行]
	 *     文件的二进制内容[换行]
	 * </pre>
	 *
	 * @param formFieldName 表单名
	 * @param value         值，可以是普通值、资源（如文件等）
	 * @return this
	 * @throws IORuntimeException IO异常
	 */
	@SuppressWarnings("resource")
	public MultipartOutputStream write(final String formFieldName, final Object value) throws IORuntimeException {
		// 多资源
		if (value instanceof MultiResource) {
			for (final Resource subResource : (MultiResource) value) {
				write(formFieldName, subResource);
			}
			return this;
		}

		// --分隔符(boundary)[换行]
		beginPart();

		if (value instanceof Resource) {
			appendResource(formFieldName, (Resource) value);
		} else if (value instanceof File) {
			appendResource(formFieldName, new FileResource((File) value));
		} else if (value instanceof Path) {
			appendResource(formFieldName, new FileResource((Path) value));
		} else if (value instanceof byte[]) {
			appendResource(formFieldName, new BytesResource((byte[]) value));
		} else if (value instanceof InputStream) {
			appendResource(formFieldName, new InputStreamResource((InputStream) value));
		} else if (value instanceof Reader) {
			appendResource(formFieldName, new InputStreamResource((Reader) value, this.charset));
		} else {
			appendResource(formFieldName,
				new StringResource(ConvertUtil.toStr(value), null, this.charset));
		}

		write(StrUtil.CRLF);
		return this;
	}

	@Override
	public void write(final int b) throws IOException {
		this.out.write(b);
	}

	/**
	 * 上传表单结束
	 *
	 * @throws IORuntimeException IO异常
	 */
	public void finish() throws IORuntimeException {
		if (!isFinish) {
			write(StrUtil.format("--{}--\r\n", boundary));
			this.isFinish = true;
		}
	}

	@Override
	public void close() {
		finish();
		IoUtil.closeQuietly(this.out);
	}

	/**
	 * 添加Multipart表单的Resource数据项，支持包括{@link HttpResource}资源格式
	 *
	 * @param formFieldName 表单名
	 * @param resource      资源
	 * @throws IORuntimeException IO异常
	 */
	private void appendResource(final String formFieldName, final Resource resource) throws IORuntimeException {
		final String fileName = resource.getName();

		// Content-Disposition
		if (null == fileName) {
			// Content-Disposition: form-data; name="参数名"[换行]
			write(StrUtil.format(CONTENT_DISPOSITION_TEMPLATE, formFieldName));
		} else {
			// Content-Disposition: form-data; name="参数名"; filename="文件名"[换行]
			write(StrUtil.format(CONTENT_DISPOSITION_FILE_TEMPLATE, formFieldName, fileName));
		}

		// Content-Type
		if (resource instanceof HttpResource) {
			final String contentType = ((HttpResource) resource).getContentType();
			if (StrUtil.isNotBlank(contentType)) {
				// Content-Type: 类型[换行]
				write(StrUtil.format(CONTENT_TYPE_FILE_TEMPLATE, contentType));
			}
		} else if (StrUtil.isNotEmpty(fileName)) {
			// 根据name的扩展名指定互联网媒体类型，默认二进制流数据
			write(StrUtil.format(CONTENT_TYPE_FILE_TEMPLATE,
				FileUtil.getMimeType(fileName, ContentType.OCTET_STREAM.getValue())));
		}

		// 内容
		write(StrUtil.CRLF);
		resource.writeTo(this);
	}

	/**
	 * part开始，写出:<br>
	 * <pre>
	 *     --分隔符(boundary)[换行]
	 * </pre>
	 */
	private void beginPart() {
		// --分隔符(boundary)[换行]
		write("--", boundary, StrUtil.CRLF);
	}

	/**
	 * 写出对象
	 *
	 * @param contents 写出的字符串
	 */
	private void write(final CharSequence... contents) {
		IoUtil.writeStrs(this, this.charset, false, contents);
	}
}
