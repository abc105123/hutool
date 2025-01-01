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

package org.dromara.hutool.json.serializer;

import org.dromara.hutool.json.*;

/**
 * JSON序列化上下文，用于获取当前JSON对象，以便在序列化过程中获取配置信息
 *
 * @author Looly
 * @since 6.0.0
 */
public interface JSONContext {

	/**
	 * 获取当前JSON对象<br>
	 * 此对象为在Mapper时预定义的对象，用于指定序列化的JSON类型<br>
	 * 未指定返回{@code null}
	 *
	 * @return JSON对象
	 */
	JSON getContextJson();

	/**
	 * 获取JSON工厂
	 *
	 * @return JSON工厂
	 */
	JSONFactory getFactory();

	/**
	 * 获取JSON配置
	 *
	 * @return JSON配置
	 */
	default JSONConfig config() {
		return getFactory().getConfig();
	}

	/**
	 * 获取当前JSON对象，如果为非JSONObject，则创建一个JSONObject对象
	 *
	 * @return JSON对象
	 */
	default JSONObject getOrCreateObj() {
		final JSON contextJson = getContextJson();
		if (contextJson instanceof JSONObject) {
			return (JSONObject) contextJson;
		}

		return getFactory().ofObj();
	}

	/**
	 * 获取当前JSON对象，如果为非JSONArray，则创建一个JSONArray对象
	 *
	 * @return JSON对象
	 */
	default JSONArray getOrCreateArray() {
		final JSON contextJson = getContextJson();
		if (contextJson instanceof JSONArray) {
			return (JSONArray) contextJson;
		}
		return getFactory().ofArray();
	}

	/**
	 * 获取当前JSON对象，如果为非JSONPrimitive，则创建一个JSONPrimitive对象
	 *
	 * @param value 值
	 * @return JSON对象
	 */
	default JSONPrimitive getOrCreatePrimitive(final Object value) {
		final JSON contextJson = getContextJson();
		if (contextJson instanceof JSONPrimitive) {
			return ((JSONPrimitive) contextJson).setValue(value);
		}
		return getFactory().ofPrimitive(value);
	}
}
