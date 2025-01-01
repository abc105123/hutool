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

import org.dromara.hutool.core.compress.InflaterInputStream;
import org.dromara.hutool.core.map.CaseInsensitiveMap;
import org.dromara.hutool.core.reflect.ConstructorUtil;

import java.io.InputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * 全局响应内容压缩解压器注册中心<br>
 * 通过注册指定Accept-Encoding的流，来包装响应内容流，从而支持特殊压缩算法
 *
 * @author Looly
 * @since 6.0.0
 */
public enum GlobalCompressStreamRegister {
	/**
	 * 单例对象
	 */
	INSTANCE;

	/**
	 * 存储内容压缩流信息
	 */
	private final Map<String, Class<? extends InputStream>> compressMap = new CaseInsensitiveMap<>();

	/**
	 * 构造，初始化默认的压缩算法
	 */
	GlobalCompressStreamRegister() {
		compressMap.put("gzip", GZIPInputStream.class);
		compressMap.put("deflate", InflaterInputStream.class);
	}

	/**
	 * 包装原始响应流为指定压缩算法解压流
	 *
	 * @param in 原始响应流
	 * @param contentEncoding 压缩编码，如gzip等
	 * @return 包装后的响应流
	 */
	public InputStream wrapStream(final InputStream in, final String contentEncoding){
		final Class<? extends InputStream> streamClass = get(contentEncoding);
		if (null != streamClass) {
			try {
				return ConstructorUtil.newInstance(streamClass, in);
			} catch (final Exception ignore) {
				// 对于构造错误的压缩算法，跳过之
			}
		}

		return in;
	}

	/**
	 * 获取解压器
	 *
	 * @param contentEncoding Accept-Encoding名称，如gzip、defalte、br等，不区分大小写
	 * @return 解压器
	 */
	public Class<? extends InputStream> get(final String contentEncoding) {
		return compressMap.get(contentEncoding);
	}

	/**
	 * 注册解压器
	 *
	 * @param contentEncoding Accept-Encoding名称，如gzip、defalte、br等，不区分大小写
	 * @param streamClass     解压类
	 */
	synchronized public void register(final String contentEncoding, final Class<? extends InputStream> streamClass) {
		this.compressMap.put(contentEncoding, streamClass);
	}

	/**
	 * 注销压缩器
	 *
	 * @param contentEncoding Accept-Encoding名称，如gzip、defalte、br等，不区分大小写
	 */
	synchronized public void unRegister(final String contentEncoding) {
		this.compressMap.remove(contentEncoding);
	}
}
