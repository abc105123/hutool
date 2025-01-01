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

package org.dromara.hutool.core.collection;


import org.dromara.hutool.core.collection.set.SetUtil;
import org.dromara.hutool.core.func.SerFunction;
import org.dromara.hutool.core.lang.Opt;
import org.dromara.hutool.core.map.MapUtil;
import org.dromara.hutool.core.stream.CollectorUtil;
import org.dromara.hutool.core.stream.StreamUtil;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 集合的stream操作封装
 *
 * @author 528910437@QQ.COM, VampireAchao&lt;achao1441470436@gmail.com&gt;;Lion Li&gt;
 * @since 5.5.2
 */
public class CollStreamUtil {

	/**
	 * 将collection转化为类型不变的map<br>
	 * <B>{@code Collection<V>  ---->  Map<K,V>}</B>
	 *
	 * @param collection 需要转化的集合
	 * @param key        V类型转化为K类型的lambda方法
	 * @param <V>        collection中的泛型
	 * @param <K>        map中的key类型
	 * @return 转化后的map
	 */
	public static <V, K> Map<K, V> toIdentityMap(final Collection<V> collection, final SerFunction<V, K> key) {
		return toIdentityMap(collection, key, false);
	}


	/**
	 * 将collection转化为类型不变的map<br>
	 * <B>{@code Collection<V>  ---->  Map<K,V>}</B>
	 *
	 * @param collection 需要转化的集合
	 * @param key        V类型转化为K类型的lambda方法
	 * @param isParallel 是否并行流
	 * @param <V>        collection中的泛型
	 * @param <K>        map中的key类型
	 * @return 转化后的map
	 */
	public static <V, K> Map<K, V> toIdentityMap(final Collection<V> collection, final SerFunction<V, K> key, final boolean isParallel) {
		if (CollUtil.isEmpty(collection)) {
			return MapUtil.zero();
		}
		return toMap(collection, (v) -> Opt.ofNullable(v).map(key).getOrNull(), Function.identity(), isParallel);
	}

	/**
	 * 将Collection转化为map(value类型与collection的泛型不同)<br>
	 * <B>{@code Collection<E> -----> Map<K,V>  }</B>
	 *
	 * @param collection 需要转化的集合
	 * @param key        E类型转化为K类型的lambda方法
	 * @param value      E类型转化为V类型的lambda方法
	 * @param <E>        collection中的泛型
	 * @param <K>        map中的key类型
	 * @param <V>        map中的value类型
	 * @return 转化后的map
	 */
	public static <E, K, V> Map<K, V> toMap(final Collection<E> collection, final Function<E, K> key, final Function<E, V> value) {
		return toMap(collection, key, value, false);
	}

	/**
	 * @param collection 需要转化的集合
	 * @param key        E类型转化为K类型的lambda方法
	 * @param value      E类型转化为V类型的lambda方法
	 * @param isParallel 是否并行流
	 * @param <E>        collection中的泛型
	 * @param <K>        map中的key类型
	 * @param <V>        map中的value类型
	 * @return 转化后的map
	 */
	public static <E, K, V> Map<K, V> toMap(final Collection<E> collection, final Function<E, K> key, final Function<E, V> value, final boolean isParallel) {
		if (CollUtil.isEmpty(collection)) {
			return MapUtil.zero();
		}
		return StreamUtil.of(collection, isParallel)
			.collect(HashMap::new, (m, v) -> m.put(key.apply(v), value.apply(v)), HashMap::putAll);
	}


	/**
	 * 将collection按照规则(比如有相同的班级id)分组成map<br>
	 * <B>{@code Collection<E> -------> Map<K,List<E>> } </B>
	 *
	 * @param collection 需要分组的集合
	 * @param key        分组的规则
	 * @param <E>        collection中的泛型
	 * @param <K>        map中的key类型
	 * @return 分组后的map
	 */
	public static <E, K> Map<K, List<E>> groupByKey(final Collection<E> collection, final Function<E, K> key) {
		return groupByKey(collection, key, false);
	}

