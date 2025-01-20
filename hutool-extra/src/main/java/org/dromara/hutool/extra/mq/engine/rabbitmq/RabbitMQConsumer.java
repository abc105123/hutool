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
import com.rabbitmq.client.DeliverCallback;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.extra.mq.Consumer;
import org.dromara.hutool.extra.mq.MQException;
import org.dromara.hutool.extra.mq.Message;
import org.dromara.hutool.extra.mq.MessageHandler;

import java.io.IOException;

/**
 * RabbitMQ消费者
 *
 * @author Looly
 * @since 6.0.0
 */
public class RabbitMQConsumer implements Consumer {

	private final Channel channel;
	private String topic;

	/**
	 * 构造
	 *
	 * @param channel Channel
	 */
	public RabbitMQConsumer(final Channel channel) {
		this.channel = channel;
	}

	/**
	 * 设置队列（主题）
	 *
	 * @param topic 队列名
	 * @return this
	 */
	public RabbitMQConsumer setTopic(final String topic) {
		this.topic = topic;
		return this;
	}

	@Override
	public void subscribe(final MessageHandler messageHandler) {
		try {
			this.channel.queueDeclare(this.topic, false, false, false, null);
		} catch (final IOException e) {
			throw new MQException(e);
		}

		final DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			messageHandler.handle(new Message() {
				@Override
				public String topic() {
					return consumerTag;
				}

				@Override
				public byte[] content() {
					return delivery.getBody();
				}
			});
		};

		try {
			this.channel.basicConsume(this.topic, true, deliverCallback, consumerTag -> { });
		} catch (final IOException e) {
			throw new MQException(e);
		}
	}

	@Override
	public void close() {
		IoUtil.closeQuietly(this.channel);
	}
}
