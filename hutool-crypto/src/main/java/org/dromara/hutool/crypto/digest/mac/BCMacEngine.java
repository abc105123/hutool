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

package org.dromara.hutool.crypto.digest.mac;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.dromara.hutool.core.lang.wrapper.SimpleWrapper;

/**
 * BouncyCastle的MAC算法实现引擎，使用{@link Mac} 实现摘要<br>
 * 当引入BouncyCastle库时自动使用其作为Provider
 *
 * @author Looly
 * @since 5.8.0
 */
public class BCMacEngine extends SimpleWrapper<Mac> implements MacEngine {

	// ------------------------------------------------------------------------------------------- Constructor start
	/**
	 * 构造
	 *
	 * @param mac    {@link Mac}
	 * @param params 参数，例如密钥可以用{@link KeyParameter}
	 * @since 5.8.0
	 */
	public BCMacEngine(final Mac mac, final CipherParameters params) {
		super(initMac(mac, params));
	}
	// ------------------------------------------------------------------------------------------- Constructor end

	@Override
	public void update(final byte[] in, final int inOff, final int len) {
		this.raw.update(in, inOff, len);
	}

	@Override
	public byte[] doFinal() {
		final byte[] result = new byte[getMacLength()];
		this.raw.doFinal(result, 0);
		return result;
	}

	@Override
	public void reset() {
		this.raw.reset();
	}

	@Override
	public int getMacLength() {
		return this.raw.getMacSize();
	}

	@Override
	public String getAlgorithm() {
		return this.raw.getAlgorithmName();
	}

	/**
	 * 初始化
	 *
	 * @param mac    摘要算法
	 * @param params 参数，例如密钥可以用{@link KeyParameter}
	 * @return this
	 */
	private static Mac initMac(final Mac mac, final CipherParameters params) {
		mac.init(params);
		return mac;
	}
}
