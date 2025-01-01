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

import org.dromara.hutool.core.util.RuntimeUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 简单单线程任务调度器<br>
 * 通过自定义Job定时执行任务，通过{@link #getResult()} 可以获取调取时的执行结果
 *
 * @param <T> 结果类型
 */
public class SimpleScheduler<T> {
	private final Job<T> job;

	/**
	 * 构造
	 *
	 * @param job    任务
	 * @param period 任务间隔，单位毫秒
	 */
	public SimpleScheduler(final Job<T> job, final long period) {
		this(job, 0, period, true);
	}

	/**
	 * 构造
	 *
	 * @param job                   任务
	 * @param initialDelay          初始延迟，单位毫秒
	 * @param period                执行周期，单位毫秒
	 * @param fixedRateOrFixedDelay {@code true}表示fixedRate模式，{@code false}表示fixedDelay模式
	 */
	public SimpleScheduler(final Job<T> job, final long initialDelay, final long period, final boolean fixedRateOrFixedDelay) {
		this.job = job;

		final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		// 启动定时任务
		ThreadUtil.schedule(scheduler, job, initialDelay, period, fixedRateOrFixedDelay);
		// 定时任务在程序结束时结束
		RuntimeUtil.addShutdownHook(scheduler::shutdown);
	}

	/**
	 * 获取执行任务的阶段性结果
	 *
	 * @return 结果
	 */
	public T getResult() {
		return this.job.getResult();
	}

	/**
	 * 带有结果计算的任务<br>
	 * 用户实现此接口，通过{@link #run()}实现定时任务的内容，定时任务每次执行或多次执行都可以产生一个结果<br>
	 * 这个结果存储在一个volatile的对象属性中，通过{@link #getResult()}来读取这一阶段的结果。
	 *
	 * @param <T> 结果类型
	 */
	public interface Job<T> extends Runnable {
		/**
		 * 获取执行结果
		 *
		 * @return 执行结果
		 */
		T getResult();
	}
}
