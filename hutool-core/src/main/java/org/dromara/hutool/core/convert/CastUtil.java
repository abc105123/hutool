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

package org.dromara.hutool.core.convert;

import org.dromara.hutool.core.lang.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 转换工具类，提供集合、Map等向上向下转换工具
 *
 * @author feg545
 * @since 5.8.1
 */
public class CastUtil {

	/**
	 * 将指定对象强制转换为指定类型
	 *
	 * @param <T>        目标类型
	 * @param value      被转换的对象
	 * @return 转换后的对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cast(final Object value) {
		return (T)value;
	}

	/**
	 * 将指定对象强制转换为指定类型
	 *
	 * @param <T>        目标类型
	 * @param targetType 指定目标类型
	 * @param value      被转换的对象
	 * @return 转换后的对象
	 */
	public static <T> T castTo(final Class<T> targetType, final Object value) {
		return Assert.notNull(targetType).cast(value);
	}

	/**
	 * 泛型集合向上转型。例如将Collection&lt;Integer&gt;转换为Collection&lt;Number&gt;
	 *
	 * @param collection 集合
	 * @param <T>        元素类型
	 * @return 转换后的集合
	 */
	@SuppressWarnings("unchecked")
	public static <T> Collection<T> castUp(final Collection<? extends T> collection) {
		return (Collection<T>) collection;
	}

	/**
	 * 泛型集合向下转型。例如将Collection&lt;Number&gt;转换为Collection&lt;Integer&gt;
	 *
	 * @param collection 集合
	 * @param <T>        元素类型
	 * @return 转换后的集合
	 */
	@SuppressWarnings("unchecked")
	public static <T> Collection<T> castDown(final Collection<? super T> collection) {
		return (Collection<T>) collection;
	}

	/**
	 * 泛型集合向上转型。例如将Set&lt;Integer&gt;转换为Set&lt;Number&gt;
	 *
	 * @param set 集合
	 * @param <T> 泛型
	 * @return 泛化集合
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> castUp(final Set<? extends T> set) {
		return (Set<T>) set;
	}

	/**
	 * 泛型集合向下转型。例如将Set&lt;Number&gt;转换为Set&lt;Integer&gt;
	 *
	 * @param set 集合
	 * @param <T> 泛型子类
	 * @return 泛化集合
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> castDown(final Set<? super T> set) {
		return (Set<T>) set;
	}

	/**
	 * 泛型接口向上转型。例如将List&lt;Integer&gt;转换为List&lt;Number&gt;
	 *
	 * @param list 集合
	 * @param <T>  泛型的父类
	 * @return 泛化集合
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> castUp(final List<? extends T> list) {
		return (List<T>) list;
	}

	/**
	 * 泛型集合向下转型。例如将List&lt;Number&gt;转换为List&lt;Integer&gt;
	 *
	 * @param list 集合
	 * @param <T>  泛型的子类
	 * @return 泛化集合
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> castDown(final List<? super T> list) {
		return (List<T>) list;
	}

	/**
	 * 泛型集合向下转型。例如将Map&lt;Integer, Integer&gt;转换为Map&lt;Number,Number&gt;
	 *
	 * @param map 集合
	 * @param <K> 泛型父类
	 * @param <V> 泛型父类
	 * @return 泛化集合
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> castUp(final Map<? extends K, ? extends V> map) {
		return (Map<K, V>) map;
	}

	/**
	 * 泛型集合向下转型。例如将Map&lt;Number,Number&gt;转换为Map&lt;Integer, Integer&gt;
	 *
	 * @param map 集合
	 * @param <K> 泛型子类
	 * @param <V> 泛型子类
	 * @return 泛化集合
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> castDown(final Map<? super K, ? super V> map) {
		return (Map<K, V>) map;
	}
}
