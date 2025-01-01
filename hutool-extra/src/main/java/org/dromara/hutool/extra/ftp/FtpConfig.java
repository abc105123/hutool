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

package org.dromara.hutool.extra.ftp;

import org.dromara.hutool.extra.ssh.Connector;

import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * FTP配置项，提供FTP各种参数信息
 *
 * @author Looly
 */
public class FtpConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 创建默认配置
	 *
	 * @return FtpConfig
	 */
	public static FtpConfig of() {
		return new FtpConfig();
	}

	private Connector connector;
	/**
	 * 编码
	 */
	private Charset charset;

	/**
	 * Socket连接超时时长，单位毫秒
	 */
	private long soTimeout;

	/**
	 * 服务器语言
	 */
	private String serverLanguageCode;

	/**
	 * 服务器系统关键词
	 */
	private String systemKey;

	/**
	 * 构造
	 */
	public FtpConfig() {
	}

	/**
	 * 构造
	 *
	 * @param connector 连接信息，包括host、port、user、password等信息
	 * @param charset   编码
	 */
	public FtpConfig(final Connector connector, final Charset charset) {
		this(connector, charset, null, null);
	}

	/**
	 * 构造
	 *
	 * @param connector          连接信息，包括host、port、user、password等信息
	 * @param charset            编码
	 * @param serverLanguageCode 服务器语言
	 * @param systemKey          系统关键字
	 */
	public FtpConfig(final Connector connector, final Charset charset, final String serverLanguageCode, final String systemKey) {
		this.connector = connector;
		this.charset = charset;
		this.serverLanguageCode = serverLanguageCode;
		this.systemKey = systemKey;
	}

	/**
	 * 获取连接信息
	 *
	 * @return 连接信息
	 */
	public Connector getConnector() {
		return connector;
	}

	/**
	 * 设置连接信息
	 *
	 * @param connector 连接信息
	 * @return this
	 */
	public FtpConfig setConnector(final Connector connector) {
		this.connector = connector;
		return this;
	}

	/**
	 * 设置超时，注意此方法会调用{@link Connector#setTimeout(long)}<br>
	 * 此方法需在{@link #setConnector(Connector)}后调用，否则会创建空的Connector
	 * @param timeout 链接超时
	 * @return this
	 */
	public FtpConfig setConnectionTimeout(final long timeout){
		if(null == connector){
			connector = Connector.of();
		}
		connector.setTimeout(timeout);
		return this;
	}

	/**
	 * 获取编码
	 *
	 * @return 编码
	 */
	public Charset getCharset() {
		return charset;
	}

	/**
	 * 设置编码
	 *
	 * @param charset 编码
	 * @return this
	 */
	public FtpConfig setCharset(final Charset charset) {
		this.charset = charset;
		return this;
	}

	/**
	 * 获取读取数据超时时间
	 *
	 * @return 读取数据超时时间
	 */
	public long getSoTimeout() {
		return soTimeout;
	}

	/**
	 * 设置读取数据超时时间
	 *
	 * @param soTimeout 读取数据超时时间
	 * @return this
	 */
	public FtpConfig setSoTimeout(final long soTimeout) {
		this.soTimeout = soTimeout;
		return this;
	}

	/**
	 * 获取服务器语言
	 *
	 * @return 服务器语言
	 */
	public String getServerLanguageCode() {
		return serverLanguageCode;
	}

	/**
	 * 设置服务器语言
	 *
	 * @param serverLanguageCode 服务器语言
	 * @return this
	 */
	public FtpConfig setServerLanguageCode(final String serverLanguageCode) {
		this.serverLanguageCode = serverLanguageCode;
		return this;
	}

	/**
	 * 获取服务器系统关键词
	 *
	 * @return 服务器系统关键词
	 */
	public String getSystemKey() {
		return systemKey;
	}

	/**
	 * 设置服务器系统关键词
	 *
	 * @param systemKey 服务器系统关键词
	 * @return this
	 */
	public FtpConfig setSystemKey(final String systemKey) {
		this.systemKey = systemKey;
		return this;
	}
}
