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

package org.dromara.hutool.poi.ofd;

import org.dromara.hutool.core.io.IORuntimeException;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.io.file.PathUtil;
import org.ofdrw.font.Font;
import org.ofdrw.layout.OFDDoc;
import org.ofdrw.layout.edit.Annotation;
import org.ofdrw.layout.element.Div;
import org.ofdrw.layout.element.Img;
import org.ofdrw.layout.element.Paragraph;
import org.ofdrw.reader.OFDReader;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Path;

/**
 * OFD文件生成器
 *
 * @author Looly
 * @since 5.5.3
 */
public class OfdWriter implements Serializable, Closeable {
	private static final long serialVersionUID = 1L;

	private final OFDDoc doc;

	/**
	 * 构造
	 *
	 * @param file 生成的文件
	 */
	public OfdWriter(final File file) {
		this(file.toPath());
	}

	/**
	 * 构造
	 *
	 * @param file 生成的文件
	 */
	public OfdWriter(final Path file) {
		try {
			if(PathUtil.exists(file, true)){
				this.doc = new OFDDoc(new OFDReader(file), file);
			} else{
				this.doc = new OFDDoc(file);
			}
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 构造
	 *
	 * @param out 需要输出的流
	 */
	public OfdWriter(final OutputStream out) {
		this.doc = new OFDDoc(out);
	}

	/**
	 * 增加文本内容
	 *
	 * @param font  字体
	 * @param texts 文本
	 * @return this
	 */
	public OfdWriter addText(final Font font, final String... texts) {
		final Paragraph paragraph = new Paragraph();
		if (null != font) {
			paragraph.setDefaultFont(font);
		}
		for (final String text : texts) {
			paragraph.add(text);
		}
		return add(paragraph);
	}

	/**
	 * 追加图片
	 *
	 * @param picFile 图片文件
	 * @param width   宽度
	 * @param height  高度
	 * @return this
	 */
	public OfdWriter addPicture(final File picFile, final int width, final int height) {
		return addPicture(picFile.toPath(), width, height);
	}

	/**
	 * 追加图片
	 *
	 * @param picFile 图片文件
	 * @param width   宽度
	 * @param height  高度
	 * @return this
	 */
	public OfdWriter addPicture(final Path picFile, final int width, final int height) {
		final Img img;
		try {
			img = new Img(width, height, picFile);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
		return add(img);
	}

	/**
	 * 增加节点
	 *
	 * @param div 节点，可以是段落、Canvas、Img或者填充
	 * @return this
	 */
	@SuppressWarnings("rawtypes")
	public OfdWriter add(final Div div) {
		this.doc.add(div);
		return this;
	}

	/**
	 * 增加注释，比如水印等
	 *
	 * @param page 页码
	 * @param annotation 节点，可以是段落、Canvas、Img或者填充
	 * @return this
	 */
	public OfdWriter add(final int page, final Annotation annotation) {
		try {
			this.doc.addAnnotation(page, annotation);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
		return this;
	}

	@Override
	public void close() {
		IoUtil.closeQuietly(this.doc);
	}
}
