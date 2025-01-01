/*
 * Copyright (c) 2013-2025 Hutool Team and hutool.cn
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

package org.dromara.hutool.core.date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class Issue3301Test {
	@Test
	void ofTest() {
		final ZonedDateTime now = ZonedDateTime.now();
		// 获得一个特殊的 temporal
		final String text = DateTimeFormatter.ISO_INSTANT.format(now);
		final TemporalAccessor temporal = DateTimeFormatter.ISO_INSTANT.parse(text);

		final LocalDateTime actual = TimeUtil.of(temporal);
		Assertions.assertEquals(now.toLocalDateTime().toString(), actual.toString());
	}
}
