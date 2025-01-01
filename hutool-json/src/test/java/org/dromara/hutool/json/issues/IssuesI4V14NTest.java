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

package org.dromara.hutool.json.issues;

import org.dromara.hutool.core.reflect.TypeReference;
import org.dromara.hutool.json.JSONObject;
import org.dromara.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IssuesI4V14NTest {

	@Test
	public void parseTest(){
		final String str = "{\"A\" : \"A\\nb\"}";
		final JSONObject jsonObject = JSONUtil.parseObj(str);
		assertEquals("A\nb", jsonObject.getStr("A"));

		final Map<String, String> map = jsonObject.toBean(new TypeReference<Map<String, String>>() {});
		assertEquals("A\nb", map.get("A"));
	}
}
