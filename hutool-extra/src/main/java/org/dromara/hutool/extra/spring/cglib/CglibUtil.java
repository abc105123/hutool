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

package org.dromara.hutool.extra.spring.cglib;

import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.reflect.ConstructorUtil;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.cglib.core.Converter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Cglib工具类
 *
 * @author Looly
 * @since 5.4.1
 */
public class CglibUtil {

	/**
	 * 拷贝Bean对象属性到目标类型<br>
	 * 此方法通过指定目标类型自动创建之，然后拷贝属性
	 *
	 * @param <T>         目标对象类型
	 * @param source      源bean对象
	 * @param targetClass 目标bean类，自动实例化此对象
	 * @return 目标对象
	 */
	public static <T> T copy(final Object source, final Class<T> targetClass) {
		return copy(source, targetClass, null);
	}

	/**
	 * 拷贝Bean对象属性<br>
	 * 此方法通过指定目标类型自动创建之，然后拷贝属性
	 *
	 * @param <T>         目标对象类型
	 * @param source      源bean对象
	 * @param targetClass 目标bean类，自动实例化此对象
	 * @param converter   转换器，无需可传{@code null}
	 * @return 目标对象
	 */
	public static <T> T copy(final Object source, final Class<T> targetClass, final Converter converter) {
		final T target = ConstructorUtil.newInstanceIfPossible(targetClass);
		copy(source, target, converter);
		return target;
	}

	/**
	 * 拷贝Bean对象属性
	 *
	 * @param source 源bean对象
	 * @param target 目标bean对象
	 */
	public static void copy(final Object source, final Object target) {
		copy(source, target, null);
	}

	/**
	 * 拷贝Bean对象属性
	 *
	 * @param source    源bean对象
	 * @param target    目标bean对象
	 * @param converter 转换器，无需可传{@code null}
	 */
	public static void copy(final Object source, final Object target, final Converter converter) {
		Assert.notNull(source, "Source bean must be not null.");
		Assert.notNull(target, "Target bean must be not null.");

		final Class<?> sourceClass = source.getClass();
		final Class<?> targetClass = target.getClass();
		final BeanCopier beanCopier = BeanCopierCache.INSTANCE.get(sourceClass, targetClass, converter);

		beanCopier.copy(source, target, converter);
	}

	/**
	 * 拷贝List Bean对象属性
	 *
	 * @param <S>    源bean类型
	 * @param <T>    目标bean类型
	 * @param source 源bean对象list
	 * @param target 目标bean对象
	 * @return 目标bean对象list
	 */
	public static <S, T> List<T> copyList(final Collection<S> source, final Supplier<T> target) {
		return copyList(source, target, null, null);
	}

	/**
	 * 拷贝List Bean对象属性
	 *
	 * @param source    源bean对象list
	 * @param target    目标bean对象
	 * @param converter 转换器，无需可传{@code null}
	 * @param <S>       源bean类型
	 * @param <T>       目标bean类型
	 * @return 目标bean对象list
	 * @since 5.4.1
	 */
	public static <S, T> List<T> copyList(final Collection<S> source, final Supplier<T> target, final Converter converter) {
		return copyList(source, target, converter, null);
	}

	/**
	 * 拷贝List Bean对象属性
	 *
	 * @param source   源bean对象list
	 * @param target   目标bean对象
	 * @param callback 回调对象
	 * @param <S>      源bean类型
	 * @param <T>      目标bean类型
	 * @return 目标bean对象list
	 * @since 5.4.1
	 */
	public static <S, T> List<T> copyList(final Collection<S> source, final Supplier<T> target, final BiConsumer<S, T> callback) {
		return copyList(source, target, null, callback);
	}

	/**
	 * 拷贝List Bean对象属性
	 *
	 * @param source    源bean对象list
	 * @param target    目标bean对象
	 * @param converter 转换器，无需可传{@code null}
	 * @param callback  回调对象
	 * @param <S>       源bean类型
	 * @param <T>       目标bean类型
	 * @return 目标bean对象list
	 */
	public static <S, T> List<T> copyList(final Collection<S> source, final Supplier<T> target, final Converter converter, final BiConsumer<S, T> callback) {
		return source.stream().map(s -> {
			final T t = target.get();
			copy(s, t, converter);
			if (callback != null) {
				callback.accept(s, t);
			}
			return t;
		}).collect(Collectors.toList());
	}

	/**
	 * 将Bean转换为Map
	 *
	 * @param bean Bean对象
	 * @return {@link BeanMap}
	 * @since 5.4.1
	 */
	public static BeanMap toMap(final Object bean) {
		return BeanMap.create(bean);
	}

	/**
	 * 将Map中的内容填充至Bean中
	 * @param map Map
	 * @param bean Bean
	 * @param <T> Bean类型
	 * @return bean
	 * @since 5.6.3
	 */
	@SuppressWarnings("rawtypes")
	public static <T> T fillBean(final Map map, final T bean){
		BeanMap.create(bean).putAll(map);
		return bean;
	}

	/**
	 * 将Map转换为Bean
	 * @param map Map
	 * @param beanClass Bean类
	 * @param <T> Bean类型
	 * @return bean
	 * @since 5.6.3
	 */
	@SuppressWarnings("rawtypes")
	public static <T> T toBean(final Map map, final Class<T> beanClass){
		return fillBean(map, ConstructorUtil.newInstanceIfPossible(beanClass));
	}
}
