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

package org.dromara.hutool.core.io.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataSizeUtilTest {

	@Test
	public void parseTest(){
		long parse = DataSizeUtil.parse("3M");
		Assertions.assertEquals(3145728, parse);

		parse = DataSizeUtil.parse("3m");
		Assertions.assertEquals(3145728, parse);

		parse = DataSizeUtil.parse("3MB");
		Assertions.assertEquals(3145728, parse);

		parse = DataSizeUtil.parse("3mb");
		Assertions.assertEquals(3145728, parse);

		parse = DataSizeUtil.parse("3.1M");
		Assertions.assertEquals(3250585, parse);

		parse = DataSizeUtil.parse("3.1m");
		Assertions.assertEquals(3250585, parse);

		parse = DataSizeUtil.parse("3.1MB");
		Assertions.assertEquals(3250585, parse);

		parse = DataSizeUtil.parse("-3.1MB");
		Assertions.assertEquals(-3250585, parse);

		parse = DataSizeUtil.parse("+3.1MB");
		Assertions.assertEquals(3250585, parse);

		parse = DataSizeUtil.parse("3.1mb");
		Assertions.assertEquals(3250585, parse);

		parse = DataSizeUtil.parse("3.1");
		Assertions.assertEquals(3, parse);

		try {
			DataSizeUtil.parse("3.1.3");
		} catch (final IllegalArgumentException ie) {
			Assertions.assertEquals("'3.1.3' is not a valid data size", ie.getMessage());
		}


	}

	@Test
	public void formatTest(){
		String format = DataSizeUtil.format(Long.MAX_VALUE);
		Assertions.assertEquals("8 EB", format);

		format = DataSizeUtil.format(1024L * 1024 * 1024 * 1024 * 1024);
		Assertions.assertEquals("1 PB", format);

		format = DataSizeUtil.format(1024L * 1024 * 1024 * 1024);
		Assertions.assertEquals("1 TB", format);
	}

	@Test
	void issueI88Z4ZTest() {
		final String size = DataSizeUtil.format(10240000);
		final long bytes = DataSize.parse(size).toBytes();
		Assertions.assertEquals(10244587, bytes);
	}
}
