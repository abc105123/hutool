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

package org.dromara.hutool.core.io;

import org.dromara.hutool.core.io.resource.ClassPathResource;
import org.dromara.hutool.core.text.StrUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.io.IOException;
import java.util.Properties;

/**
 * ClassPath资源读取测试
 *
 * @author Looly
 */
public class ClassPathResourceTest {

	@Test
	public void readStringTest() {
		final ClassPathResource resource = new ClassPathResource("test.properties");
		final String content = resource.readUtf8Str();
		Assertions.assertTrue(StrUtil.isNotEmpty(content));
	}

	@Test()
	@EnabledForJreRange(max = JRE.JAVA_8)
	public void readStringTest2() {
		// JDK9+中因为模块的加入，根路径读取可能为空
		// 读取classpath根目录测试
		final ClassPathResource resource = new ClassPathResource("/");
		final String content = resource.readUtf8Str();
		Assertions.assertTrue(StrUtil.isNotEmpty(content));
	}

	@Test()
	@EnabledForJreRange(min = JRE.JAVA_9)
	public void readStringTestForJdk9() {
		// JDK9+中因为模块的加入，根路径读取可能为空
		// 读取classpath根目录测试
		final ClassPathResource resource = new ClassPathResource("/");
		final String content = resource.readUtf8Str();
		Assertions.assertNotNull(content);
	}

	@Test
	public void readTest() throws IOException {
		final ClassPathResource resource = new ClassPathResource("test.properties");
		final Properties properties = new Properties();
		properties.load(resource.getStream());

		Assertions.assertEquals("1", properties.get("a"));
		Assertions.assertEquals("2", properties.get("b"));
	}

	@Test
	public void readFromJarTest() {
		//测试读取junit的jar包下的LICENSE-junit.txt文件
		final ClassPathResource resource = new ClassPathResource("META-INF/LICENSE.md");

		String result = resource.readUtf8Str();
		Assertions.assertNotNull(result);

		//二次读取测试，用于测试关闭流对再次读取的影响
		result = resource.readUtf8Str();
		Assertions.assertNotNull(result);
	}

	@Test
	public void getAbsTest() {
		final ClassPathResource resource = new ClassPathResource("META-INF/LICENSE.md");
		final String absPath = resource.getAbsolutePath();
		Assertions.assertTrue(absPath.contains("LICENSE"));
	}
}
