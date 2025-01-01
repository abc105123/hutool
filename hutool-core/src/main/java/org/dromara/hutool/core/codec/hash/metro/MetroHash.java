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

package org.dromara.hutool.core.codec.hash.metro;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Apache 发布的MetroHash算法接口，是一组用于非加密用例的最先进的哈希函数。
 * 除了卓越的性能外，他们还以算法生成而著称。
 *
 * <p>
 * 官方实现：https://github.com/jandrewrogers/MetroHash
 * 官方文档：http://www.jandrewrogers.com/2015/05/27/metrohash/
 * 来自：https://github.com/postamar/java-metrohash/
 *
 * @param <R> 返回值类型，为this类型
 * @author Marius Posta
 */
public interface MetroHash<R extends MetroHash<R>> {

	/**
	 * 创建 {@code MetroHash}对象
	 *
	 * @param seed  种子
	 * @param is128 是否128位
	 * @return {@code MetroHash}对象
	 */
	static MetroHash<?> of(final long seed, final boolean is128) {
		return is128 ? new MetroHash128(seed) : new MetroHash64(seed);
	}

	/**
	 * 将给定的{@link ByteBuffer}中的数据追加计算hash值<br>
	 * 此方法会更新hash值状态
	 *
	 * @param input 内容
	 * @return this
	 */
	R apply(final ByteBuffer input);

	/**
	 * 将结果hash值写出到{@link ByteBuffer}中，可选端序
	 *
	 * @param output    输出
	 * @param byteOrder 端序
	 * @return this
	 */
	R write(ByteBuffer output, final ByteOrder byteOrder);

	/**
	 * 重置，重置后可复用对象开启新的计算
	 *
	 * @return this
	 */
	R reset();
}
