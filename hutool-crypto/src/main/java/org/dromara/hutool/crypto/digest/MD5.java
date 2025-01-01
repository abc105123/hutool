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

package org.dromara.hutool.crypto.digest;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * MD5算法
 *
 * @author Looly
 * @since 4.4.3
 */
public class MD5 extends Digester {
	private static final long serialVersionUID = 1L;

	// issue#I6ZIQH
	// MD5算法不使用BC库，使用JDK默认以提高初始性能
	private static final DigesterFactory FACTORY = DigesterFactory.ofJdk(DigestAlgorithm.MD5.getValue());

	/**
	 * 创建MD5实例
	 *
	 * @return MD5
	 * @since 4.6.0
	 */
	public static MD5 of() {
		return new MD5();
	}

	/**
	 * 构造
	 */
	public MD5() {
		super(FACTORY.createMessageDigester());
	}

	/**
	 * 构造
	 *
	 * @param salt 盐值
	 */
	public MD5(final byte[] salt) {
		this(salt, 0, 1);
	}

	/**
	 * 构造
	 *
	 * @param salt        盐值
	 * @param digestCount 摘要次数，当此值小于等于1,默认为1。
	 */
	public MD5(final byte[] salt, final int digestCount) {
		this(salt, 0, digestCount);
	}

	/**
	 * 构造
	 *
	 * @param salt         盐值
	 * @param saltPosition 加盐位置，即将盐值字符串放置在数据的index数，默认0
	 * @param digestCount  摘要次数，当此值小于等于1,默认为1。
	 */
	public MD5(final byte[] salt, final int saltPosition, final int digestCount) {
		this();
		this.salt = salt;
		this.saltPosition = saltPosition;
		this.digestCount = digestCount;
	}

	/**
	 * 生成16位MD5摘要
	 *
	 * @param data    数据
	 * @param charset 编码
	 * @return 16位MD5摘要
	 * @since 4.6.0
	 */
	public String digestHex16(final String data, final Charset charset) {
		return DigestUtil.md5HexTo16(digestHex(data, charset));
	}

	/**
	 * 生成16位MD5摘要
	 *
	 * @param data 数据
	 * @return 16位MD5摘要
	 * @since 4.5.1
	 */
	public String digestHex16(final String data) {
		return DigestUtil.md5HexTo16(digestHex(data));
	}

	/**
	 * 生成16位MD5摘要
	 *
	 * @param data 数据
	 * @return 16位MD5摘要
	 * @since 4.5.1
	 */
	public String digestHex16(final InputStream data) {
		return DigestUtil.md5HexTo16(digestHex(data));
	}

	/**
	 * 生成16位MD5摘要
	 *
	 * @param data 数据
	 * @return 16位MD5摘要
	 */
	public String digestHex16(final File data) {
		return DigestUtil.md5HexTo16(digestHex(data));
	}

	/**
	 * 生成16位MD5摘要
	 *
	 * @param data 数据
	 * @return 16位MD5摘要
	 * @since 4.5.1
	 */
	public String digestHex16(final byte[] data) {
		return DigestUtil.md5HexTo16(digestHex(data));
	}
}
