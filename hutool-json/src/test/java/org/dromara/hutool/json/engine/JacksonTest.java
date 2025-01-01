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

package org.dromara.hutool.json.engine;

import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.json.JSONObject;
import org.dromara.hutool.json.JSONUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JacksonTest {
	/**
	 * jackson默认缩进两个空格，使用\r\n换行符
	 */
	@Test
	void prettyPrintTest() {
		final JSONEngine engine = JSONEngineFactory.createEngine("jackson");
		engine.init(JSONEngineConfig.of().setPrettyPrint(true));

		final JSONEngineTest.TestBean testBean = new JSONEngineTest.TestBean("张三", 18, true);
		String jsonString = engine.toJsonString(testBean);
		// 使用统一换行符
		jsonString = StrUtil.removeAll(jsonString, '\r');
		Assertions.assertEquals("{\n" +
			"  \"name\" : \"张三\",\n" +
			"  \"age\" : 18,\n" +
			"  \"gender\" : true\n" +
			"}", jsonString);
	}

	/**
	 * https://gitee.com/dromara/hutool/issues/IB3GM4<br>
	 * JSON和Jackson兼容
	 */
	@Test
	void toJsonStringOfHutoolJsonTest() {
		final JSONObject jsonObject = JSONUtil.ofObj()
			.putValue("name", "张三")
			.putValue("age", 18)
			.putValue("sub", JSONUtil.ofObj()
				.putValue("aaa", "aa1").putValue("bbb", "bb1"));
		final JSONEngine engine = JSONEngineFactory.createEngine("jackson");
		final String jsonString = engine.toJsonString(jsonObject);
		Assertions.assertEquals("{\"name\":\"张三\",\"age\":18,\"sub\":{\"aaa\":\"aa1\",\"bbb\":\"bb1\"}}", jsonString);
	}
}
