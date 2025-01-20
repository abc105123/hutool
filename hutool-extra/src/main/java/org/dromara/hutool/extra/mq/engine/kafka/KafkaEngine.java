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

package org.dromara.hutool.extra.mq.engine.kafka;

import org.dromara.hutool.extra.mq.Consumer;
import org.dromara.hutool.extra.mq.Producer;
import org.dromara.hutool.extra.mq.engine.MQEngine;

import java.util.Properties;

/**
 * Kafka引擎
 *
 * @author Looly
 * @since 6.0.0
 */
public class KafkaEngine implements MQEngine {

	private final Properties properties;

	/**
	 * 构造
	 *
	 * @param properties 配置
	 */
	public KafkaEngine(final Properties properties) {
		this.properties = properties;
	}

	/**
	 * 增加配置项
	 *
	 * @param key   配置项
	 * @param value 值
	 * @return this
	 */
	public KafkaEngine addProperty(final String key, final String value) {
		this.properties.put(key, value);
		return this;
	}

	@Override
	public Producer getProducer() {
		return new KafkaProducer(this.properties);
	}

	@Override
	public Consumer getConsumer() {
		return new KafkaConsumer(this.properties);
	}
}
