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

package org.dromara.hutool.core.data.id;

import org.dromara.hutool.core.util.RandomUtil;

import java.security.SecureRandom;
import java.util.Random;

/**
 * NanoId，一个小型、安全、对 URL友好的唯一字符串 ID 生成器，特点：
 *
 * <ul>
 *     <li>安全：它使用加密、强大的随机 API，并保证符号的正确分配</li>
 *     <li>体积小：只有 258 bytes 大小（压缩后）、无依赖</li>
 *     <li>紧凑：它使用比 UUID (A-Za-z0-9_~)更多的符号</li>
 * </ul>
 *
 * <p>
 * 此实现的逻辑基于JavaScript的NanoId实现，见：<a href="https://github.com/ai/nanoid">https://github.com/ai/nanoid</a>
 *
 * @author David Klebanoff
 */
public class NanoId {

	/**
	 * 默认随机数生成器，使用{@link SecureRandom}确保健壮性
	 */
	private static final SecureRandom DEFAULT_NUMBER_GENERATOR = RandomUtil.getSecureRandom();

	/**
	 * 默认随机字母表，使用URL安全的Base64字符
	 */
	private static final char[] DEFAULT_ALPHABET =
			"_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

	/**
	 * 默认长度
	 */
	public static final int DEFAULT_SIZE = 21;

	/**
	 * 生成伪随机的NanoId字符串，长度为默认的{@link #DEFAULT_SIZE}，使用密码安全的伪随机生成器
	 *
	 * @return 伪随机的NanoId字符串
	 */
	public static String randomNanoId() {
		return randomNanoId(DEFAULT_SIZE);
	}

	/**
	 * 生成伪随机的NanoId字符串
	 *
	 * @param size ID长度
	 * @return 伪随机的NanoId字符串
	 */
	public static String randomNanoId(final int size) {
		return randomNanoId(null, null, size);
	}

	/**
	 * 生成伪随机的NanoId字符串
	 *
	 * @param random   随机数生成器
	 * @param alphabet 随机字母表
	 * @param size     ID长度
	 * @return 伪随机的NanoId字符串
	 */
	public static String randomNanoId(Random random, char[] alphabet, final int size) {
		if (random == null) {
			random = DEFAULT_NUMBER_GENERATOR;
		}

		if (alphabet == null) {
			alphabet = DEFAULT_ALPHABET;
		}

		if (alphabet.length == 0 || alphabet.length >= 256) {
			throw new IllegalArgumentException("Alphabet must contain between 1 and 255 symbols.");
		}

		if (size <= 0) {
			throw new IllegalArgumentException("Size must be greater than zero.");
		}

		final int mask = (2 << (int) Math.floor(Math.log(alphabet.length - 1) / Math.log(2))) - 1;
		final int step = (int) Math.ceil(1.6 * mask * size / alphabet.length);

		final StringBuilder idBuilder = new StringBuilder();

		while (true) {
			final byte[] bytes = new byte[step];
			random.nextBytes(bytes);
			for (int i = 0; i < step; i++) {
				final int alphabetIndex = bytes[i] & mask;
				if (alphabetIndex < alphabet.length) {
					idBuilder.append(alphabet[alphabetIndex]);
					if (idBuilder.length() == size) {
						return idBuilder.toString();
					}
				}
			}
		}
	}
}
