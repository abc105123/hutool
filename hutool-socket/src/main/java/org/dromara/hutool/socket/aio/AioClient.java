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

import org.dromara.hutool.socket.ChannelUtil;
import org.dromara.hutool.socket.SocketConfig;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Aio Socket客户端
 *
 * @author Looly
 * @since 4.5.0
 */
public class AioClient implements Closeable {

	private final AioSession session;

	/**
	 * 构造
	 *
	 * @param address  地址
	 * @param ioAction IO处理类
	 */
	public AioClient(final InetSocketAddress address, final IoAction<ByteBuffer> ioAction) {
		this(address, ioAction, new SocketConfig());
	}

	/**
	 * 构造
	 *
	 * @param address  地址
	 * @param ioAction IO处理类
	 * @param config   配置项
	 */
	public AioClient(final InetSocketAddress address, final IoAction<ByteBuffer> ioAction, final SocketConfig config) {
		this(createChannel(address, config.getThreadPoolSize()), ioAction, config);
	}

	/**
	 * 构造
	 *
	 * @param channel  {@link AsynchronousSocketChannel}
	 * @param ioAction IO处理类
	 * @param config   配置项
	 */
	public AioClient(final AsynchronousSocketChannel channel, final IoAction<ByteBuffer> ioAction, final SocketConfig config) {
		this.session = new AioSession(channel, ioAction, config);
		ioAction.accept(this.session);
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
	public <T> AioClient setOption(final SocketOption<T> name, final T value) throws IOException {
		this.session.getChannel().setOption(name, value);
		return this;
	}

	/**
	 * 获取IO处理器
	 *
	 * @return {@link IoAction}
	 */
	public IoAction<ByteBuffer> getIoAction() {
		return this.session.getIoAction();
	}

	/**
	 * 从服务端读取数据
	 *
	 * @return this
	 */
	public AioClient read() {
		this.session.read();
		return this;
	}

	/**
	 * 写数据到服务端
	 *
	 * @param data 数据
	 * @return this
	 */
	public AioClient write(final ByteBuffer data) {
		this.session.write(data);
		return this;
	}

	/**
	 * 关闭客户端
	 */
	@Override
	public void close() {
		this.session.close();
	}

	// ------------------------------------------------------------------------------------- Private method start

	/**
	 * 初始化
	 *
	 * @param address  地址和端口
	 * @param poolSize 线程池大小
	 * @return this
	 */
	private static AsynchronousSocketChannel createChannel(final InetSocketAddress address, final int poolSize) {
		return ChannelUtil.connect(ChannelUtil.createFixedGroup(poolSize), address);
	}
	// ------------------------------------------------------------------------------------- Private method end
}
