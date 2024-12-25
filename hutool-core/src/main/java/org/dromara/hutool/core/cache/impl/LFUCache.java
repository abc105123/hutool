/*
 * Copyright (c) 2013-2024 Hutool Team and hutool.cn
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

package org.dromara.hutool.core.cache.impl;

import org.dromara.hutool.core.thread.lock.NoLock;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LFU(least frequently used) 最少使用率缓存<br>
 * 根据使用次数来判定对象是否被持续缓存<br>
 * 使用率是通过访问次数计算的。<br>
 * 当缓存满时清理过期对象。<br>
 * 清理后依旧满的情况下清除最少访问（访问计数最小）的对象并将其他对象的访问数减去这个最小访问数，以便新对象进入后可以公平计数。
 *
 * @author Looly,jodd
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class LFUCache<K, V> extends LockedCache<K, V> {
	private static final long serialVersionUID = 1L;

	/**
	 * 构造
	 *
	 * @param capacity 容量
	 */
	public LFUCache(final int capacity) {
		this(capacity, 0);
	}

	/**
	 * 构造
	 *
	 * @param capacity 容量
	 * @param timeout 过期时长
	 */
	public LFUCache(int capacity, final long timeout) {
		if(Integer.MAX_VALUE == capacity) {
			capacity -= 1;
		}

		this.capacity = capacity;
		this.timeout = timeout;
		//lock = new ReentrantLock();
		//cacheMap = new HashMap<>(capacity + 1, 1.0f);
		lock = NoLock.INSTANCE;
		cacheMap = new ConcurrentHashMap<>(capacity + 1, 1.0f);
	}

	// ---------------------------------------------------------------- prune

	/**
	 * 清理过期对象。<br>
	 * 清理后依旧满的情况下清除最少访问（访问计数最小）的对象并将其他对象的访问数减去这个最小访问数，以便新对象进入后可以公平计数。
	 *
	 * @return 清理个数
	 */
	@Override
	protected int pruneCache() {
		int count = 0;
		CacheObj<K, V> comin = null;

		// 清理过期对象并找出访问最少的对象
		Iterator<CacheObj<K, V>> values = cacheObjIter();
		CacheObj<K, V> co;
		while (values.hasNext()) {
			co = values.next();
			if (co.isExpired() == true) {
				values.remove();
				onRemove(co.key, co.obj);
				count++;
				continue;
			}

			//找出访问最少的对象
			if (comin == null || co.accessCount.get() < comin.accessCount.get()) {
				comin = co;
			}
		}

		// 减少所有对象访问量，并清除减少后为0的访问对象
		if (isFull() && comin != null) {
			final long minAccessCount = comin.accessCount.get();

			values = cacheObjIter();
			CacheObj<K, V> co1;
			while (values.hasNext()) {
				co1 = values.next();
				if (co1.accessCount.addAndGet(-minAccessCount) <= 0) {
					values.remove();
					onRemove(co1.key, co1.obj);
					count++;
				}
			}
		}

		return count;
	}
}