	/**
	 * 将collection按照规则(比如有相同的班级id)分组成map<br>
	 * <B>{@code Collection<E> -------> Map<K,List<E>> } </B>
	 *
	 * @param collection 需要分组的集合
	 * @param key        键分组的规则
	 * @param isParallel 是否并行流
	 * @param <E>        collection中的泛型
	 * @param <K>        map中的key类型
	 * @return 分组后的map
	 */
	public static <E, K> Map<K, List<E>> groupByKey(final Collection<E> collection, final Function<E, K> key, final boolean isParallel) {
		if (CollUtil.isEmpty(collection)) {
			return MapUtil.zero();
		}
		return groupBy(collection, key, Collectors.toList(), isParallel);
	}

	/**
	 * 将collection按照两个规则(比如有相同的年级id,班级id)分组成双层map<br>
	 * <B>{@code Collection<E>  --->  Map<T,Map<U,List<E>>> } </B>
	 *
	 * @param collection 需要分组的集合
	 * @param key1       第一个分组的规则
	 * @param key2       第二个分组的规则
	 * @param <E>        集合元素类型
	 * @param <K>        第一个map中的key类型
	 * @param <U>        第二个map中的key类型
	 * @return 分组后的map
	 */
	public static <E, K, U> Map<K, Map<U, List<E>>> groupBy2Key(final Collection<E> collection, final Function<E, K> key1, final Function<E, U> key2) {
		return groupBy2Key(collection, key1, key2, false);
	}


	/**
	 * 将collection按照两个规则(比如有相同的年级id,班级id)分组成双层map<br>
	 * <B>{@code Collection<E>  --->  Map<T,Map<U,List<E>>> } </B>
	 *
	 * @param collection 需要分组的集合
	 * @param key1       第一个分组的规则
	 * @param key2       第二个分组的规则
	 * @param isParallel 是否并行流
	 * @param <E>        集合元素类型
	 * @param <K>        第一个map中的key类型
	 * @param <U>        第二个map中的key类型
	 * @return 分组后的map
	 */
	public static <E, K, U> Map<K, Map<U, List<E>>> groupBy2Key(final Collection<E> collection, final Function<E, K> key1,
																final Function<E, U> key2, final boolean isParallel) {
		if (CollUtil.isEmpty(collection)) {
			return MapUtil.zero();
		}
		return groupBy(collection, key1, CollectorUtil.groupingBy(key2, Collectors.toList()), isParallel);
	}

	/**
	 * 将collection按照两个规则(比如有相同的年级id,班级id)分组成双层map<br>
	 * <B>{@code Collection<E>  --->  Map<T,Map<U,E>> } </B>
	 *
	 * @param collection 需要分组的集合
	 * @param key1       第一个分组的规则
	 * @param key2       第二个分组的规则
	 * @param <T>        第一个map中的key类型
	 * @param <U>        第二个map中的key类型
	 * @param <E>        collection中的泛型
	 * @return 分组后的map
	 */
	public static <E, T, U> Map<T, Map<U, E>> group2Map(final Collection<E> collection, final Function<E, T> key1, final Function<E, U> key2) {
		return group2Map(collection, key1, key2, false);
	}

	/**
	 * 将collection按照两个规则(比如有相同的年级id,班级id)分组成双层map<br>
	 * <B>{@code Collection<E>  --->  Map<T,Map<U,E>> } </B>
	 *
	 * @param collection 需要分组的集合
	 * @param key1       第一个分组的规则
	 * @param key2       第二个分组的规则
	 * @param isParallel 是否并行流
	 * @param <T>        第一个map中的key类型
	 * @param <U>        第二个map中的key类型
	 * @param <E>        collection中的泛型
	 * @return 分组后的map
	 */
	public static <E, T, U> Map<T, Map<U, E>> group2Map(final Collection<E> collection,
														final Function<E, T> key1, final Function<E, U> key2, final boolean isParallel) {
		if (CollUtil.isEmpty(collection) || key1 == null || key2 == null) {
			return MapUtil.zero();
		}
		return groupBy(collection, key1, CollectorUtil.toMap(key2, Function.identity(), (l, r) -> l), isParallel);
	}

