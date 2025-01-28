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

package org.dromara.hutool.extra.mq.engine.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.extra.mq.Consumer;
import org.dromara.hutool.extra.mq.MQConfig;
import org.dromara.hutool.extra.mq.MQException;
import org.dromara.hutool.extra.mq.Producer;
import org.dromara.hutool.extra.mq.engine.MQEngine;

/**
 * RocketMQ引擎
 *
 * @author Looly
 * @since 6.0.0
 */
public class RocketMQEngine implements MQEngine {

	private MQConfig config;

	/**
	 * 默认构造
	 */
	public RocketMQEngine() {
		// SPI方式加载时检查库是否引入
		Assert.notNull( org.apache.rocketmq.common.message.Message.class);
	}

	@Override
	public RocketMQEngine init(final MQConfig config) {
		this.config = config;
		return this;
	}

	@Override
	public Producer getProducer() {
		final DefaultMQProducer defaultMQProducer = new DefaultMQProducer();
		defaultMQProducer.setNamesrvAddr(config.getBrokerUrl());
		try {
			defaultMQProducer.start();
		} catch (final MQClientException e) {
			throw new MQException(e);
		}
		return new RocketMQProducer(defaultMQProducer);
	}

	@Override
	public Consumer getConsumer() {
		final DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer();
		defaultMQPushConsumer.setNamesrvAddr(config.getBrokerUrl());
		return new RocketMQConsumer(defaultMQPushConsumer);
	}
}
