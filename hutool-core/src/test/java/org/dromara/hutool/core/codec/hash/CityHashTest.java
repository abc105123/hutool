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

package org.dromara.hutool.core.codec.hash;

import org.dromara.hutool.core.util.ByteUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CityHashTest {

	@Test
	public void hash32Test() {
		final CityHash cityHash = CityHash.INSTANCE;
		int hv = cityHash.hash32(ByteUtil.toUtf8Bytes("你"));
		Assertions.assertEquals(1290029860, hv);

		hv = cityHash.hash32(ByteUtil.toUtf8Bytes("你好"));
		Assertions.assertEquals(1374181357, hv);

		hv = cityHash.hash32(ByteUtil.toUtf8Bytes("见到你很高兴"));
		Assertions.assertEquals(1475516842, hv);
		hv = cityHash.hash32(ByteUtil.toUtf8Bytes("我们将通过生成一个大的文件的方式来检验各种方法的执行效率因为这种方式在结束的时候需要执行文件"));
		Assertions.assertEquals(0x51020cae, hv);
	}

	@Test
	public void hash64Test() {
		final CityHash cityHash = CityHash.INSTANCE;
		long hv = cityHash.hash64(ByteUtil.toUtf8Bytes("你"));
		Assertions.assertEquals(-4296898700418225525L, hv);

		hv = cityHash.hash64(ByteUtil.toUtf8Bytes("你好"));
		Assertions.assertEquals(-4294276205456761303L, hv);

		hv = cityHash.hash64(ByteUtil.toUtf8Bytes("见到你很高兴"));
		Assertions.assertEquals(272351505337503793L, hv);
		hv = cityHash.hash64(ByteUtil.toUtf8Bytes("我们将通过生成一个大的文件的方式来检验各种方法的执行效率因为这种方式在结束的时候需要执行文件"));
		Assertions.assertEquals(-8234735310919228703L, hv);
	}
}
