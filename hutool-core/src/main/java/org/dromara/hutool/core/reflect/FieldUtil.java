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

import org.dromara.hutool.core.annotation.Alias;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.convert.CompositeConverter;
import org.dromara.hutool.core.convert.Converter;
import org.dromara.hutool.core.exception.HutoolException;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.map.MapUtil;
import org.dromara.hutool.core.map.reference.WeakConcurrentMap;
import org.dromara.hutool.core.text.StrUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 反射中{@link Field}字段工具类，包括字段获取和字段赋值。
 *
 * @author Looly
 */
public class FieldUtil {

	/**
	 * 字段缓存
	 */
	private static final WeakConcurrentMap<Class<?>, FieldReflect> FIELDS_CACHE = new WeakConcurrentMap<>();

	/**
	 * 清除方法缓存
	 */
	synchronized static void clearCache() {
		FIELDS_CACHE.clear();
	}

	/**
	 * 是否为父类引用字段<br>
	 * 当字段所在类是对象子类时（对象中定义的非static的class），会自动生成一个以"this$0"为名称的字段，指向父类对象
	 *
	 * @param field 字段
	 * @return 是否为父类引用字段
	 * @since 5.7.20
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isOuterClassField(final Field field) {
		return "this$0".equals(field.getName());
	}

	// region ----- Field

	/**
	 * 查找指定类中是否包含指定名称对应的字段，包括所有字段（包括非public字段），也包括父类和Object类的字段
	 *
	 * @param beanClass 被查找字段的类,不能为null
	 * @param name      字段名
	 * @return 是否包含字段
	 * @throws SecurityException 安全异常
	 * @since 4.1.21
	 */
	public static boolean hasField(final Class<?> beanClass, final String name) throws SecurityException {
		return null != getField(beanClass, name);
	}

	/**
	 * 获取字段名，如果存在{@link Alias}注解，读取注解的值作为名称
	 *
	 * @param field    字段
	 * @return 字段名
	 */
	public static String getFieldName(final Field field) {
		return getFieldName(field, true);
	}

	/**
	 * 获取字段名，可选是否使用{@link Alias}注解，读取注解的值作为名称
	 *
	 * @param field    字段
	 * @param useAlias 是否检查并使用{@link Alias}注解
	 * @return 字段名
	 */
	public static String getFieldName(final Field field, final boolean useAlias) {
		if (null == field) {
			return null;
		}

		if (useAlias) {
			final Alias alias = field.getAnnotation(Alias.class);
			if (null != alias) {
				return alias.value();
			}
		}

		return field.getName();
	}

	/**
	 * 获取本类定义的指定名称的字段，包括私有字段，但是不包括父类字段
	 *
	 * @param beanClass Bean的Class
	 * @param name      字段名称
	 * @return 字段对象，如果未找到返回{@code null}
	 */
	public static Field getDeclaredField(final Class<?> beanClass, final String name) {
		final Field[] fields = getDeclaredFields(beanClass, (field -> StrUtil.equals(name, field.getName())));
		return ArrayUtil.isEmpty(fields) ? null : fields[0];
	}

	/**
	 * 查找指定类中的指定name的字段（包括非public字段），也包括父类和Object类的字段， 字段不存在则返回{@code null}
	 *
	 * @param beanClass 被查找字段的类,不能为null
	 * @param name      字段名
	 * @return 字段
	 * @throws SecurityException 安全异常
	 */
	public static Field getField(final Class<?> beanClass, final String name) throws SecurityException {
		final Field[] fields = getFields(beanClass, (field -> StrUtil.equals(name, field.getName())));
		return ArrayUtil.isEmpty(fields) ? null : fields[0];
	}

	/**
	 * 获取指定类中字段名和字段对应的有序Map，包括其父类中的字段<br>
	 * 如果子类与父类中存在同名字段，则父类字段忽略
	 *
	 * @param beanClass 类
	 * @return 字段名和字段对应的Map，有序
	 * @since 5.0.7
	 */
	public static Map<String, Field> getFieldMap(final Class<?> beanClass) {
		final Field[] fields = getFields(beanClass);
		final HashMap<String, Field> map = MapUtil.newHashMap(fields.length, true);
		for (final Field field : fields) {
			map.putIfAbsent(field.getName(), field);
		}
		return map;
	}

	/**
	 * 获得一个类中所有字段列表，包括其父类中的字段<br>
	 * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
	 *
	 * @param beanClass 类
	 * @return 字段列表
	 * @throws SecurityException 安全检查异常
	 */
	public static Field[] getFields(final Class<?> beanClass) throws SecurityException {
		return getFields(beanClass, null);
	}


	/**
	 * 获得一个类中所有满足条件的字段列表，包括其父类中的字段<br>
	 * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
	 *
	 * @param beanClass      类
	 * @param fieldPredicate field过滤器，过滤掉不需要的field，{@link Predicate#test(Object)}为{@code true}保留，null表示全部保留
	 * @return 字段列表
	 * @throws SecurityException 安全检查异常
	 * @since 5.7.14
	 */
	public static Field[] getFields(final Class<?> beanClass, final Predicate<Field> fieldPredicate) throws SecurityException {
		Assert.notNull(beanClass);
		return FIELDS_CACHE.computeIfAbsent(beanClass, FieldReflect::of).getAllFields(fieldPredicate);
	}

	/**
	 * 获得当前类声明的所有字段（包括非public字段），但不包括父类的字段<br>
	 *
	 * @param beanClass      类
	 * @param fieldPredicate field过滤器，过滤掉不需要的field，{@link Predicate#test(Object)}为{@code true}保留，null表示全部保留
	 * @return 字段列表
	 * @throws SecurityException 安全检查异常
	 * @since 6.0.0
	 */
	public static Field[] getDeclaredFields(final Class<?> beanClass, final Predicate<Field> fieldPredicate) throws SecurityException {
		Assert.notNull(beanClass);
		return FIELDS_CACHE.computeIfAbsent(beanClass, FieldReflect::of).getDeclaredFields(fieldPredicate);
	}

