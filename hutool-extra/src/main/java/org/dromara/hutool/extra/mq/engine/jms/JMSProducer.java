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

package org.dromara.hutool.extra.mq.engine.jms;

import jakarta.jms.*;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.extra.mq.MQException;
import org.dromara.hutool.extra.mq.Message;
import org.dromara.hutool.extra.mq.Producer;

import java.io.IOException;

/**
 * ActiveMQ消息生产者
 *
 * @author Looly
 * @since 6.0.0
 */
public class JMSProducer implements Producer {

	private final Session session;
	private MessageProducer producer;

	/**
	 * 构造
	 *
	 * @param session Session
	 */
	public JMSProducer(final Session session) {
		this.session = session;
	}

	/**
	 * 设置主题
	 *
	 * @param topic 主题
	 * @return this
	 */
	public JMSProducer setTopic(final String topic) {
		final Destination destination = createDestination(topic, DestinationType.TOPIC);
		this.producer = createProducer(destination);
		return this;
	}

	/**
	 * 设置队列
	 *
	 * @param queue 队列
	 * @return this
	 */
	public JMSProducer setQueue(final String queue) {
		final Destination destination = createDestination(queue, DestinationType.QUEUE);
		this.producer = createProducer(destination);
		return this;
	}

	@Override
	public void send(final Message message) {
		try {
			final BytesMessage bytesMessage = session.createBytesMessage();
			bytesMessage.writeBytes(message.content());
			this.producer.send(bytesMessage);
		} catch (final JMSException e) {
			throw new MQException(e);
		}
	}

	@Override
	public void close() throws IOException {
		IoUtil.closeQuietly(this.producer);
		IoUtil.closeQuietly(this.session);
	}

	/**
	 * 创建消息生产者
	 *
	 * @param destination 目的地
	 * @return this
	 */
	private MessageProducer createProducer(final Destination destination) {
		try {
			return session.createProducer(destination);
		} catch (final JMSException e) {
			throw new MQException(e);
		}
	}

	/**
	 * 创建消息目的地
	 *
	 * @param name   消息目的地名称
	 * @param type   消息目的地类型
	 * @return this
	 */
	private Destination createDestination(final String name, final DestinationType type) {
		try {
			switch (type){
				case QUEUE:
					return session.createQueue(name);
				case TOPIC:
					return session.createTopic(name);
				default:
					throw new MQException("Unknown destination type: " + type);
			}
		} catch (final JMSException e) {
			throw new MQException(e);
		}
	}
}
