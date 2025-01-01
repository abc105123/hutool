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

package org.dromara.hutool.http.client;

import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.http.HttpGlobalConfig;
import org.dromara.hutool.http.HttpUtil;
import org.dromara.hutool.http.client.engine.ClientEngine;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class IssueIBC5N8Test {
	@Test
	@Disabled
	public void getBadSSLTest(){
		HttpGlobalConfig.setTrustAnyHost(true);
		requestBadSSL("httpclient4");
		requestBadSSL("httpclient5");
		requestBadSSL("okhttp");
		requestBadSSL("jdkClient");
	}

	private void requestBadSSL(final String engineName) {
		final ClientEngine engine = HttpUtil.createClient(engineName);

		final Request req = Request.of("https://expired.badssl.com/");
		final Response res = engine.send(req);
		Console.log(res.getStatus());
	}
}
