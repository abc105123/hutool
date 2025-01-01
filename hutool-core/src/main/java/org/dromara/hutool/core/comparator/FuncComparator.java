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

package org.dromara.hutool.core.comparator;

import java.util.function.Function;

/**
 * 指定函数排序器
 *
 * @param <T> 被比较的对象
 * @author Looly
 */
public class FuncComparator<T> extends NullComparator<T> {
	private static final long serialVersionUID = 1L;

	/**
	 * 构造
	 *
	 * @param nullGreater 是否{@code null}在后
	 * @param compareSelf 在字段值相同情况下，是否比较对象本身。
	 *                    如果此项为{@code false}，字段值比较后为0会导致对象被认为相同，可能导致被去重。
	 * @param func        比较项获取函数
	 */
	public FuncComparator(final boolean nullGreater, final boolean compareSelf, final Function<T, Comparable<?>> func) {
		super(nullGreater, (a, b)->{
			// 通过给定函数转换对象为指定规则的可比较对象
			final Comparable<?> v1;
			final Comparable<?> v2;
			try {
				v1 = func.apply(a);
				v2 = func.apply(b);
			} catch (final Exception e) {
				throw new ComparatorException(e);
			}

			// 首先比较用户自定义的转换结果，如果为0，根据compareSelf参数决定是否比较对象本身。
			// compareSelf为false时，主要用于多规则比较，比如多字段比较的情况
			int result = CompareUtil.compare(v1, v2, nullGreater);
			if (compareSelf && 0 == result) {
				//避免TreeSet / TreeMap 过滤掉排序字段相同但是对象不相同的情况
				result = CompareUtil.compare(a, b, nullGreater);
			}
			return result;
		});
	}
}
