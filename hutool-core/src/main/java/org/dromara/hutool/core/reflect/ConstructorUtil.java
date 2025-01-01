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

package org.dromara.hutool.core.reflect;

import org.dromara.hutool.core.exception.HutoolException;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.map.reference.WeakConcurrentMap;
import org.dromara.hutool.core.reflect.creator.DefaultObjectCreator;
import org.dromara.hutool.core.reflect.creator.PossibleObjectCreator;

import java.lang.reflect.Constructor;

/**
 * 反射中{@link Constructor}构造工具类，包括获取构造类和通过构造实例化对象相关工具
 *
 * @author Looly
 */
public class ConstructorUtil {
	/**
	 * 构造对象缓存
	 */
	private static final WeakConcurrentMap<Class<?>, Constructor<?>[]> CONSTRUCTORS_CACHE = new WeakConcurrentMap<>();

	/**
	 * 清除方法缓存
	 */
	synchronized static void clearCache() {
		CONSTRUCTORS_CACHE.clear();
	}

	// region ----- Constructor

	/**
	 * 查找类中的指定参数的构造方法，如果找到构造方法，会自动设置可访问为true
	 *
	 * @param <T>            对象类型
	 * @param clazz          类
	 * @param parameterTypes 参数类型，只要任何一个参数是指定参数的父类或接口或相等即可，此参数可以不传
	 * @return 构造方法，如果未找到返回null
	 */
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> getConstructor(final Class<T> clazz, final Class<?>... parameterTypes) {
		if (null == clazz) {
			return null;
		}

		final Constructor<?>[] constructors = getConstructors(clazz);
		Class<?>[] pts;
		for (final Constructor<?> constructor : constructors) {
			pts = constructor.getParameterTypes();
			if (ClassUtil.isAllAssignableFrom(pts, parameterTypes)) {
				// 构造可访问
				ReflectUtil.setAccessible(constructor);
				return (Constructor<T>) constructor;
			}
		}
		return null;
	}

	/**
	 * 获得一个类中所有构造列表
	 *
	 * @param <T>       构造的对象类型
	 * @param beanClass 类，非{@code null}
	 * @return 字段列表
	 * @throws SecurityException 安全检查异常
	 */
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T>[] getConstructors(final Class<T> beanClass) throws SecurityException {
		Assert.notNull(beanClass);
		return (Constructor<T>[]) CONSTRUCTORS_CACHE.computeIfAbsent(beanClass, (key) -> getConstructorsDirectly(beanClass));
	}

	/**
	 * 获得一个类中所有构造列表，直接反射获取，无缓存
	 *
	 * @param beanClass 类
	 * @return 字段列表
	 * @throws SecurityException 安全检查异常
	 */
	public static Constructor<?>[] getConstructorsDirectly(final Class<?> beanClass) throws SecurityException {
		return beanClass.getDeclaredConstructors();
	}

	// endregion

	// region ----- newInstance

	/**
	 * 实例化对象<br>
	 * 类必须有空构造函数
	 *
	 * @param <T>   对象类型
	 * @param clazz 类名
	 * @return 对象
	 * @throws HutoolException 包装各类异常
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(final String clazz) throws HutoolException {
		return (T) DefaultObjectCreator.of(clazz).create();
	}

	/**
	 * 实例化对象
	 *
	 * @param <T>    对象类型
	 * @param clazz  类
	 * @param params 构造函数参数
	 * @return 对象
	 * @throws HutoolException 包装各类异常
	 */
	public static <T> T newInstance(final Class<T> clazz, final Object... params) throws HutoolException {
		return DefaultObjectCreator.of(clazz, params).create();
	}

	/**
	 * 尝试遍历并调用此类的所有构造方法，直到构造成功并返回
	 * <p>
	 * 对于某些特殊的接口，按照其默认实现实例化，例如：
	 * <pre>
	 *     Map       -》 HashMap
	 *     Collction -》 ArrayList
	 *     List      -》 ArrayList
	 *     Set       -》 HashSet
	 * </pre>
	 *
	 * @param <T>  对象类型
	 * @param type 被构造的类
	 * @return 构造后的对象，构造失败返回{@code null}
	 */
	public static <T> T newInstanceIfPossible(final Class<T> type) {
		return PossibleObjectCreator.of(type).create();
	}
	// endregion
}
