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

package org.dromara.hutool.socket.aio;

import org.dromara.hutool.core.io.IORuntimeException;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.thread.ThreadFactoryBuilder;
import org.dromara.hutool.core.thread.ThreadUtil;
import org.dromara.hutool.log.Log;
import org.dromara.hutool.socket.SocketConfig;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;

/**
 * 基于AIO的Socket服务端实现
 *
 * @author Looly
 */
public class AioServer implements Closeable {
	private static final Log log = Log.get();
	private static final AcceptHandler ACCEPT_HANDLER = new AcceptHandler();

	private AsynchronousChannelGroup group;
	private AsynchronousServerSocketChannel channel;
	protected IoAction<ByteBuffer> ioAction;
	protected final SocketConfig config;


	/**
	 * 构造
	 *
	 * @param port 端口
	 */
	public AioServer(final int port) {
		this(new InetSocketAddress(port), new SocketConfig());
	}

	/**
	 * 构造
	 *
	 * @param address 地址
	 * @param config  {@link SocketConfig} 配置项
	 */
	@SuppressWarnings("resource")
	public AioServer(final InetSocketAddress address, final SocketConfig config) {
		this.config = config;
		init(address);
	}

	/**
	 * 初始化
	 *
	 * @param address 地址和端口
	 * @return this
	 */
	public AioServer init(final InetSocketAddress address) {
		try {
			this.group = AsynchronousChannelGroup.withFixedThreadPool(//
					config.getThreadPoolSize(), // 默认线程池大小
					ThreadFactoryBuilder.of().setNamePrefix("Hutool-socket-").build()//
			);
			this.channel = AsynchronousServerSocketChannel.open(group).bind(address);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
		return this;
	}

	/**
	 * 开始监听
	 *
	 * @param sync 是否阻塞
	 */
	public void start(final boolean sync) {
		doStart(sync);
	}

	/**
	 * 设置 Socket 的 Option 选项<br>
	 * 选项见：{@link java.net.StandardSocketOptions}
	 *
	 * @param <T>   选项泛型
	 * @param name  {@link SocketOption} 枚举
	 * @param value SocketOption参数
	 * @return this
	 * @throws IOException IO异常
	 */
	public <T> AioServer setOption(final SocketOption<T> name, final T value) throws IOException {
		this.channel.setOption(name, value);
		return this;
	}

	/**
	 * 获取IO处理器
	 *
	 * @return {@link IoAction}
	 */
	public IoAction<ByteBuffer> getIoAction() {
		return this.ioAction;
	}

	/**
	 * 设置IO处理器，单例存在
	 *
	 * @param ioAction {@link IoAction}
	 * @return this;
	 */
	public AioServer setIoAction(final IoAction<ByteBuffer> ioAction) {
		this.ioAction = ioAction;
		return this;
	}

	/**
	 * 获取{@link AsynchronousServerSocketChannel}
	 *
	 * @return {@link AsynchronousServerSocketChannel}
	 */
	public AsynchronousServerSocketChannel getChannel() {
		return this.channel;
	}

	/**
	 * 处理接入的客户端
	 *
	 * @return this
	 */
	public AioServer accept() {
		this.channel.accept(this, ACCEPT_HANDLER);
		return this;
	}

	/**
	 * 服务是否开启状态
	 *
	 * @return 服务是否开启状态
	 */
	public boolean isOpen() {
		return (null != this.channel) && this.channel.isOpen();
	}

	/**
	 * 关闭服务
	 */
	@Override
	public void close() {
		IoUtil.closeQuietly(this.channel);

		if (null != this.group && !this.group.isShutdown()) {
			try {
				this.group.shutdownNow();
			} catch (final IOException e) {
				// ignore
			}
		}

		// 结束阻塞
		synchronized (this) {
			this.notify();
		}
	}

	// ------------------------------------------------------------------------------------- Private method start

	/**
	 * 开始监听
	 *
	 * @param sync 是否阻塞
	 */
	@SuppressWarnings("resource")
	private void doStart(final boolean sync) {
		log.debug("Aio Server started, waiting for accept.");

		// 接收客户端连接
		accept();

		if (sync) {
			ThreadUtil.sync(this);
		}
	}
	// ------------------------------------------------------------------------------------- Private method end
}
