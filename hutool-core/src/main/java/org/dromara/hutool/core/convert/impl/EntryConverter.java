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

package org.dromara.hutool.core.convert.impl;

import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.convert.ConvertException;
import org.dromara.hutool.core.convert.Converter;
import org.dromara.hutool.core.convert.ConverterWithRoot;
import org.dromara.hutool.core.convert.MatcherConverter;
import org.dromara.hutool.core.lang.tuple.Pair;
import org.dromara.hutool.core.lang.wrapper.Wrapper;
import org.dromara.hutool.core.map.MapUtil;
import org.dromara.hutool.core.reflect.ConstructorUtil;
import org.dromara.hutool.core.reflect.TypeReference;
import org.dromara.hutool.core.reflect.TypeUtil;
import org.dromara.hutool.core.text.CharUtil;
import org.dromara.hutool.core.text.StrUtil;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * {@link Map.Entry} 转换器，支持以下类型转为Entry
 * <ul>
 *     <li>{@link Map}</li>
 *     <li>{@link Map.Entry}</li>
 *     <li>带分隔符的字符串，支持分隔符{@code :}、{@code =}、{@code ,}</li>
 *     <li>Bean，包含{@code getKey}和{@code getValue}方法</li>
 * </ul>
 *
 * @author Looly
 */
public class EntryConverter extends ConverterWithRoot implements MatcherConverter, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 构造
	 *
	 * @param converter 转换器，用于将Entry中key和value转换为指定类型的对象
	 */
	public EntryConverter(final Converter converter) {
		super(converter);
	}

	@Override
	public boolean match(final Type targetType, final Class<?> rawType, final Object value) {
		return Map.Entry.class.isAssignableFrom(rawType);
	}

	@Override
	public Object convert(Type targetType, final Object value) throws ConvertException {
		if (targetType instanceof TypeReference) {
			targetType = ((TypeReference<?>) targetType).getType();
		}
		final Type keyType = TypeUtil.getTypeArgument(targetType, 0);
		final Type valueType = TypeUtil.getTypeArgument(targetType, 1);

		return convert(targetType, keyType, valueType, value);
	}

	/**
	 * 转换对象为指定键值类型的指定类型Map
	 *
	 * @param targetType 目标的Map类型
	 * @param keyType    键类型
	 * @param valueType  值类型
	 * @param value      被转换的值
	 * @return 转换后的Map
	 * @throws ConvertException 转换异常或不支持的类型
	 */
	@SuppressWarnings("rawtypes")
	public Map.Entry<?, ?> convert(final Type targetType, final Type keyType, final Type valueType, final Object value)
		throws ConvertException {
		Map map = null;
		if (value instanceof Map.Entry) {
			final Map.Entry entry = (Map.Entry) value;
			map = MapUtil.of(entry.getKey(), entry.getValue());
		} else if (value instanceof Pair) {
			final Pair entry = (Pair<?, ?>) value;
			map = MapUtil.of(entry.getLeft(), entry.getRight());
		} else if (value instanceof Map) {
			map = (Map) value;
		} else if (value instanceof CharSequence) {
			final CharSequence str = (CharSequence) value;
			map = strToMap(str);
		} else if (BeanUtil.isWritableBean(value.getClass())) {
			// 一次性只读场景，包装为Map效率更高
			map = BeanUtil.toBeanMap(value);
		}

		if (null != map) {
			return mapToEntry(targetType, keyType, valueType, map);
		}

		throw new ConvertException("Unsupported to map from [{}] of type: {}", value, value.getClass().getName());
	}

	/**
	 * 字符串转单个键值对的Map，支持分隔符{@code :}、{@code =}、{@code ,}
	 *
	 * @param str 字符串
	 * @return map or null
	 */
	private static Map<CharSequence, CharSequence> strToMap(final CharSequence str) {
		// key:value  key=value  key,value
		final int index = StrUtil.indexOf(str,
			c -> c == CharUtil.COLON || c == CharUtil.EQUAL || c == CharUtil.COMMA,
			0, str.length());

		if (index > -1) {
			return MapUtil.of(str.subSequence(0, index), str.subSequence(index + 1, str.length()));
		}
		return null;
	}

	/**
	 * Map转Entry
	 *
	 * @param targetType 目标的Map类型
	 * @param keyType    键类型
	 * @param valueType  值类型
	 * @param map        被转换的map
	 * @return Entry
	 */
	@SuppressWarnings("rawtypes")
	private Map.Entry<?, ?> mapToEntry(final Type targetType, final Type keyType, final Type valueType, final Map map) {

		final Object key;
		Object value;
		if (1 == map.size()) {
			final Map.Entry entry = (Map.Entry) map.entrySet().iterator().next();
			key = entry.getKey();
			value = entry.getValue();
		} else {
			// 忽略Map中其它属性
			key = map.get("key");
			value = map.get("value");
		}

		if(value instanceof Wrapper){
			value = ((Wrapper) value).getRaw();
		}

		return (Map.Entry<?, ?>) ConstructorUtil.newInstance(TypeUtil.getClass(targetType),
			TypeUtil.isUnknown(keyType) ? key : rootConverter.convert(keyType, key),
			TypeUtil.isUnknown(valueType) ? value : rootConverter.convert(valueType, value)
		);
	}
}
