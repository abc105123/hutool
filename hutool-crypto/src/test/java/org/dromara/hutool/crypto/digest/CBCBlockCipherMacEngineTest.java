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

package org.dromara.hutool.crypto.digest;

import org.dromara.hutool.crypto.KeyUtil;
import org.dromara.hutool.crypto.digest.mac.Mac;
import org.dromara.hutool.crypto.digest.mac.SM4MacEngine;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CBCBlockCipherMacEngineTest {

	@Test
	public void SM4CMACTest(){
		// https://github.com/dromara/hutool/issues/2206
		final byte[] key = new byte[16];
		final CipherParameters parameter = new KeyParameter(KeyUtil.generateKey("SM4", key).getEncoded());
		final Mac mac = new Mac(new SM4MacEngine(parameter));

		// 原文
		final String testStr = "test中文";

		final String macHex1 = mac.digestHex(testStr);
		Assertions.assertEquals("3212e848db7f816a4bd591ad9948debf", macHex1);
	}

	@Test
	public void SM4CMACWithIVTest(){
		// https://github.com/dromara/hutool/issues/2206
		final byte[] key = new byte[16];
		final byte[] iv = new byte[16];
		CipherParameters parameter = new KeyParameter(KeyUtil.generateKey("SM4", key).getEncoded());
		parameter = new ParametersWithIV(parameter, iv);
		final Mac mac = new Mac(new SM4MacEngine(parameter));

		// 原文
		final String testStr = "test中文";

		final String macHex1 = mac.digestHex(testStr);
		Assertions.assertEquals("3212e848db7f816a4bd591ad9948debf", macHex1);
	}
}
