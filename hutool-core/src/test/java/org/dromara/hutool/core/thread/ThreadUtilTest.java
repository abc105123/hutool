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

package org.dromara.hutool.core.thread;

import org.dromara.hutool.core.date.TimeUtil;
import org.dromara.hutool.core.exception.HutoolException;
import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.core.util.RandomUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadUtilTest {


	@Test
	@Disabled
	public void testNewFixedExecutor(){
		final ThreadPoolExecutor executor = ThreadUtil.newFixedExecutor(1, 2, "test-", true);
		for (int i = 0; i < 20; i++) {
			executor.execute(()->{
				Console.log("执行任务1");
			});
		}
	}
	@Test
	public void testNewExecutorByBlockingCoefficient(){
		final ThreadPoolExecutor executor = ThreadUtil.newExecutorByBlockingCoefficient(0.5f);
		Console.log(executor.getCorePoolSize());
	}

	@Test
	public void executeTest() {
		final boolean isValid = true;

		ThreadUtil.execute(() -> Assertions.assertTrue(isValid));
	}

	@Test
	@Disabled
	public void phaserTest(){
		final LocalDateTime now = TimeUtil.parse("2022-08-04T22:59:59+08:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		Assertions.assertNotNull(now);

		final int repeat = 30; // 执行的轮数配置
		final Phaser phaser = new Phaser() {  // 进行一些处理方法的覆写
			//返回ture: 移相器终止，false: 移相器继续执行
			@Override
			protected boolean onAdvance(final int phase, final int registeredParties) { // 回调处理
				System.out.printf("【onAdvance()处理】进阶处理操作，phase = %s、registeredParties = %s%n",
						phase, registeredParties);
				return phase + 1 >= repeat || registeredParties == 0; // 终止处理
			}
		};
		for (int x = 0; x < 2; x++) {   // 循环创建2个线程
			phaser.register(); // 注册参与者的线程
			new Thread(()->{ // 每一个线程都在持续的执行之中
				while (!phaser.isTerminated()) { // 现在没有终止Phaser执行
					try {
						TimeUnit.SECONDS.sleep(RandomUtil.randomInt(1, 10)); // 增加操作延迟,模拟各个线程执行时间不多。阿超、阿珍准备爱果的时间不同
					} catch (final InterruptedException e) {
						throw new HutoolException(e);
					}
					phaser.arriveAndAwaitAdvance(); // 等待其他的线程就位； 准备就绪，并等待其他线程就绪; 阿超、阿珍准备好了爱果，相互等待见面共度美好的一天
					final String date = TimeUtil.formatNormal(now.plusYears(phaser.getPhase() - 1)); // 增加一年
					System.out.printf("【%s】%s 阿超和阿珍共度了一个美好的七夕。%n", date, Thread.currentThread().getName());
					ThreadUtil.sleep(3, TimeUnit.SECONDS);
				}
			}, "子线程 - " + (x == 0 ? "阿超" : "阿珍")).start();
		}

		ThreadUtil.waitForDie();
	}

	@Test
	public void cyclicBarrierTest(){
		//示例：7个同学，集齐7个龙珠，7个同学一起召唤神龙；前后集齐了2次
		final AtomicInteger times = new AtomicInteger();
		final CyclicBarrier barrier = new CyclicBarrier(7, ()->{
			System.out.println("    ");
			System.out.println("    ");
			System.out.println("【循环栅栏业务处理】7个子线程 都收集了一颗龙珠，七颗龙珠已经收集齐全，开始召唤神龙。" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			times.getAndIncrement();
		}); // 现在设置的栅栏的数量为2
		for (int x = 0; x < 7; x++) {   // 创建7个线程, 当然也可以使用线程池替换。
			new Thread(() -> {
				while (times.get() < 2) {
					try {
						System.out.printf("【Barrier - 收集龙珠】当前的线程名称：%s%n", Thread.currentThread().getName());
						final int time = ThreadLocalRandom.current().nextInt(1, 10); // 等待一段时间，模拟线程的执行时间
						TimeUnit.SECONDS.sleep(time); // 模拟业务延迟，收集龙珠的时间
						barrier.await(); // 等待，凑够了7个等待的线程
						System.err.printf("〖Barrier - 举起龙珠召唤神龙〗当前的线程名称：%s\t%s%n", Thread.currentThread().getName(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
						if (barrier.getParties() >= 7) {
							barrier.reset(); // 重置栅栏，等待下一次的召唤。
						}
					} catch (final Exception e) {
						throw new HutoolException(e);
					}
				}
			}, "线程 - " + x).start();
		}
	}

	@Test
	void safeSleepTest() {
		final long sleepMillis = RandomUtil.randomLong(1, 1000);
		// 随机sleep时长，确保sleep时间足够
		final long l = System.currentTimeMillis();
		ThreadUtil.safeSleep(sleepMillis);
		Assertions.assertTrue(System.currentTimeMillis() - l >= sleepMillis);
	}
}
