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

package org.dromara.hutool.core.collection.set;

import org.dromara.hutool.core.map.concurrent.ConcurrentLinkedHashMap;

import java.util.Collection;

/**
 * 通过{@link ConcurrentLinkedHashMap}实现的线程安全HashSet
 *
 * @param <E> 元素类型
 * @author Looly
 * @since 3.1.0
 */
public class ConcurrentLinkedHashSet<E> extends SetFromMap<E> {
	private static final long serialVersionUID = 7997886765361607470L;

	// ----------------------------------------------------------------------------------- Constructor start

	/**
	 * 构造
	 */
	public ConcurrentLinkedHashSet() {
		super(new ConcurrentLinkedHashMap.Builder<E, Boolean>().maximumWeightedCapacity(64).build());
	}

	/**
	 * 构造<br>
	 * 触发因子为默认的0.75
	 *
	 * @param initialCapacity 初始大小
	 */
	public ConcurrentLinkedHashSet(final int initialCapacity) {
		super(new ConcurrentLinkedHashMap.Builder<E, Boolean>()
			.initialCapacity(initialCapacity)
			.maximumWeightedCapacity(initialCapacity)
			.build());
	}

	/**
	 * 构造
	 *
	 * @param initialCapacity  初始大小
	 * @param concurrencyLevel 线程并发度
	 */
	public ConcurrentLinkedHashSet(final int initialCapacity, final int concurrencyLevel) {
		super(new ConcurrentLinkedHashMap.Builder<E, Boolean>()
			.initialCapacity(initialCapacity)
			.maximumWeightedCapacity(initialCapacity)
			.concurrencyLevel(concurrencyLevel)
			.build());
	}

	/**
	 * 从已有集合中构造
	 *
	 * @param iter {@link Iterable}
	 */
	public ConcurrentLinkedHashSet(final Iterable<E> iter) {
		super(iter instanceof Collection ?
			new ConcurrentLinkedHashMap.Builder<E, Boolean>().initialCapacity(((Collection<E>) iter).size()).build() :
			new ConcurrentLinkedHashMap.Builder<E, Boolean>().build());
		if (iter instanceof Collection) {
			this.addAll((Collection<E>) iter);
		} else {
			for (final E e : iter) {
				this.add(e);
			}
		}
	}
	// ----------------------------------------------------------------------------------- Constructor end
}