	/**
	 * 获得一个类中所有字段列表，直接反射获取，无缓存<br>
	 * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
	 *
	 * @param beanClass            类
	 * @param withSuperClassFields 是否包括父类的字段列表
	 * @return 字段列表
	 * @throws SecurityException 安全检查异常
	 */
	public static Field[] getFieldsDirectly(final Class<?> beanClass, final boolean withSuperClassFields) throws SecurityException {
		return FieldReflect.of(beanClass).getFieldsDirectly(withSuperClassFields);
	}

	// endregion

	// region ----- get Field Value

	/**
	 * 获取字段值
	 *
	 * @param obj       对象，如果static字段，此处为类
	 * @param fieldName 字段名
	 * @return 字段值
	 * @throws HutoolException 包装IllegalAccessException异常
	 */
	public static Object getFieldValue(final Object obj, final String fieldName) throws HutoolException {
		if (null == obj || StrUtil.isBlank(fieldName)) {
			return null;
		}
		return getFieldValue(obj, getField(obj instanceof Class ? (Class<?>) obj : obj.getClass(), fieldName));
	}

	/**
	 * 获取静态字段值
	 *
	 * @param field 字段
	 * @return 字段值
	 * @throws HutoolException 包装IllegalAccessException异常
	 * @since 5.1.0
	 */
	public static Object getStaticFieldValue(final Field field) throws HutoolException {
		return getFieldValue(null, field);
	}

	/**
	 * 获取字段值
	 *
	 * @param obj   对象，static字段则此字段为null
	 * @param field 字段
	 * @return 字段值
	 * @throws HutoolException 包装IllegalAccessException异常
	 */
	public static Object getFieldValue(Object obj, final Field field) throws HutoolException {
		if (null == field) {
			return null;
		}
		if (obj instanceof Class) {
			// 静态字段获取时对象为null
			obj = null;
		}

		ReflectUtil.setAccessible(field);
		final Object result;
		try {
			result = field.get(obj);
		} catch (final IllegalAccessException e) {
			throw new HutoolException(e, "IllegalAccess for {}.{}", field.getDeclaringClass(), field.getName());
		}
		return result;
	}

	/**
	 * 获取所有字段的值
	 *
	 * @param obj bean对象，如果是static字段，此处为类class
	 * @return 字段值数组
	 * @since 4.1.17
	 */
	public static Object[] getFieldsValue(final Object obj) {
		return getFieldsValue(obj, null);
	}

	/**
	 * 获取所有字段的值
	 *
	 * @param obj    bean对象，如果是static字段，此处为类class
	 * @param filter 字段过滤器，{@code null}返回原集合
	 * @return 字段值数组
	 * @since 5.8.23
	 */
	public static Object[] getFieldsValue(final Object obj, final Predicate<Field> filter) {
		if (null != obj) {
			final Field[] fields = getFields(obj instanceof Class ? (Class<?>) obj : obj.getClass(), filter);
			if (null != fields) {
				return ArrayUtil.mapToArray(fields, field -> getFieldValue(obj, field), Object[]::new);
			}
		}
		return null;
	}

	// endregion

	// region ----- set Field Value

	/**
	 * 设置字段值
	 *
	 * @param obj       对象,static字段则此处传Class
	 * @param fieldName 字段名
	 * @param value     值，值类型必须与字段类型匹配，不会自动转换对象类型
	 * @throws HutoolException 包装IllegalAccessException异常
	 */
	public static void setFieldValue(final Object obj, final String fieldName, final Object value) throws HutoolException {
		Assert.notNull(obj,  "Object must be not null !");
		Assert.notBlank(fieldName);

		final Field field = getField((obj instanceof Class) ? (Class<?>) obj : obj.getClass(), fieldName);
		Assert.notNull(field, "Field [{}] is not exist in [{}]", fieldName, obj.getClass().getName());
		setFieldValue(obj, field, value);
	}

	/**
	 * 设置静态（static）字段值
	 *
	 * @param field 字段
	 * @param value 值，值类型必须与字段类型匹配，不会自动转换对象类型
	 * @throws HutoolException UtilException 包装IllegalAccessException异常
	 */
	public static void setStaticFieldValue(final Field field, final Object value) throws HutoolException {
		setFieldValue(null, field, value);
	}

	/**
	 * 设置字段值，如果值类型必须与字段类型匹配，会自动转换对象类型
	 *
	 * @param obj   对象，如果是static字段，此参数为null
	 * @param field 字段
	 * @param value 值，类型不匹配会自动转换对象类型
	 * @throws HutoolException UtilException 包装IllegalAccessException异常
	 */
	public static void setFieldValue(final Object obj, final Field field, final Object value) throws HutoolException {
		setFieldValue(obj, field, value, CompositeConverter.getInstance());
	}

	/**
	 * 设置字段值，如果值类型必须与字段类型匹配，会自动转换对象类型
	 *
	 * @param obj   对象，如果是static字段，此参数为null
	 * @param field 字段
	 * @param value 值，类型不匹配会自动转换对象类型
	 * @param converter 转换器，用于转换给定value为字段类型，{@code null}表示不转换
	 * @throws HutoolException UtilException 包装IllegalAccessException异常
	 */
	public static void setFieldValue(final Object obj, final Field field, final Object value, final Converter converter) throws HutoolException {
		Assert.notNull(field, "Field in [{}] not exist !", obj);

		FieldInvoker.of(field).setConverter(converter).invokeSet(obj, value);
	}
	// endregion
}
