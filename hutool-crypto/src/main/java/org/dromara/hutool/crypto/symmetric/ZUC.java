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

package org.dromara.hutool.crypto.symmetric;

import org.dromara.hutool.core.util.RandomUtil;
import org.dromara.hutool.crypto.KeyUtil;

import javax.crypto.spec.IvParameterSpec;

/**
 * 祖冲之算法集（ZUC算法）实现，基于BouncyCastle实现。
 *
 * @author Looly
 * @since 5.7.12
 */
public class ZUC extends SymmetricCrypto {
	private static final long serialVersionUID = 1L;

	/**
	 * 生成ZUC算法密钥
	 *
	 * @param algorithm ZUC算法
	 * @return 密钥
	 *
	 * @see KeyUtil#generateKey(String)
	 */
	public static byte[] generateKey(final ZUCAlgorithm algorithm) {
		return KeyUtil.generateKey(algorithm.value).getEncoded();
	}

	/**
	 * 构造
	 *
	 * @param algorithm ZUC算法枚举，包括128位和256位两种
	 * @param key       密钥
	 * @param iv        加盐，128位加盐是16bytes，256位是25bytes，{@code null}是随机加盐
	 */
	public ZUC(final ZUCAlgorithm algorithm, final byte[] key, final byte[] iv) {
		super(algorithm.value,
				KeyUtil.generateKey(algorithm.value, key),
				generateIvParam(algorithm, iv));
	}

	/**
	 * ZUC类型，包括128位和256位
	 *
	 * @author Looly
	 */
	public enum ZUCAlgorithm {
		/**
		 * ZUC-128
		 */
		ZUC_128("ZUC-128"),
		/**
		 * ZUC-256
		 */
		ZUC_256("ZUC-256");

		private final String value;

		/**
		 * 构造
		 *
		 * @param value 算法的字符串表示，区分大小写
		 */
		ZUCAlgorithm(final String value) {
			this.value = value;
		}

		/**
		 * 获得算法的字符串表示形式
		 *
		 * @return 算法字符串
		 */
		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 生成加盐参数
	 *
	 * @param algorithm ZUC算法
	 * @param iv 加盐，128位加盐是16bytes，256位是25bytes，{@code null}是随机加盐
	 * @return {@link IvParameterSpec}
	 */
	private static IvParameterSpec generateIvParam(final ZUCAlgorithm algorithm, byte[] iv){
		if(null == iv){
			switch (algorithm){
				case ZUC_128:
					iv = RandomUtil.randomBytes(16);
					break;
				case ZUC_256:
					iv = RandomUtil.randomBytes(25);
					break;
			}
		}
		return new IvParameterSpec(iv);
	}
}
