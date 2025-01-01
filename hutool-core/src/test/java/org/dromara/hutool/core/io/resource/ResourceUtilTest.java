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

package org.dromara.hutool.core.io.resource;

import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResourceUtilTest {

	@Test
	public void readXmlTest(){
		final String str = ResourceUtil.readUtf8Str("test.xml");
		Assertions.assertNotNull(str);

		final Resource resource = new ClassPathResource("test.xml");
		final String xmlStr = resource.readUtf8Str();

		Assertions.assertEquals(str, xmlStr);
	}

	@Test
	public void stringResourceTest(){
		final StringResource stringResource = new StringResource("testData", "test");
		Assertions.assertEquals("test", stringResource.getName());
		Assertions.assertArrayEquals("testData".getBytes(), stringResource.readBytes());
		Assertions.assertArrayEquals("testData".getBytes(), IoUtil.readBytes(stringResource.getStream()));
	}

	@Test
	public void fileResourceTest(){
		final FileResource resource = new FileResource(FileUtil.file("test.xml"));
		Assertions.assertEquals("test.xml", resource.getName());
		Assertions.assertTrue(StrUtil.isNotEmpty(resource.readUtf8Str()));
	}

	@Test
	void getResourceTest() {
		final Resource resource = ResourceUtil.getResource("file:test.xml");
		Assertions.assertNotNull(resource);
	}

	@Test
	void getResourceTest2() {
		// project:开头表示基于项目的相对路径，此处无文件报错
		Assertions.assertThrows(NoResourceException.class, () -> ResourceUtil.getResource("project:test.xml"));
	}
}
