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

package org.dromara.hutool.socket;

import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.util.RuntimeUtil;

import java.io.Serializable;

/**
 * Socket通讯配置
 *
 * @author Looly
 *
 */
public class SocketConfig implements Serializable{
	private static final long serialVersionUID = 1L;

	/** CPU核心数 */
	private static final int CPU_COUNT = RuntimeUtil.getProcessorCount();

	/** 共享线程池大小，此线程池用于接收和处理用户连接 */
	private int threadPoolSize = CPU_COUNT;

	/** 读取超时时长，小于等于0表示默认 */
	private long readTimeout;
	/** 写出超时时长，小于等于0表示默认 */
	private long writeTimeout;

	/** 读取缓存大小 */
	private int readBufferSize = IoUtil.DEFAULT_BUFFER_SIZE;
	/** 写出缓存大小 */
	private int writeBufferSize = IoUtil.DEFAULT_BUFFER_SIZE;

	/**
	 * 获取共享线程池大小，此线程池用于接收和处理用户连接
	 *
	 * @return 共享线程池大小，此线程池用于接收和处理用户连接
	 */
	public int getThreadPoolSize() {
		return threadPoolSize;
	}

	/**
	 * 设置共享线程池大小，此线程池用于接收和处理用户连接
	 *
	 * @param threadPoolSize 共享线程池大小，此线程池用于接收和处理用户连接
	 */
	public void setThreadPoolSize(final int threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
	}

	/**
	 * 获取读取超时时长，小于等于0表示默认
	 *
	 * @return 读取超时时长，小于等于0表示默认
	 */
	public long getReadTimeout() {
		return readTimeout;
	}

	/**
	 * 设置读取超时时长，小于等于0表示默认
	 *
	 * @param readTimeout 读取超时时长，小于等于0表示默认
	 */
	public void setReadTimeout(final long readTimeout) {
		this.readTimeout = readTimeout;
	}

	/**
	 * 获取写出超时时长，小于等于0表示默认
	 *
	 * @return 写出超时时长，小于等于0表示默认
	 */
	public long getWriteTimeout() {
		return writeTimeout;
	}

	/**
	 * 设置写出超时时长，小于等于0表示默认
	 *
	 * @param writeTimeout 写出超时时长，小于等于0表示默认
	 */
	public void setWriteTimeout(final long writeTimeout) {
		this.writeTimeout = writeTimeout;
	}

	/**
	 * 获取读取缓存大小
	 * @return 读取缓存大小
	 */
	public int getReadBufferSize() {
		return readBufferSize;
	}

	/**
	 * 设置读取缓存大小
	 * @param readBufferSize 读取缓存大小
	 */
	public void setReadBufferSize(final int readBufferSize) {
		this.readBufferSize = readBufferSize;
	}

	/**
	 * 获取写出缓存大小
	 * @return 写出缓存大小
	 */
	public int getWriteBufferSize() {
		return writeBufferSize;
	}

	/**
	 * 设置写出缓存大小
	 * @param writeBufferSize 写出缓存大小
	 */
	public void setWriteBufferSize(final int writeBufferSize) {
		this.writeBufferSize = writeBufferSize;
	}
}
