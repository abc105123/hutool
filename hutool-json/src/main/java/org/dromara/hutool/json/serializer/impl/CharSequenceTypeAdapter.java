/*
 * Copyright (c) 2024 Hutool Team and hutool.cn
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

import org.dromara.hutool.core.reflect.TypeUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.json.JSON;
import org.dromara.hutool.json.JSONObject;
import org.dromara.hutool.json.JSONPrimitive;
import org.dromara.hutool.json.reader.JSONTokener;
import org.dromara.hutool.json.serializer.JSONContext;
import org.dromara.hutool.json.serializer.MatcherJSONDeserializer;
import org.dromara.hutool.json.serializer.MatcherJSONSerializer;
import org.dromara.hutool.json.xml.JSONXMLParser;
import org.dromara.hutool.json.xml.ParseConfig;

import java.lang.reflect.Type;

/**
 * {@link CharSequence}类型适配器，主要用于：
 * <ul>
 *     <li>序列化(serialize)：按照给定类型，解析JSON或XML字符串为{@link JSON}</li>
 *     <li>反序列化(deserialize)：如果为原始值，返回原始值并调用toString方法，其他JSON对象直接转为JSON字符换。</li>
 * </ul>
 *
 * {@link CharSequence}适配器主要解决在JSON的get调用时，如果用户指定为字符串类型转换问题。
 *
 * @author looly
 * @since 6.0.0
 */
public class CharSequenceTypeAdapter implements MatcherJSONSerializer<CharSequence>, MatcherJSONDeserializer<CharSequence> {

	/**
	 * 单例
	 */
	public static final CharSequenceTypeAdapter INSTANCE = new CharSequenceTypeAdapter();

	@Override
	public boolean match(final Object bean, final JSONContext context) {
		return bean instanceof CharSequence;
	}

	@Override
	public boolean match(final JSON json, final Type deserializeType) {
		return CharSequence.class.isAssignableFrom(TypeUtil.getClass(deserializeType));
	}

	@Override
	public JSON serialize(final CharSequence bean, final JSONContext context) {
		// null 检查
		final String jsonStr = StrUtil.trim(bean);
		if (StrUtil.isEmpty(jsonStr)) {
			// https://www.rfc-editor.org/rfc/rfc8259#section-7
			// 未被包装的空串理解为null
			return null;
		}

		final JSON contextJson = context.getContextJson();

		// 如果指定序列化为JSONPrimitive，则直接返回原始值
		if (contextJson instanceof JSONPrimitive) {
			return context.getOrCreatePrimitive(bean);
		}

		// 按照XML解析
		if (StrUtil.startWith(jsonStr, '<')) {
			// 可能为XML
			final JSONObject jsonObject = context.getOrCreateObj();
			JSONXMLParser.of(ParseConfig.of(), null).parseJSONObject(jsonStr, jsonObject);
			return jsonObject;
		}

		// 按照JSON字符串解析
		return context.getFactory().ofParser(new JSONTokener(jsonStr, context.config().isIgnoreZeroWithChar())).parse();
	}

	@Override
	public CharSequence deserialize(final JSON json, final Type deserializeType) {
		if (json instanceof JSONPrimitive) {
			return ((JSONPrimitive) json).getValue().toString();
		}
		return json.toString();
	}
}