	/**
	 * 将collection按照规则(比如有相同的班级id)分组成map，map中的key为班级id，value为班级名<br>
	 * <B>{@code Collection<E> -------> Map<K,List<V>> } </B>
	 *
	 * @param collection 需要分组的集合
	 * @param key        键分组的规则
	 * @param value      值分组的规则
	 * @param <E>        collection中的泛型
	 * @param <K>        map中的key类型
	 * @param <V>        List中的value类型
	 * @return 分组后的map
	 */
	public static <E, K, V> Map<K, List<V>> groupKeyValue(final Collection<E> collection, final SerFunction<E, K> key,
														  final SerFunction<E, V> value) {
		return groupKeyValue(collection, key, value, false);
	}

	/**
	 * 将collection按照规则(比如有相同的班级id)分组成map，map中的key为班级id，value为班级名<br>
	 * <B>{@code Collection<E> -------> Map<K,List<V>> } </B>
	 *
	 * @param collection 需要分组的集合
	 * @param key        键分组的规则
	 * @param value      值分组的规则
	 * @param isParallel 是否并行流
	 * @param <E>        collection中的泛型
	 * @param <K>        map中的key类型
	 * @param <V>        List中的value类型
	 * @return 分组后的map
	 */
	public static <E, K, V> Map<K, List<V>> groupKeyValue(final Collection<E> collection, final SerFunction<E, K> key,
														  final SerFunction<E, V> value, final boolean isParallel) {
		if (CollUtil.isEmpty(collection)) {
			return MapUtil.zero();
		}
		return groupBy(collection, key, Collectors.mapping(v -> Opt.ofNullable(v).map(value).orElse(null), Collectors.toList()), isParallel);
	}

	/**
	 * 作为所有groupingBy的公共方法，更接近于原生，灵活性更强
	 *
	 * @param collection 需要分组的集合
	 * @param key        第一次分组时需要的key
	 * @param downstream 分组后需要进行的操作
	 * @param <E>        collection中的泛型
	 * @param <K>        map中的key类型
	 * @param <D>        后续操作的返回值
	 * @return 分组后的map
	 * @since 5.7.18
	 */
	public static <E, K, D> Map<K, D> groupBy(final Collection<E> collection, final Function<E, K> key, final Collector<E, ?, D> downstream) {
		if (CollUtil.isEmpty(collection)) {
			return MapUtil.zero();
		}
		return groupBy(collection, key, downstream, false);
	}

	/**
	 * 作为所有groupingBy的公共方法，更接近于原生，灵活性更强
	 *
	 * @param collection 需要分组的集合
	 * @param key        第一次分组时需要的key
	 * @param downstream 分组后需要进行的操作
	 * @param isParallel 是否并行流
	 * @param <E>        collection中的泛型
	 * @param <K>        map中的key类型
	 * @param <D>        后续操作的返回值
	 * @return 分组后的map
	 * @see Collectors#groupingBy(Function, Collector)
	 * @since 5.7.18
	 */
	public static <E, K, D> Map<K, D> groupBy(final Collection<E> collection, final Function<E, K> key, final Collector<E, ?, D> downstream, final boolean isParallel) {
		if (CollUtil.isEmpty(collection)) {
			return MapUtil.zero();
		}
		return StreamUtil.of(collection, isParallel).collect(CollectorUtil.groupingBy(key, downstream));
	}

	/**
	 * 将collection转化为List集合，但是两者的泛型不同<br>
	 * <B>{@code Collection<E>  ------>  List<T> } </B>
	 *
	 * @param collection 需要转化的集合
	 * @param function   collection中的泛型转化为list泛型的lambda表达式
	 * @param <E>        collection中的泛型
	 * @param <T>        List中的泛型
	 * @return 转化后的list
	 */
	public static <E, T> List<T> toList(final Collection<E> collection, final Function<E, T> function) {
		return toList(collection, function, false);
	}

