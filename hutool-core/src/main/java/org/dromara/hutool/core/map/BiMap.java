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

package org.dromara.hutool.core.map;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 双向Map<br>
 * 互换键值对不检查值是否有重复，如果有则后加入的元素替换先加入的元素<br>
 * 值的顺序在HashMap中不确定，所以谁覆盖谁也不确定，在有序的Map中按照先后顺序覆盖，保留最后的值<br>
 * 它与TableMap的区别是，BiMap维护两个Map实现高效的正向和反向查找
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @since 5.2.6
 */
public class BiMap<K, V> extends MapWrapper<K, V> {
	private static final long serialVersionUID = 1L;

	private Map<V, K> inverse;

	/**
	 * 构造
	 *
	 * @param raw 被包装的Map
	 */
	public BiMap(final Map<K, V> raw) {
		super(raw);
	}

	@Override
	public V put(final K key, final V value) {
		final V oldValue = super.put(key, value);
		if (null != this.inverse) {
			if(null != oldValue){
				// issue#I88R5M
				// 如果put的key相同，value不同，需要在inverse中移除旧的关联
				this.inverse.remove(oldValue);
			}
			this.inverse.put(value, key);
		}
		return oldValue;
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> m) {
		super.putAll(m);
		if (null != this.inverse) {
			m.forEach((key, value) -> this.inverse.put(value, key));
		}
	}

	@Override
	public V remove(final Object key) {
		final V v = super.remove(key);
		if(null != this.inverse && null != v){
			this.inverse.remove(v);
		}
		return v;
	}

	@Override
	public boolean remove(final Object key, final Object value) {
		return super.remove(key, value) && null != this.inverse && this.inverse.remove(value, key);
	}

	@Override
	public void clear() {
		super.clear();
		this.inverse = null;
	}

	/**
	 * 获取反向Map
	 *
	 * @return 反向Map
	 */
	public Map<V, K> getInverse() {
		if (null == this.inverse) {
			inverse = MapUtil.inverse(getRaw());
		}
		return this.inverse;
	}

	/**
	 * 根据值获得键
	 *
	 * @param value 值
	 * @return 键
	 */
	public K getKey(final V value) {
		return getInverse().get(value);
	}

	@Override
	public V putIfAbsent(final K key, final V value) {
		if (null != this.inverse) {
			this.inverse.putIfAbsent(value, key);
		}
		return super.putIfAbsent(key, value);
	}

	@Override
	public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
		final V result = super.computeIfAbsent(key, mappingFunction);
		resetInverseMap();
		return result;
	}

	@Override
	public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		final V result = super.computeIfPresent(key, remappingFunction);
		resetInverseMap();
		return result;
	}

	@Override
	public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		final V result = super.compute(key, remappingFunction);
		resetInverseMap();
		return result;
	}

	@Override
	public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		final V result = super.merge(key, value, remappingFunction);
		resetInverseMap();
		return result;
	}

	/**
	 * 重置反转的Map，如果反转map为空，则不操作。
	 */
	private void resetInverseMap() {
		if (null != this.inverse) {
			inverse = null;
		}
	}
}
