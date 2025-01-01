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

import org.dromara.hutool.core.util.RandomUtil;

import java.io.Serializable;
import java.net.HttpURLConnection;

/**
 * HTTP 全局参数配置
 *
 * @author Looly
 * @since 4.6.2
 */
public class HttpGlobalConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * -1: 含义，永不超时。
	 * 如果：设置timeout = 3s(3000 ms), 那一次请求最大超时：就是：6s
	 * 官方含义：timeout of zero is interpreted as an infinite timeout. （0的超时被解释为无限超时。）
	 * 这里实际项目一定要进行修改，防止把系统拖死.
	 * 底层调用：{@link HttpURLConnection#setReadTimeout(int)} 同时设置: 读取超时
	 * 底层调用：{@link HttpURLConnection#setConnectTimeout(int)} 同时设置: 连接超时
	 */
	private static int timeout = -1;
	private static String boundary = "--------------------Hutool_" + RandomUtil.randomStringLower(16);
	private static int maxRedirects = 0;
	private static boolean ignoreEOFError = true;
	/**
	 * 是否从响应正文中的meta标签获取编码信息
	 */
	private static boolean getCharsetFromContent = true;
	private static boolean trustAnyHost = false;

	/**
	 * 获取全局默认的超时时长
	 *
	 * @return 全局默认的超时时长
	 */
	public static int getTimeout() {
		return timeout;
	}

	/**
	 * 设置默认的连接和读取超时时长<br>
	 * -1: 含义，永不超时。<br>
	 * 如果：设置timeout = 3s(3000 ms), 那一次请求最大超时：就是：6s<br>
	 * 官方含义：timeout of zero is interpreted as an infinite timeout. （0的超时被解释为无限超时。）<br>
	 * 这里实际项目一定要进行修改，防止把系统拖死.<br>
	 * 底层调用：{@link HttpURLConnection#setReadTimeout(int)} 同时设置: 读取超时<br>
	 * 底层调用：{@link HttpURLConnection#setConnectTimeout(int)} 同时设置: 连接超时
	 *
	 * @param customTimeout 超时时长
	 */
	synchronized public static void setTimeout(final int customTimeout) {
		timeout = customTimeout;
	}

	/**
	 * 获取全局默认的Multipart边界
	 *
	 * @return 全局默认的Multipart边界
	 * @since 5.7.17
	 */
	public static String getBoundary() {
		return boundary;
	}

	/**
	 * 设置默认的Multipart边界
	 *
	 * @param customBoundary 自定义Multipart边界
	 * @since 5.7.17
	 */
	synchronized public static void setBoundary(final String customBoundary) {
		boundary = customBoundary;
	}

	/**
	 * 获取全局默认的最大重定向次数，如设置0表示不重定向<br>
	 * 如果设置为1，表示重定向一次，即请求两次
	 *
	 * @return 全局默认的最大重定向次数
	 */
	public static int getMaxRedirects() {
		return maxRedirects;
	}

	/**
	 * 设置默认全局默认的最大重定向次数，如设置0表示不重定向<br>
	 * 如果设置为1，表示重定向一次，即请求两次
	 *
	 * @param customMaxRedirectCount 全局默认的最大重定向次数
	 * @since 5.7.19
	 */
	synchronized public static void setMaxRedirects(final int customMaxRedirectCount) {
		maxRedirects = customMaxRedirectCount;
	}

	/**
	 * 获取是否忽略响应读取时可能的EOF异常。<br>
	 * 在Http协议中，对于Transfer-Encoding: Chunked在正常情况下末尾会写入一个Length为0的的chunk标识完整结束。<br>
	 * 如果服务端未遵循这个规范或响应没有正常结束，会报EOF异常，此选项用于是否忽略这个异常。
	 *
	 * @return 是否忽略响应读取时可能的EOF异常
	 * @since 5.7.20
	 */
	public static boolean isIgnoreEOFError() {
		return ignoreEOFError;
	}

	/**
	 * 设置是否忽略响应读取时可能的EOF异常。<br>
	 * 在Http协议中，对于Transfer-Encoding: Chunked在正常情况下末尾会写入一个Length为0的的chunk标识完整结束。<br>
	 * 如果服务端未遵循这个规范或响应没有正常结束，会报EOF异常，此选项用于是否忽略这个异常。
	 *
	 * @param customIgnoreEOFError 是否忽略响应读取时可能的EOF异常。
	 * @since 5.7.20
	 */
	synchronized public static void setIgnoreEOFError(final boolean customIgnoreEOFError) {
		ignoreEOFError = customIgnoreEOFError;
	}

	/**
	 * 设置是否从响应正文中的meta标签获取编码信息
	 *
	 * @param customGetCharsetFromContent 是否从响应正文中的meta标签获取编码信息
	 * @since 6.0.0
	 */
	synchronized public static void setGetCharsetFromContent(final boolean customGetCharsetFromContent){
		getCharsetFromContent = customGetCharsetFromContent;
	}

	/**
	 * 是否从响应正文中的meta标签获取编码信息
	 *
	 * @return 是否从响应正文中的meta标签获取编码信息
	 * @since 6.0.0
	 */
	public static boolean isGetCharsetFromContent(){
		return getCharsetFromContent;
	}

	/**
	 * 是否信任所有Host
	 * @return 是否信任所有Host
	 * @since 5.8.27
	 */
	public static boolean isTrustAnyHost(){
		return trustAnyHost;
	}

	/**
	 * 是否信任所有Host<br>
	 * 见：https://github.com/dromara/hutool/issues/2042<br>
	 *
	 * @param customTrustAnyHost 如果设置为{@code false}，则按照JDK默认验证机制，验证目标服务器的证书host和请求host是否一致，{@code true}表示不验证。
	 * @since 5.8.27
	 */
	public static void setTrustAnyHost(final boolean customTrustAnyHost) {
		trustAnyHost = customTrustAnyHost;
	}
}
