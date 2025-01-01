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

import org.dromara.hutool.json.JSON;

import java.lang.reflect.Type;

/**
 * JSON自定义反序列化接口，实现{@link JSON} to Bean，主要作用于JSON转为Java对象时，使用方式为：
 * <ul>
 *     <li>定义好反序列化规则，关联指定类型与转换器实现反序列化。</li>
 *     <li>使Bean实现此接口，调用{@link #deserialize(JSON, Type)}解析字段，返回this即可。</li>
 * </ul>
 *
 * @param <V> 反序列化后的类型
 * @author Looly
 */
@FunctionalInterface
public interface JSONDeserializer<V> extends TypeAdapter{

	/**
	 * 反序列化，通过实现此方法，自定义实现JSON转换为指定类型的逻辑<br>
	 * deserializeType用于指明当结果对象存在泛型时，可以获取泛型对应的实际类型
	 *
	 * @param json {@link JSON}
	 * @param deserializeType 反序列化类型
	 * @return 目标对象
	 */
	V deserialize(JSON json, Type deserializeType);
}
