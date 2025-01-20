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
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.extra.mq.MQException;
import org.dromara.hutool.extra.mq.Message;
import org.dromara.hutool.extra.mq.Producer;

import java.io.IOException;

/**
 * RabbitMQ消息生产者
 *
 * @author Looly
 * @since 6.0.0
 */
public class RabbitMQProducer implements Producer {

	private final Channel channel;

	/**
	 * 构造
	 *
	 * @param channel Channel
	 */
	public RabbitMQProducer(final Channel channel) {
		this.channel = channel;
	}

	@Override
	public void send(final Message message) {
		try {
			this.channel.basicPublish(StrUtil.EMPTY, message.topic(), null, message.content());
		} catch (final IOException e) {
			throw new MQException(e);
		}
	}

	@Override
	public void close() {
		IoUtil.closeQuietly(this.channel);
	}
}
