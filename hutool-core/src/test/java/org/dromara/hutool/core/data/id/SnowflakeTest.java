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

import org.dromara.hutool.core.collection.set.ConcurrentHashSet;
import org.dromara.hutool.core.exception.HutoolException;
import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.core.lang.tuple.Pair;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.thread.ThreadUtil;
import org.dromara.hutool.core.util.RandomUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Snowflake单元测试
 * @author Looly
 *
 */
public class SnowflakeTest {

	@Test
	public void snowflakeTest1(){
		//构建Snowflake，提供终端ID和数据中心ID
		final Snowflake idWorker = new Snowflake(0, 0);
		final long nextId = idWorker.next();
		Assertions.assertTrue(nextId > 0);
	}

	@Test
	public void snowflakeTest(){
		final HashSet<Long> hashSet = new HashSet<>();

		//构建Snowflake，提供终端ID和数据中心ID
		final Snowflake idWorker = new Snowflake(0, 0);
		for (int i = 0; i < 1000; i++) {
			final long id = idWorker.next();
			hashSet.add(id);
		}
		Assertions.assertEquals(1000L, hashSet.size());
	}

	@Test
	public void snowflakeGetTest(){
		//构建Snowflake，提供终端ID和数据中心ID
		final Snowflake idWorker = new Snowflake(1, 2);
		final long nextId = idWorker.next();

		Assertions.assertEquals(1, idWorker.getWorkerId(nextId));
		Assertions.assertEquals(2, idWorker.getDataCenterId(nextId));
		Assertions.assertTrue(idWorker.getGenerateDateTime(nextId) - System.currentTimeMillis() < 10);
	}

	@Test
	@Disabled
	public void uniqueTest(){
		// 测试并发环境下生成ID是否重复
		final Snowflake snowflake = IdUtil.getSnowflake(0, 0);

		final Set<Long> ids = new ConcurrentHashSet<>();
		ThreadUtil.concurrencyTest(100, () -> {
			for (int i = 0; i < 50000; i++) {
				if(!ids.add(snowflake.next())){
					throw new HutoolException("重复ID！");
				}
			}
		});
	}

	@Test
	@Disabled
	public void uniqueTest2(){
		// 测试并发环境下生成ID是否重复
		final Snowflake snowflake = IdUtil.getSnowflake();

		final Set<Long> ids = new ConcurrentHashSet<>();
		ThreadUtil.concurrencyTest(100, () -> {
			for (int i = 0; i < 50000; i++) {
				if(!ids.add(snowflake.next())){
					throw new HutoolException("重复ID！");
				}
			}
		});
	}

	@Test
	public void getSnowflakeLengthTest(){
		for (int i = 0; i < 1000; i++) {
			final long l = IdUtil.getSnowflake(0, 0).next();
			Assertions.assertEquals(19, StrUtil.toString(l).length());
		}
	}

	@Test
	@Disabled
	public void snowflakeRandomSequenceTest(){
		final Snowflake snowflake = new Snowflake(null, 0, 0,
				false, 2);
		for (int i = 0; i < 1000; i++) {
			final long id = snowflake.next();
			Console.log(id);
			ThreadUtil.sleep(10);
		}
	}

	@Test
	@Disabled
	public void uniqueOfRandomSequenceTest(){
		// 测试并发环境下生成ID是否重复
		final Snowflake snowflake = new Snowflake(null, 0, 0,
				false, 100);

		final Set<Long> ids = new ConcurrentHashSet<>();
		ThreadUtil.concurrencyTest(100, () -> {
			for (int i = 0; i < 50000; i++) {
				if(!ids.add(snowflake.next())){
					throw new HutoolException("重复ID！");
				}
			}
		});
	}

	/**
	 * 测试-根据传入时间戳-计算ID起终点
	 */
	@Test
	public void snowflakeTestGetIdScope() {
		final long workerId = RandomUtil.randomLong(31);
		final long dataCenterId = RandomUtil.randomLong(31);
		final Snowflake idWorker = new Snowflake(workerId, dataCenterId);
		final long generatedId = idWorker.next();
		// 随机忽略数据中心和工作机器的占位
		final boolean ignore = RandomUtil.randomBoolean();
		final long createTimestamp = idWorker.getGenerateDateTime(generatedId);
		final Pair<Long, Long> idScope = idWorker.getIdScopeByTimestamp(createTimestamp, createTimestamp, ignore);
		final long startId = idScope.getLeft();
		final long endId = idScope.getRight();

		// 起点终点相差比较
		final long trueOffSet = endId - startId;
		// 忽略数据中心和工作机器时差值为22个1，否则为12个1
		final long expectedOffSet = ignore ? ~(-1 << 22) : ~(-1 << 12);
		Assertions.assertEquals(trueOffSet, expectedOffSet);
	}
}
