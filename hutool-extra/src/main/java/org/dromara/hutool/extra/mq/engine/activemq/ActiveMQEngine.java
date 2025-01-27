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
import org.apache.activemq.ActiveMQConnectionFactory;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.extra.mq.MQConfig;
import org.dromara.hutool.extra.mq.engine.jms.JMSEngine;

/**
 * ActiveMQ引擎
 *
 * @author Looly
 * @since 6.0.0
 */
public class ActiveMQEngine extends JMSEngine {

	/**
	 * 默认构造
	 */
	public ActiveMQEngine() {
		super((Connection) null);
		// SPI方式加载时检查库是否引入
		Assert.notNull(org.apache.activemq.ActiveMQConnectionFactory.class);
	}

	/**
	 * 构造
	 *
	 * @param config 配置
	 */
	public ActiveMQEngine(final MQConfig config) {
		super(createFactory(config));
	}

	/**
	 * 构造
	 *
	 * @param factory {@link ConnectionFactory}
	 */
	public ActiveMQEngine(final ActiveMQConnectionFactory factory) {
		super(factory);
	}

	@Override
	public ActiveMQEngine init(final MQConfig config) {
		super.init(createFactory(config));
		return this;
	}

	/**
	 * 创建{@link ActiveMQConnectionFactory}
	 *
	 * @param config 配置
	 * @return {@link ActiveMQConnectionFactory}
	 */
	private static ActiveMQConnectionFactory createFactory(final MQConfig config) {
		final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
		factory.setBrokerURL(config.getBrokerUrl());

		// TODO 配置其他参数

		return factory;
	}
}
