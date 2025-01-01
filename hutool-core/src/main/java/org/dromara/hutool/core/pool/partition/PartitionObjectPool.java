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

package org.dromara.hutool.core.pool.partition;

import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.pool.ObjectFactory;
import org.dromara.hutool.core.pool.ObjectPool;
import org.dromara.hutool.core.pool.Poolable;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.thread.ThreadUtil;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 分区对象池实现<br>
 * 来自：https://github.com/DanielYWoo/fast-object-pool/blob/master/src/main/java/cn/danielw/fop/ObjectPool.java
 *
 * @param <T> 对象类型
 * @author Daniel, Looly
 */
public class PartitionObjectPool<T> implements ObjectPool<T> {
	private static final long serialVersionUID = 1L;

	private final PartitionPoolConfig config;
	// 分区，创建后不再变更，线程安全
	private final PoolPartition<T>[] partitions;

	private boolean closed;

	/**
	 * 构造
	 *
	 * @param config  配置
	 * @param factory 对象工厂，用于创建、验证和销毁对象
	 */
	@SuppressWarnings({"unchecked", "resource"})
	public PartitionObjectPool(final PartitionPoolConfig config, final ObjectFactory<T> factory) {
		this.config = config;

		final int partitionSize = config.getPartitionSize();
		this.partitions = new PoolPartition[partitionSize];
		for (int i = 0; i < partitionSize; i++) {
			partitions[i] = new PoolPartition<>(config, createBlockingQueue(config), factory);
		}
	}

	/**
	 * 获取持有对象总数
	 *
	 * @return 总数
	 */
	@Override
	public int getTotal() {
		int size = 0;
		for (final PoolPartition<T> subPool : partitions) {
			size += subPool.getTotal();
		}
		return size;
	}

	@Override
	public int getIdleCount() {
		int size = 0;
		for (final PoolPartition<T> subPool : partitions) {
			size += subPool.getIdleCount();
		}
		return size;
	}

	@Override
	public int getActiveCount() {
		int size = 0;
		for (final PoolPartition<T> subPool : partitions) {
			size += subPool.getActiveCount();
		}
		return size;
	}

	@Override
	public T borrowObject() {
		checkClosed();
		return this.partitions[getPartitionIndex(this.config)].borrowObject();
	}

	@Override
	public PartitionObjectPool<T> returnObject(final T obj) {
		checkClosed();
		this.partitions[getPartitionIndex(this.config)].returnObject(obj);
		return this;
	}

	@Override
	public ObjectPool<T> free(final T obj) {
		checkClosed();
		this.partitions[getPartitionIndex(this.config)].free(obj);
		return this;
	}

	@Override
	public void close() throws IOException {
		this.closed = true;
		IoUtil.closeQuietly(this.partitions);
	}

	@Override
	public String toString() {
		return StrUtil.format("PartitionObjectPool: total: {}, idle: {}, active: {}",
			getTotal(), getIdleCount(), getActiveCount());
	}

	/**
	 * 创建阻塞队列，默认为{@link ArrayBlockingQueue}<br>
	 * 如果需要自定义队列类型，子类重写此方法
	 *
	 * @param poolConfig 池配置
	 * @return 队列
	 */
	protected BlockingQueue<Poolable<T>> createBlockingQueue(final PartitionPoolConfig poolConfig) {
		return new ArrayBlockingQueue<>(poolConfig.getMaxSize());
	}

	/**
	 * 获取当前线程被分配的分区<br>
	 * 默认根据线程ID（TID）取分区大小余数<br>
	 * 如果需要自定义，子类重写此方法
	 *
	 * @param poolConfig 池配置
	 * @return 分配的分区
	 */
	protected int getPartitionIndex(final PartitionPoolConfig poolConfig) {
		return (int) (ThreadUtil.currentThreadId() % poolConfig.getPartitionSize());
	}

	/**
	 * 检查池是否关闭
	 */
	private void checkClosed() {
		if (this.closed) {
			throw new IllegalStateException("Object Pool is closed!");
		}
	}
}
