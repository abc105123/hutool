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

package org.dromara.hutool.setting;

import org.dromara.hutool.core.io.resource.ResourceUtil;
import org.dromara.hutool.core.util.CharsetUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IssueIB1I8PTest {
	@Test
	void loadTest() {
		final SettingLoader loader = new SettingLoader(CharsetUtil.UTF_8, true);
		loader.setValueEditor((group, key, value)->{
			if("pass".equals(key)){
				return "pass" + value;
			}
			return value;
		});
		final Setting setting = new Setting(ResourceUtil.getResource("test.setting"), loader);
		Assertions.assertEquals("pass123456", setting.getStrByGroup("pass", "demo"));
	}
}
