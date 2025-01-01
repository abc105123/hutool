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

package org.dromara.hutool.http.server.servlet;

import jakarta.servlet.http.HttpServletResponse;
import org.dromara.hutool.core.util.ByteUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * ServletUtil工具类测试
 *
 * @author dazer
 * @see ServletUtil
 * @see ServletUtil
 */
public class ServletUtilTest {

	@Test
	@Disabled
	public void writeTest() {
		final HttpServletResponse response = null;
		final byte[] bytes = ByteUtil.toUtf8Bytes("地球是我们共同的家园，需要大家珍惜.");

		//下载文件
		// 这里没法直接测试，直接写到这里，方便调用；
		//noinspection ConstantConditions
		if (response != null) {
			final String fileName = "签名文件.pdf";
			final String contentType = "application/pdf";// application/octet-stream、image/jpeg、image/gif
			response.setCharacterEncoding(StandardCharsets.UTF_8.name()); // 必须设置否则乱码; 但是 safari乱码
			ServletUtil.write(response, new ByteArrayInputStream(bytes), contentType, fileName);
		}
	}

	@Test
	@Disabled
	public void jakartaWriteTest() {
		final jakarta.servlet.http.HttpServletResponse response = null;
		final byte[] bytes = ByteUtil.toUtf8Bytes("地球是我们共同的家园，需要大家珍惜.");

		//下载文件
		// 这里没法直接测试，直接写到这里，方便调用；
		//noinspection ConstantConditions
		if (response != null) {
			final String fileName = "签名文件.pdf";
			final String contentType = "application/pdf";// application/octet-stream、image/jpeg、image/gif
			response.setCharacterEncoding(StandardCharsets.UTF_8.name()); // 必须设置否则乱码; 但是 safari乱码
			ServletUtil.write(response, new ByteArrayInputStream(bytes), contentType, fileName);
		}
	}
}
