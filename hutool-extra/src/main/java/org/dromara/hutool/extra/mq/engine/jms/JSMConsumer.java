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
import org.dromara.hutool.extra.mq.Consumer;
import org.dromara.hutool.extra.mq.MQException;
import org.dromara.hutool.extra.mq.MessageHandler;

import java.io.IOException;

/**
 * ActiveMQ消息消费者
 *
 * @author Looly
 * @since 6.0.0
 */
public class JSMConsumer implements Consumer {

	private final Session session;
	private MessageConsumer consumer;

	/**
	 * 构造
	 *
	 * @param session Session
	 */
	public JSMConsumer(final Session session) {
		this.session = session;
	}

	/**
	 * 设置主题
	 *
	 * @param topic 主题
	 * @return this
	 */
	public JSMConsumer setTopic(final String topic) {
		try {
			this.consumer = this.session.createConsumer(this.session.createTopic(topic));
		} catch (final JMSException e) {
			throw new MQException(e);
		}
		return this;
	}

	@Override
	public void subscribe(final MessageHandler messageHandler) {
		final Message message;
		try{
			message = consumer.receive(3000);
		} catch (final JMSException e){
			throw new MQException(e);
		}

		messageHandler.handle(new JMSMessage(message));
	}

	@Override
	public void listen(final MessageHandler messageHandler) {
		try {
			consumer.setMessageListener(message -> messageHandler.handle(new JMSMessage(message)));
		} catch (final JMSException e) {
			throw new MQException(e);
		}
	}

	@Override
	public void close() throws IOException {
		IoUtil.closeQuietly(this.consumer);
		IoUtil.closeQuietly(this.session);
	}

	/**
	 * JMS消息包装
	 */
	private static class JMSMessage implements org.dromara.hutool.extra.mq.Message{

		private final Message message;

		private JMSMessage(final Message message) {
			this.message = message;
		}

		@Override
		public String topic() {
			try {
				return message.getJMSDestination().toString();
			} catch (final JMSException e) {
				throw new MQException(e);
			}
		}

		@Override
		public byte[] content() {
			try {
				return message.getBody(byte[].class);
			} catch (final JMSException e) {
				throw new MQException(e);
			}
		}
	}
}
