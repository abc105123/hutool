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

package org.dromara.hutool.poi.excel.reader;

import org.dromara.hutool.poi.excel.ExcelUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 *
 */
public class Issue3120Test {

	@Test
	void readTest() {
		final ExcelReader reader = ExcelUtil.getReader("issue3120.xlsx");
		final List<List<Object>> read = reader.read(2, Integer.MAX_VALUE, false);
		Assertions.assertEquals("[1, null, 100, null, 20]", read.get(0).toString());
		Assertions.assertEquals("[32, null, 200, null, 30]", read.get(1).toString());
	}

}
