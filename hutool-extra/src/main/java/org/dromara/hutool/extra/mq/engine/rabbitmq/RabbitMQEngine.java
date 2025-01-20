/*
 * Copyright (c) 2025 Hutool Team and hutool.cn
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

package org.dromara.hutool.extra.mq.engine.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.extra.mq.Consumer;
import org.dromara.hutool.extra.mq.MQException;
import org.dromara.hutool.extra.mq.Producer;
import org.dromara.hutool.extra.mq.engine.MQEngine;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ引擎
 *
 * @author Looly
 * @since 6.0.0
 */
public class RabbitMQEngine implements MQEngine, Closeable {

	private final Connection connection;

	/**
	 * 构造
	 *
	 * @param factory 连接工厂
	 */
	public RabbitMQEngine(final ConnectionFactory factory) {
		try {
			this.connection = factory.newConnection();
		} catch (final IOException | TimeoutException e) {
			throw new MQException(e);
		}
	}

	@Override
	public Producer getProducer() {
		return new RabbitMQProducer(createChannel());
	}

	@Override
	public Consumer getConsumer() {
		return new RabbitMQConsumer(createChannel());
	}

	@Override
	public void close() throws IOException {
		IoUtil.nullSafeClose(this.connection);
	}

	/**
	 * 创建Channel
	 *
	 * @return Channel
	 */
	private Channel createChannel() {
		try {
			return this.connection.createChannel();
		} catch (final IOException e) {
			throw new MQException(e);
		}
	}
}
