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

package org.dromara.hutool.core.xml;

import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.bean.copier.CopyOptions;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.collection.ListUtil;
import org.dromara.hutool.core.map.MapUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * XML转换器，用于转换Map或Bean等
 *
 * @author Looly
 * @since 6.0.0
 */
public class XmlMapper {

	/**
	 * 创建XmlMapper
	 *
	 * @param node {@link Node}XML节点
	 * @return XmlMapper
	 */
	public static XmlMapper of(final Node node) {
		return new XmlMapper(node);
	}

	private final Node node;

	/**
	 * 构造
	 *
	 * @param node {@link Node}XML节点
	 */
	public XmlMapper(final Node node) {
		this.node = node;
	}

	/**
	 * XML转Java Bean<br>
	 * 如果XML根节点只有一个，且节点名和Bean的名称一致，则直接转换子节点
	 *
	 * @param <T>  bean类型
	 * @param bean bean类
	 * @param copyOptions 拷贝选线，可选是否忽略错误等
	 * @return bean
	 */
	public <T> T toBean(final Class<T> bean, final CopyOptions copyOptions) {
		final Map<String, Object> map = toMap();
		if (null != map && map.size() == 1) {
			final String nodeName = CollUtil.getFirst(map.keySet());
			if (bean.getSimpleName().equalsIgnoreCase(nodeName)) {
				// 只有key和bean的名称匹配时才做单一对象转换
				return BeanUtil.toBean(CollUtil.get(map.values(), 0), bean);
			}
		}
		return BeanUtil.toBean(map, bean, copyOptions);
	}

	/**
	 * XML节点转Map
	 *
	 * @return map
	 */
	public Map<String, Object> toMap() {
		return toMap(new LinkedHashMap<>());
	}

	/**
	 * XML节点转Map
	 *
	 * @param result 结果Map
	 * @return map
	 */
	public Map<String, Object> toMap(final Map<String, Object> result) {
		return toMap(this.node, result);
	}

	/**
	 * XML节点转Map
	 *
	 * @param result 结果Map
	 * @return map
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> toMap(final Node node, Map<String, Object> result) {
		if (null == result) {
			result = new HashMap<>();
		}
		final NodeList nodeList = node.getChildNodes();
		final int length = nodeList.getLength();
		Node childNode;
		Element childEle;
		for (int i = 0; i < length; ++i) {
			childNode = nodeList.item(i);
			if (!XmlUtil.isElement(childNode)) {
				continue;
			}

			childEle = (Element) childNode;
			final Object newValue;
			if (childEle.hasChildNodes()) {
				// 子节点继续递归遍历
				final Map<String, Object> map = toMap(childEle, new LinkedHashMap<>());
				if (MapUtil.isNotEmpty(map)) {
					newValue = map;
				} else {
					newValue = childEle.getTextContent();
				}
			} else {
				newValue = childEle.getTextContent();
			}

			if (null != newValue) {
				final Object value = result.get(childEle.getNodeName());
				if (null != value) {
					if (value instanceof List) {
						((List<Object>) value).add(newValue);
					} else {
						result.put(childEle.getNodeName(), ListUtil.of(value, newValue));
					}
				} else {
					result.put(childEle.getNodeName(), newValue);
				}
			}
		}
		return result;
	}
}
