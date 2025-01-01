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

package org.dromara.hutool.json.serializer.impl;

import org.dromara.hutool.core.convert.ConvertUtil;
import org.dromara.hutool.core.map.MapUtil;
import org.dromara.hutool.core.reflect.TypeUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.util.ObjUtil;
import org.dromara.hutool.json.JSON;
import org.dromara.hutool.json.JSONArray;
import org.dromara.hutool.json.JSONObject;
import org.dromara.hutool.json.serializer.JSONContext;
import org.dromara.hutool.json.serializer.MatcherJSONDeserializer;
import org.dromara.hutool.json.serializer.MatcherJSONSerializer;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Map类型适配器，用于将JSON对象和Map对象互转。
 *
 * @author Looly
 * @since 6.0.0
 */
public class MapTypeAdapter implements MatcherJSONSerializer<Map<?, ?>>, MatcherJSONDeserializer<Map<?, ?>> {

	/**
	 * 单例
	 */
	public static final MapTypeAdapter INSTANCE = new MapTypeAdapter();

	@Override
	public boolean match(final Object bean, final JSONContext context) {
		return bean instanceof Map;
	}

	@Override
	public boolean match(final JSON json, final Type deserializeType) {
		if (json instanceof JSONObject) {
			final Class<?> rawType = TypeUtil.getClass(deserializeType);
			return Map.class.isAssignableFrom(rawType);
		}
		return false;
	}

	@Override
	public JSON serialize(final Map<?, ?> bean, final JSONContext context) {

		final JSON contextJson = context.getContextJson();
		// 序列化为JSONArray
		if (contextJson instanceof JSONArray) {
			final Iterator<?> iter;
			if (bean instanceof Iterator) {
				iter = (Iterator<?>) bean;
			} else if (bean instanceof Iterable) {
				iter = ((Iterable<?>) bean).iterator();
			} else {
				iter = bean.entrySet().iterator();
			}
			IterTypeAdapter.mapFromIterator(bean, iter, (JSONArray) contextJson);
			return contextJson;
		}

		// Map to JSONObject
		final JSONObject result = context.getOrCreateObj();
		// 注入键值对
		for (final Map.Entry<?, ?> e : bean.entrySet()) {
			result.putValue(StrUtil.toStringOrNull(e.getKey()), e.getValue());
		}
		return result;
	}

	@Override
	public Map<?, ?> deserialize(final JSON json, final Type deserializeType) {
		final Type keyType = TypeUtil.getTypeArgument(deserializeType, 0);
		final Type valueType = TypeUtil.getTypeArgument(deserializeType, 1);
		return toMap(json,
			TypeUtil.getClass(deserializeType),
			keyType,
			valueType);
	}

	/**
	 * 将JSON对象转换为Map
	 *
	 * @param json      JSON对象
	 * @param mapClass  Map类型
	 * @param keyType   键类型
	 * @param valueType 值类型
	 * @return Map
	 */
	public Map<?, ?> toMap(final JSON json, final Class<?> mapClass, final Type keyType, final Type valueType) {
		final Map<?, ?> map = MapUtil.createMap(mapClass, LinkedHashMap::new);
		for (final Map.Entry<String, JSON> entry : (JSONObject) json) {
			map.put(
				// key类型为String转目标类型，使用标准转换器
				ConvertUtil.convert(keyType, entry.getKey()),
				ObjUtil.apply(entry.getValue(), (value) -> value.toBean(valueType))
			);
		}
		return map;
	}
}
