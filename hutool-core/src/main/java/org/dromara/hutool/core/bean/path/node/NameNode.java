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

package org.dromara.hutool.core.bean.path.node;

import org.dromara.hutool.core.math.NumberUtil;

/**
 * 处理名称节点或序号节点，如：
 * <ul>
 *     <li>name</li>
 *     <li>1</li>
 * </ul>
 *
 * @author Looly
 */
public class NameNode implements Node {

	private final String name;

	/**
	 * 获取节点名
	 *
	 * @return 节点名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 构造
	 *
	 * @param name 节点名
	 */
	public NameNode(final String name) {
		this.name = name;
	}

	/**
	 * 是否为数字节点
	 *
	 * @return 是否为数字节点
	 */
	public boolean isNumber() {
		return NumberUtil.isInteger(name);
	}

	@Override
	public String toString() {
		return this.name;
	}
}
