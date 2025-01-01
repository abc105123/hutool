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

import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.array.ArrayUtil;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * {@link ParameterizedType} 接口实现，用于重新定义泛型类型
 *
 * @author Looly
 * @since 4.5.7
 */
public class ParameterizedTypeImpl implements ParameterizedType, Serializable {
	private static final long serialVersionUID = 1L;

	private final Type[] actualTypeArguments;
	private final Type ownerType;
	private final Type rawType;

	/**
	 * 构造
	 *
	 * @param actualTypeArguments 实际的泛型参数类型
	 * @param ownerType 拥有者类型
	 * @param rawType 原始类型
	 */
	public ParameterizedTypeImpl(final Type[] actualTypeArguments, final Type ownerType, final Type rawType) {
		this.actualTypeArguments = actualTypeArguments;
		this.ownerType = ownerType;
		this.rawType = rawType;
	}

	@Override
	public Type[] getActualTypeArguments() {
		return actualTypeArguments;
	}

	@Override
	public Type getOwnerType() {
		return ownerType;
	}

	@Override
	public Type getRawType() {
		return rawType;
	}

	@Override
	public String toString() {
		final StringBuilder buf = new StringBuilder();

		final Type useOwner = this.ownerType;
		final Class<?> raw = (Class<?>) this.rawType;
		if (useOwner == null) {
			buf.append(raw.getName());
		} else {
			if (useOwner instanceof Class<?>) {
				buf.append(((Class<?>) useOwner).getName());
			} else {
				buf.append(useOwner);
			}
			buf.append('.').append(raw.getSimpleName());
		}

		appendAllTo(buf.append('<'), ", ", this.actualTypeArguments).append('>');
		return buf.toString();
	}

	/**
	 * 追加 {@code types} 到 {@code buf}，使用 {@code sep} 分隔
	 *
	 * @param buf 目标
	 * @param sep 分隔符
	 * @param types 加入的类型
	 * @return {@code buf}
	 */
	@SuppressWarnings("SameParameterValue")
	private static StringBuilder appendAllTo(final StringBuilder buf, final String sep, final Type... types) {
		if (ArrayUtil.isNotEmpty(types)) {
			boolean isFirst = true;
			for (final Type type : types) {
				if (isFirst) {
					isFirst = false;
				} else {
					buf.append(sep);
				}

				final String typeStr;
				if(type instanceof Class) {
					typeStr = ((Class<?>)type).getName();
				}else {
					typeStr = StrUtil.toString(type);
				}

				buf.append(typeStr);
			}
		}
		return buf;
	}
}
