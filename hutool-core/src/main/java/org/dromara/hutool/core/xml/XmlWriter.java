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

package org.dromara.hutool.core.xml;

import org.dromara.hutool.core.exception.HutoolException;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.util.CharsetUtil;
import org.dromara.hutool.core.util.ObjUtil;
import org.w3c.dom.Node;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * XML生成器
 *
 * @author Looly
 * @since 6.0.0
 */
public class XmlWriter {

	/**
	 * 构建XmlWriter
	 *
	 * @param node {@link Node} XML文档节点或文档本身
	 * @return XmlWriter
	 */
	public static XmlWriter of(final Node node) {
		return of(new DOMSource(node));
	}

	/**
	 * 构建XmlWriter
	 *
	 * @param source XML数据源
	 * @return XmlWriter
	 */
	public static XmlWriter of(final Source source) {
		return new XmlWriter(source);
	}

	private final Source source;

	private Charset charset = CharsetUtil.UTF_8;
	private int indent;
	private boolean omitXmlDeclaration;

	/**
	 * 构造
	 *
	 * @param source XML数据源
	 */
	public XmlWriter(final Source source) {
		this.source = source;
	}

	/**
	 * 设置编码
	 *
	 * @param charset 编码，null跳过
	 * @return this
	 */
	public XmlWriter setCharset(final Charset charset) {
		if (null != charset) {
			this.charset = charset;
		}
		return this;
	}

	/**
	 * 设置缩进
	 *
	 * @param indent 缩进
	 * @return this
	 */
	public XmlWriter setIndent(final int indent) {
		this.indent = indent;
		return this;
	}

	/**
	 * 设置是否输出 xml Declaration
	 *
	 * @param omitXmlDeclaration 是否输出 xml Declaration
	 * @return this
	 */
	public XmlWriter setOmitXmlDeclaration(final boolean omitXmlDeclaration) {
		this.omitXmlDeclaration = omitXmlDeclaration;
		return this;
	}

	/**
	 * 获得XML字符串
	 *
	 * @return XML字符串
	 */
	public String getStr(){
		final StringWriter writer = StrUtil.getWriter();
		write(writer);
		return writer.toString();
	}

	/**
	 * 将XML文档写出
	 *
	 * @param file 目标
	 */
	public void write(final File file) {
		write(new StreamResult(file));
	}

	/**
	 * 将XML文档写出
	 *
	 * @param writer 目标
	 */
	public void write(final Writer writer) {
		write(new StreamResult(writer));
	}

	/**
	 * 将XML文档写出
	 *
	 * @param out 目标
	 */
	public void write(final OutputStream out) {
		write(new StreamResult(out));
	}

	/**
	 * 将XML文档写出<br>
	 * 格式化输出逻辑参考：https://stackoverflow.com/questions/139076/how-to-pretty-print-xml-from-java
	 *
	 * @param result 目标
	 */
	public void write(final Result result) {
		final TransformerFactory factory = XXEUtil.disableXXE(TransformerFactory.newInstance());
		try {
			final Transformer xformer = factory.newTransformer();
			if (indent > 0) {
				xformer.setOutputProperty(OutputKeys.INDENT, "yes");
				//fix issue#1232@Github
				xformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
				xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
			}
			if (ObjUtil.isNotNull(this.charset)) {
				xformer.setOutputProperty(OutputKeys.ENCODING, charset.name());
			}
			if (omitXmlDeclaration) {
				xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			}
			xformer.transform(source, result);
		} catch (final Exception e) {
			throw new HutoolException(e, "Trans xml document to string error!");
		}
	}
}
