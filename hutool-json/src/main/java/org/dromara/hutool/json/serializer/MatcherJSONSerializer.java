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

/**
 * 匹配JSON序列化器，用于判断是否匹配，匹配则执行序列化
 *
 * @param <V> JSON对象类型
 * @author Looly
 * @since 6.0.0
 */
public interface MatcherJSONSerializer<V> extends JSONSerializer<V> {

	/**
	 * 判断是否匹配<br>
	 * 根据Java对象内容、类型等信息，配合当前JSON所处位置判断是否匹配，用于决定是否执行序列化
	 *
	 * @param bean      对象
	 * @param context   JSON上下文
	 * @return 是否匹配
	 */
	boolean match(Object bean, JSONContext context);
}
