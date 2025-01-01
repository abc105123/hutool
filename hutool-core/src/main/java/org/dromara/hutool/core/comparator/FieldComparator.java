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

package org.dromara.hutool.core.comparator;

import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.dromara.hutool.core.text.StrUtil;

import java.lang.reflect.Field;

/**
 * Bean字段排序器<br>
 * 参阅feilong-core中的PropertyComparator
 *
 * @param <T> 被比较的Bean
 * @author Looly
 */
public class FieldComparator<T> extends FuncComparator<T> {
	private static final long serialVersionUID = 9157326766723846313L;

	/**
	 * 构造
	 *
	 * @param beanClass Bean类
	 * @param fieldName 字段名
	 */
	public FieldComparator(final Class<T> beanClass, final String fieldName) {
		this(getNonNullField(beanClass, fieldName));
	}

	/**
	 * 构造
	 *
	 * @param field 字段
	 */
	public FieldComparator(final Field field) {
		this(true, true, field);
	}


	/**
	 * 构造
	 *
	 * @param nullGreater 是否{@code null}在后
	 * @param compareSelf 在字段值相同情况下，是否比较对象本身。
	 *                    如果此项为{@code false}，字段值比较后为0会导致对象被认为相同，可能导致被去重。
	 * @param field       字段
	 */
	public FieldComparator(final boolean nullGreater, final boolean compareSelf, final Field field) {
		super(nullGreater, compareSelf, (bean) ->
			(Comparable<?>) FieldUtil.getFieldValue(bean,
				Assert.notNull(field, "Field must be not null!")));
	}

	/**
	 * 获取字段，附带检查字段不存在的问题。
	 *
	 * @param beanClass Bean类
	 * @param fieldName 字段名
	 * @return 非null字段
	 */
	private static Field getNonNullField(final Class<?> beanClass, final String fieldName) {
		final Field field = FieldUtil.getField(beanClass, fieldName);
		if (field == null) {
			throw new IllegalArgumentException(StrUtil.format("Field [{}] not found in Class [{}]", fieldName, beanClass.getName()));
		}
		return field;
	}
}
