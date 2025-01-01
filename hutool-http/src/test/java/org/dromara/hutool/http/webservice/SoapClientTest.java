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

package org.dromara.hutool.http.webservice;

import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.core.util.CharsetUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * SOAP相关单元测试
 *
 * @author Looly
 *
 */
public class SoapClientTest {

	@Test
	@Disabled
	public void requestTest() {
		final SoapClient client = SoapClient.of("http://www.webxml.com.cn/WebServices/IpAddressSearchWebService.asmx")
		.setMethod("getCountryCityByIp", "http://WebXml.com.cn/")
		.charset(CharsetUtil.GBK)
		.setParam("theIpAddress", "218.21.240.106");

		Console.log(client.getMsgStr(true));

		Console.log(client.send().getResponseStr(true));
	}

	@Test
	@Disabled
	public void requestForMessageTest() {
		final SoapClient client = SoapClient.of("http://www.webxml.com.cn/WebServices/IpAddressSearchWebService.asmx")
				.setMethod("web:getCountryCityByIp", "http://WebXml.com.cn/")
				.setParam("theIpAddress", "218.21.240.106");

		Console.log(client.send().getBodyText());
	}
}
