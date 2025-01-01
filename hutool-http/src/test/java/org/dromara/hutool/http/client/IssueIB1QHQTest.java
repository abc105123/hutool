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

package org.dromara.hutool.http.client;

import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.http.client.engine.ClientEngineFactory;
import org.dromara.hutool.http.meta.Method;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class IssueIB1QHQTest {
	@Test
	@Disabled
	void requestByOkHttpTest() {
		for (int i = 0; i < 3; i++) {
			String response = ClientEngineFactory.createEngine("OkHttp")
				.send(Request
					.of("https://hutool.cn")
					.method(Method.POST)
					.body("123")
				).bodyStr();
			Console.log("response: {}", response);
		}
	}
}
