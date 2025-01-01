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

package org.dromara.hutool.http.server.engine;

import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.core.net.ssl.SSLContextUtil;
import org.dromara.hutool.crypto.KeyStoreUtil;
import org.dromara.hutool.http.server.ServerConfig;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;

public class SmartHttpServerTest {
	public static void main(final String[] args) throws Exception {
		final char[] pwd = "123456".toCharArray();
		final KeyStore keyStore = KeyStoreUtil.readJKSKeyStore(FileUtil.file("d:/test/keystore.jks"), pwd);
		// 初始化SSLContext
		final SSLContext sslContext = SSLContextUtil.createSSLContext(keyStore, pwd);

		final ServerEngine engine = ServerEngineFactory.createEngine("SmartHttpServer");
		engine.init(ServerConfig.of().setSslContext(sslContext));
		engine.setHandler((request, response) -> {
			Console.log(request.getPath());
			response.write("Hutool Smart-Http response test");
		});
		engine.start();
	}
}
