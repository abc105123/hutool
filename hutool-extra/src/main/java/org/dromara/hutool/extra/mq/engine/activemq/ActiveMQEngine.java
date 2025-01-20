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

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.extra.mq.Consumer;
import org.dromara.hutool.extra.mq.MQException;
import org.dromara.hutool.extra.mq.Producer;
import org.dromara.hutool.extra.mq.engine.MQEngine;

import java.io.Closeable;
import java.io.IOException;

/**
 * ActiveMQ引擎
 *
 * @author Looly
 * @since 6.0.0
 */
public class ActiveMQEngine implements MQEngine, Closeable {

	private final Connection connection;

	/**
	 * 构造
	 *
	 * @param factory {@link ConnectionFactory}
	 */
	public ActiveMQEngine(final ConnectionFactory factory) {
		try {
			this.connection = factory.createConnection();
			this.connection.start();
		} catch (final JMSException e) {
			throw new MQException(e);
		}
	}

	@Override
	public Producer getProducer() {
		return new ActiveMQProducer(createSession());
	}

	@Override
	public Consumer getConsumer() {
		return new ActiveMQConsumer(createSession());
	}

	private Session createSession() {
		try {
			return this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (final JMSException e) {
			throw new MQException(e);
		}
	}

	@Override
	public void close() throws IOException {
		IoUtil.closeQuietly(this.connection);
	}
}