	/**
	 * 将collection转化为List集合，但是两者的泛型不同<br>
	 * <B>{@code Collection<E>  ------>  List<T> } </B>
	 *
	 * @param collection 需要转化的集合
	 * @param function   collection中的泛型转化为list泛型的lambda表达式
	 * @param isParallel 是否并行流
	 * @param <E>        collection中的泛型
	 * @param <T>        List中的泛型
	 * @return 转化后的list
	 */
	public static <E, T> List<T> toList(final Collection<E> collection, final Function<E, T> function, final boolean isParallel) {
		if (CollUtil.isEmpty(collection)) {
			return ListUtil.zero();
		}
		return StreamUtil.of(collection, isParallel)
			.map(function)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	/**
	 * 将collection转化为Set集合，但是两者的泛型不同<br>
	 * <B>{@code Collection<E>  ------>  Set<T> } </B>
	 *
	 * @param collection 需要转化的集合
	 * @param function   collection中的泛型转化为set泛型的lambda表达式
	 * @param <E>        collection中的泛型
	 * @param <T>        Set中的泛型
	 * @return 转化后的Set
	 */
	public static <E, T> Set<T> toSet(final Collection<E> collection, final Function<E, T> function) {
		return toSet(collection, function, false);
	}

	/**
	 * 将collection转化为Set集合，但是两者的泛型不同<br>
	 * <B>{@code Collection<E>  ------>  Set<T> } </B>
	 *
	 * @param collection 需要转化的集合
	 * @param function   collection中的泛型转化为set泛型的lambda表达式
	 * @param isParallel 是否并行流
	 * @param <E>        collection中的泛型
	 * @param <T>        Set中的泛型
	 * @return 转化后的Set
	 */
	public static <E, T> Set<T> toSet(final Collection<E> collection, final Function<E, T> function, final boolean isParallel) {
		if (CollUtil.isEmpty(collection)) {
			return SetUtil.zero();
		}
		return StreamUtil.of(collection, isParallel)
			.map(function)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
	}


	/**
	 * 合并两个相同key类型的map
	 *
	 * @param map1  第一个需要合并的 map
	 * @param map2  第二个需要合并的 map
	 * @param merge 合并的lambda，将key  value1 value2合并成最终的类型,注意value可能为空的情况
	 * @param <K>   map中的key类型
	 * @param <X>   第一个 map的value类型
	 * @param <Y>   第二个 map的value类型
	 * @param <V>   最终map的value类型
	 * @return 合并后的map
	 */
	public static <K, X, Y, V> Map<K, V> merge(Map<K, X> map1, Map<K, Y> map2, final BiFunction<X, Y, V> merge) {
		if (MapUtil.isEmpty(map1) && MapUtil.isEmpty(map2)) {
			return MapUtil.zero();
		} else if (MapUtil.isEmpty(map1)) {
			map1 = MapUtil.empty();
		} else if (MapUtil.isEmpty(map2)) {
			map2 = MapUtil.empty();
		}
		final Set<K> key = new HashSet<>();
		key.addAll(map1.keySet());
		key.addAll(map2.keySet());
		final Map<K, V> map = MapUtil.newHashMap(key.size());
		for (final K t : key) {
			final X x = map1.get(t);
			final Y y = map2.get(t);
			final V z = merge.apply(x, y);
			if (z != null) {
				map.put(t, z);
			}
		}
		return map;
	}

	/**
	 * 笛卡尔积<br>
	 * 参考：https://www.baeldung-cn.com/java-cartesian-product-sets
	 *
	 * @param sets  集合列表
	 * @param index 索引
	 * @return 笛卡尔积
	 */
	public static Stream<List<Object>> cartesianProduct(final List<List<Object>> sets, final int index) {
		if (index == sets.size()) {
			return Stream.of(ListUtil.zero());
		}
		final List<Object> currentSet = sets.get(index);
		return currentSet.stream().flatMap(element -> cartesianProduct(sets, index + 1)
			.map(list -> {
				final List<Object> newList = new ArrayList<>(list);
				newList.add(0, element);
				return newList;
			}));
	}
}
