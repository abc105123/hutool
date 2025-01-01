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

package org.dromara.hutool.core.bean;

import org.dromara.hutool.core.bean.copier.ValueProvider;
import org.dromara.hutool.core.classloader.ClassLoaderUtil;
import org.dromara.hutool.core.reflect.ConstructorUtil;
import org.dromara.hutool.core.reflect.method.MethodUtil;
import org.dromara.hutool.core.util.JdkUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Map;

/**
 * java.lang.Record 相关工具类封装<br>
 * 来自于FastJSON2的BeanUtils
 *
 * @author fastjson2, Looly
 * @since 6.0.0
 */
public class RecordUtil {

	private static volatile Class<?> RECORD_CLASS;

	private static volatile Method METHOD_GET_RECORD_COMPONENTS;
	private static volatile Method METHOD_COMPONENT_GET_NAME;
	private static volatile Method METHOD_COMPONENT_GET_GENERIC_TYPE;

	/**
	 * 判断给定类是否为Record类
	 *
	 * @param clazz 类
	 * @return 是否为Record类
	 */
	public static boolean isRecord(final Class<?> clazz) {
		if (JdkUtil.JVM_VERSION < 14) {
			// JDK14+支持Record类
			return false;
		}
		final Class<?> superClass = clazz.getSuperclass();
		if (superClass == null) {
			return false;
		}

		if (RECORD_CLASS == null) {
			// 此处不使用同步代码，重复赋值并不影响判断
			final String superclassName = superClass.getName();
			if ("java.lang.Record".equals(superclassName)) {
				RECORD_CLASS = superClass;
				return true;
			} else {
				return false;
			}
		}

		return superClass == RECORD_CLASS;
	}

	/**
	 * 获取Record类中所有字段名称，getter方法名与字段同名
	 *
	 * @param recordClass Record类
	 * @return 字段数组
	 */
	@SuppressWarnings("unchecked")
	public static Map.Entry<String, Type>[] getRecordComponents(final Class<?> recordClass) {
		if (JdkUtil.JVM_VERSION < 14) {
			// JDK14+支持Record类
			return new Map.Entry[0];
		}
		if (null == METHOD_GET_RECORD_COMPONENTS) {
			METHOD_GET_RECORD_COMPONENTS = MethodUtil.getMethod(Class.class, "getRecordComponents");
		}

		final Class<Object> recordComponentClass = ClassLoaderUtil.loadClass("java.lang.reflect.RecordComponent");
		if (METHOD_COMPONENT_GET_NAME == null) {
			METHOD_COMPONENT_GET_NAME = MethodUtil.getMethod(recordComponentClass, "getName");
		}
		if (METHOD_COMPONENT_GET_GENERIC_TYPE == null) {
			METHOD_COMPONENT_GET_GENERIC_TYPE = MethodUtil.getMethod(recordComponentClass, "getGenericType");
		}

		final Object[] components = MethodUtil.invoke(recordClass, METHOD_GET_RECORD_COMPONENTS);
		final Map.Entry<String, Type>[] entries = new Map.Entry[components.length];
		for (int i = 0; i < components.length; i++) {
			entries[i] = new AbstractMap.SimpleEntry<>(
				MethodUtil.invoke(components[i], METHOD_COMPONENT_GET_NAME),
				MethodUtil.invoke(components[i], METHOD_COMPONENT_GET_GENERIC_TYPE)
			);
		}

		return entries;
	}

	/**
	 * 实例化Record类
	 *
	 * @param recordClass   类
	 * @param valueProvider 参数值提供器
	 * @return Record类
	 */
	public static Object newInstance(final Class<?> recordClass, final ValueProvider<String> valueProvider) {
		final Map.Entry<String, Type>[] recordComponents = getRecordComponents(recordClass);
		final Object[] args = new Object[recordComponents.length];
		for (int i = 0; i < args.length; i++) {
			args[i] = valueProvider.value(recordComponents[i].getKey(), recordComponents[i].getValue());
		}

		return ConstructorUtil.newInstance(recordClass, args);
	}
}
