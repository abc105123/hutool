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

package org.dromara.hutool.core.lang.wrapper;

/**
 * 简单包装对象<br>
 * 通过继承此类，可以直接使用被包装的对象，用于简化和统一封装。
 *
 * @param <T> 被包装对象类型
 * @author Looly
 * @since 6.0.0
 */
public class SimpleWrapper<T> implements Wrapper<T> {

	/**
	 * 原始对象
	 */
	protected final T raw;

	/**
	 * 构造
	 *
	 * @param raw 原始对象
	 */
	public SimpleWrapper(final T raw) {
		this.raw = raw;
	}

	@Override
	public T getRaw() {
		return this.raw;
	}
}
