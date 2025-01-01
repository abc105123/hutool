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

package org.dromara.hutool.core.io.stream;

import org.dromara.hutool.core.collection.iter.ComputeIter;
import org.dromara.hutool.core.io.IORuntimeException;
import org.dromara.hutool.core.io.buffer.FastByteBuffer;
import org.dromara.hutool.core.text.CharUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.util.ObjUtil;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * 行读取器，类似于BufferedInputStream，支持多行转义，规则如下：<br>
 * <ul>
 *     <li>支持'\n'和'\r\n'两种换行符，不支持'\r'换行符</li>
 *     <li>如果想读取转义符，必须定义为'\\'</li>
 *     <li>多行转义后的换行符和空格都会被忽略</li>
 * </ul>
 * <p>
 * 例子：
 * <pre>
 * a=1\
 *   2
 * </pre>
 * 读出后就是{@code a=12}
 *
 * @author Looly
 * @since 6.0.0
 */
public class LineInputStream extends FilterInputStream implements Iterable<byte[]> {

	/**
	 * 构造
	 *
	 * @param in 输入流
	 */
	public LineInputStream(final InputStream in) {
		super(in);
	}

	/**
	 * 读取一行
	 *
	 * @param charset 编码
	 * @return 行
	 * @throws IORuntimeException IO异常
	 */
	public String readLine(final Charset charset) throws IORuntimeException {
		return StrUtil.str(readLine(), charset);
	}

	/**
	 * 读取一行
	 *
	 * @return 内容
	 * @throws IORuntimeException IO异常
	 */
	public byte[] readLine() throws IORuntimeException {
		try {
			return _readLine();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Iterator<byte[]> iterator() {
		return new ComputeIter<byte[]>() {
			@Override
			protected byte[] computeNext() {
				return readLine();
			}
		};
	}

	/**
	 * 读取一行
	 *
	 * @return 内容
	 * @throws IOException IO异常
	 */
	private byte[] _readLine() throws IOException {
		FastByteBuffer out = null;
		// 换行符前是否为转义符
		boolean precedingBackslash = false;
		int c;
		while ((c = read()) > 0) {
			if(null == out){
				out = new FastByteBuffer();
			}
			if (CharUtil.BACKSLASH == c) {
				// 转义符转义，行尾需要使用'\'时，使用转义符转义，即`\\`
				if (!precedingBackslash) {
					// 转义符，添加标识，但是不加入字符
					precedingBackslash = true;
					continue;
				} else {
					precedingBackslash = false;
				}
			} else {
				if (precedingBackslash) {
					// 转义模式下，跳过转义符后的所有空白符
					if (CharUtil.isBlankChar(c)) {
						continue;
					}
					// 遇到普通字符，关闭转义
					precedingBackslash = false;
				} else if (CharUtil.LF == c) {
					// 非转义状态下，表示行的结束
					// 如果换行符是`\r\n`，删除末尾的`\r`
					final int lastIndex = out.length() - 1;
					if (lastIndex >= 0 && CharUtil.CR == out.get(lastIndex)) {
						return out.toArray(0, lastIndex);
					}
					break;
				}
			}

			out.append((byte) c);
		}

		return ObjUtil.apply(out, FastByteBuffer::toArray);
	}
}
