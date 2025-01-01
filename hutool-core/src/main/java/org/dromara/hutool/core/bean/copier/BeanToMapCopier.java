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

package org.dromara.hutool.core.bean.copier;

import org.dromara.hutool.core.bean.PropDesc;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.lang.mutable.MutableEntry;
import org.dromara.hutool.core.reflect.TypeUtil;
import org.dromara.hutool.core.text.StrUtil;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Bean属性拷贝到Map中的拷贝器
 *
 * @since 5.8.0
 */
@SuppressWarnings("rawtypes")
public class BeanToMapCopier extends AbsCopier<Object, Map> {

	/**
	 * 目标的Map类型（用于泛型类注入）
	 */
	private final Type targetType;

	/**
	 * 构造
	 *
	 * @param source      来源Map
	 * @param target      目标Map对象
	 * @param targetType  目标泛型类型
	 * @param copyOptions 拷贝选项
	 */
	public BeanToMapCopier(final Object source, final Map target, final Type targetType, final CopyOptions copyOptions) {
		super(source, target, copyOptions);
		this.targetType = targetType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map copy() {
		final CopyOptions copyOptions = this.copyOptions;
		Class<?> actualEditable = source.getClass();
		if (null != copyOptions.editable) {
			// 检查限制类是否为target的父类或接口
			Assert.isTrue(copyOptions.editable.isInstance(source),
					"Source class [{}] not assignable to Editable class [{}]", actualEditable.getName(), copyOptions.editable.getName());
			actualEditable = copyOptions.editable;
		}

		final Map<String, PropDesc> sourcePropDescMap = getBeanDesc(actualEditable).getPropMap(copyOptions.ignoreCase);
		sourcePropDescMap.forEach((sFieldName, sDesc) -> {
			if (null == sFieldName || !sDesc.isReadable(copyOptions.transientSupport)) {
				// 字段空或不可读，跳过
				return;
			}

			// 检查源对象属性是否过滤属性
			Object sValue = sDesc.getValue(this.source, copyOptions.ignoreError);
			if (!copyOptions.testPropertyFilter(sDesc.getField(), sValue)) {
				return;
			}

			// 编辑键值对
			final MutableEntry<Object, Object> entry = copyOptions.editField(sFieldName, sValue);
			if(null == entry){
				return;
			}
			sFieldName = StrUtil.toStringOrNull(entry.getKey());
			// 对key做转换，转换后为null的跳过
			if (null == sFieldName) {
				return;
			}
			sValue = entry.getValue();

			// 获取目标值真实类型并转换源值
			final Type[] typeArguments = TypeUtil.getTypeArguments(this.targetType);
			if(null != typeArguments && typeArguments.length > 1){
				//sValue = Convert.convertWithCheck(typeArguments[1], sValue, null, this.copyOptions.ignoreError);
				sValue = copyOptions.convertField(typeArguments[1], sValue);
			}

			// 目标赋值
			if(null != sValue || !copyOptions.ignoreNullValue){
				target.put(sFieldName, sValue);
			}
		});
		return this.target;
	}
}
