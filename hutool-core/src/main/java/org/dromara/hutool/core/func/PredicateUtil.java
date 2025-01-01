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

package org.dromara.hutool.core.func;

import org.dromara.hutool.core.stream.StreamUtil;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 一些{@link Predicate}相关封装
 *
 * @author Looly VampireAchao
 * @since 6.0.0
 */
public class PredicateUtil {

	/**
	 * <p>创建一个匹配任何方法的方法匹配器
	 *
	 * @param <T> 参数类型
	 * @return 方法匹配器
	 */
	public static <T> Predicate<T> alwaysTrue() {
		return method -> true;
	}

	/**
	 * 强制转换 {@code Predicate<? super T>} 为 {@code Predicate<T>}.
	 *
	 * @param <T>       参数类型
	 * @param predicate {@link Predicate}
	 * @return 强转后的{@link Predicate}
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	static <T> Predicate<T> coerce(final Predicate<? super T> predicate) {
		return (Predicate<T>) predicate;
	}

	/**
	 * 反向条件
	 *
	 * @param predicate 条件
	 * @param <T>       参数类型
	 * @return 反向条件 {@link Predicate}
	 */
	public static <T> Predicate<T> negate(final Predicate<T> predicate) {
		return predicate.negate();
	}

	/**
	 * 多个条件转换为”与“复合条件，即所有条件都为true时，才返回true
	 *
	 * @param <T>        判断条件的对象类型
	 * @param components 多个条件
	 * @return 复合条件
	 */
	public static <T> Predicate<T> and(final Iterable<Predicate<T>> components) {
		return StreamUtil.of(components, false).reduce(Predicate::and).orElseGet(() -> o -> true);
	}

	/**
	 * 多个条件转换为”与“复合条件，即所有条件都为true时，才返回true
	 *
	 * @param <T>        判断条件的对象类型
	 * @param components 多个条件
	 * @return 复合条件
	 */
	@SafeVarargs
	public static <T> Predicate<T> and(final Predicate<T>... components) {
		return StreamUtil.of(components).reduce(Predicate::and).orElseGet(() -> o -> true);
	}

	/**
	 * 多个条件转换为”或“复合条件，即任意一个条件都为true时，返回true
	 *
	 * @param <T>        判断条件的对象类型
	 * @param components 多个条件
	 * @return 复合条件
	 */
	public static <T> Predicate<T> or(final Iterable<Predicate<T>> components) {
		return StreamUtil.of(components, false).reduce(Predicate::or).orElseGet(() -> o -> false);
	}

	/**
	 * 多个条件转换为”或“复合条件，即任意一个条件都为true时，返回true
	 *
	 * @param <T>        判断条件的对象类型
	 * @param components 多个条件
	 * @return 复合条件
	 */
	@SafeVarargs
	public static <T> Predicate<T> or(final Predicate<T>... components) {
		return StreamUtil.of(components).reduce(Predicate::or).orElseGet(() -> o -> false);
	}

	/**
	 * 用于组合多个方法匹配器的方法匹配器，即所有条件都为false时，才返回true，也可理解为，任一条件为true时，返回false
	 *
	 * @param <T>        判断条件的对象类型
	 * @param components 多个条件
	 * @return 复合条件
	 */
	@SafeVarargs
	public static <T> Predicate<T> none(final Predicate<T>... components){
		return t -> Stream.of(components).noneMatch(matcher -> matcher.test(t));
	}
}
