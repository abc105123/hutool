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

package org.dromara.hutool.extra.mq.engine.activemq;

import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
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
public class ActiveMQProducer implements Producer {

	private final Session session;
	private MessageProducer producer;

	/**
	 * 构造
	 *
	 * @param session Session
	 */
	public ActiveMQProducer(final Session session) {
		this.session = session;
	}

	/**
	 * 设置主题
	 *
	 * @param topic 主题
	 * @return this
	 */
	public ActiveMQProducer setTopic(final String topic) {
		try {
			this.producer = this.session.createProducer(this.session.createTopic(topic));
		} catch (final JMSException e) {
			throw new MQException(e);
		}
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
}
