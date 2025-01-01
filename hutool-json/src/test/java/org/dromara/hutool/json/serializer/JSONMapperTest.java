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

package org.dromara.hutool.json.serializer;

import org.dromara.hutool.core.date.DateUtil;
import org.dromara.hutool.core.date.StopWatch;
import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.json.JSONFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class JSONMapperTest {

	/**
	 * Mapper性能耗费较多
	 */
	@Test
	@Disabled
	void toJSONTest() {
		final JSONFactory factory = JSONFactory.getInstance();

		final JSONMapper mapper = factory.getMapper();

		final StopWatch stopWatch = DateUtil.createStopWatch();

		final int count = 1000;
		stopWatch.start("use mapper");
		for (int i = 0; i < count; i++) {
			mapper.toJSON("qbw123", false);
		}
		stopWatch.stop();

		stopWatch.start("use ofPrimitive");
		for (int i = 0; i < count; i++) {
			factory.ofPrimitive("qbw123");
		}
		stopWatch.stop();

		Console.log(stopWatch.prettyPrint());
	}
}
