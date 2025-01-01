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

package org.dromara.hutool.http;

import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.core.map.MapUtil;
import org.dromara.hutool.core.net.url.UrlBuilder;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class Issue2531Test {

	@Test
	@Disabled
	public void getTest(){
		final Map<String,String> map = new HashMap<>();
		map.put("str","+123");

		final String queryParam = MapUtil.join(map, "&", "=");//返回str=+123
		Console.log(queryParam);

		final Request request = Request.of("http://localhost:8888/formTest?" + queryParam);
		//request.setUrl("http://localhost:8888/formTest" + "?" + queryParam);
		//noinspection resource
		final Response execute = request.send();
		Console.log(execute.body());
	}

	@Test
	public void encodePlusTest(){
		// 根据RFC3986规范，在URL中，"+"是安全字符，所以不会解码也不会编码
		UrlBuilder builder = UrlBuilder.ofHttp("https://hutool.cn/a=+");
		Assertions.assertEquals("https://hutool.cn/a=+", builder.toString());

		// 由于+为安全字符无需编码，ofHttp方法会把%2B解码为+，但是编码的时候不会编码
		builder = UrlBuilder.ofHttp("https://hutool.cn/a=%2B");
		Assertions.assertEquals("https://hutool.cn/a=+", builder.toString());

		// 如果你不想解码%2B，则charset传null表示不做解码，编码时候也被忽略
		builder = UrlBuilder.ofHttp("https://hutool.cn/a=%2B", null);
		Assertions.assertEquals("https://hutool.cn/a=%2B", builder.toString());
	}
}
