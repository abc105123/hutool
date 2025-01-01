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

package org.dromara.hutool.core.reflect.creator;

import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.reflect.ClassUtil;
import org.dromara.hutool.core.reflect.ConstructorUtil;
import org.dromara.hutool.core.reflect.ReflectUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * 尝试方式对象实例化器<br>
 * 通过判断类型或调用可能的构造，构建对象，支持：
 * <ul>
 *     <li>原始类型</li>
 *     <li>接口或抽象类型</li>
 *     <li>枚举</li>
 *     <li>数组</li>
 *     <li>使用默认参数的构造方法</li>
 * </ul>
 * <p>
 * 对于接口或抽象类型，构造其默认实现：
 * <pre>
 *     Map       -》 HashMap
 *     Collction -》 ArrayList
 *     List      -》 ArrayList
 *     Set       -》 HashSet
 * </pre>
 *
 * @param <T> 对象类型
 */
public class PossibleObjectCreator<T> implements ObjectCreator<T> {

	/**
	 * 创建默认的对象实例化器
	 *
	 * @param clazz 实例化的类
	 * @param <T>   对象类型
	 * @return DefaultObjectCreator
	 */
	public static <T> PossibleObjectCreator<T> of(final Class<T> clazz) {
		return new PossibleObjectCreator<>(clazz);
	}

	final Class<T> clazz;

	/**
	 * 构造
	 *
	 * @param clazz 实例化的类
	 */
	public PossibleObjectCreator(final Class<T> clazz) {
		this.clazz = Assert.notNull(clazz);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T create() {
		Class<T> type = this.clazz;

		// 原始类型
		if (type.isPrimitive()) {
			return (T) ClassUtil.getPrimitiveDefaultValue(type);
		}

		// 处理接口和抽象类的默认值
		type = (Class<T>) resolveType(type);

		// 尝试默认构造实例化
		try {
			return DefaultObjectCreator.of(type).create();
		} catch (final Exception e) {
			// ignore
			// 默认构造不存在的情况下查找其它构造
		}

		// 枚举
		if (type.isEnum()) {
			return type.getEnumConstants()[0];
		}

		// 数组
		if (type.isArray()) {
			return (T) Array.newInstance(type.getComponentType(), 0);
		}

		// 查找合适构造
		final Constructor<T>[] constructors = ConstructorUtil.getConstructors(type);
		Class<?>[] parameterTypes;
		for (final Constructor<T> constructor : constructors) {
			parameterTypes = constructor.getParameterTypes();
			if (0 == parameterTypes.length) {
				continue;
			}
			ReflectUtil.setAccessible(constructor);
			try {
				return constructor.newInstance(ClassUtil.getDefaultValues(parameterTypes));
			} catch (final Exception ignore) {
				// 构造出错时继续尝试下一种构造方式
			}
		}
		return null;
	}

	/**
	 * 某些特殊接口的实例化按照默认实现进行
	 *
	 * @param type 类型
	 * @return 默认类型
	 */
	private static Class<?> resolveType(final Class<?> type) {
		if (type.isAssignableFrom(AbstractMap.class)) {
			return HashMap.class;
		} else if (type.isAssignableFrom(List.class)) {
			return ArrayList.class;
		} else if (type == SortedSet.class) {
			return TreeSet.class;
		} else if (type.isAssignableFrom(Set.class)) {
			return HashSet.class;
		}

		return type;
	}
}
