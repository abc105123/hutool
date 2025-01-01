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

package org.dromara.hutool.extra.aop;

import java.lang.reflect.Method;

/**
 * 切面接口
 *
 * @author Looly
 * @author ted.L
 * @since 4.18
 */
public interface Aspect {

	/**
	 * 目标方法执行前的操作
	 *
	 * @param target 目标对象
	 * @param method 目标方法
	 * @param args   参数
	 * @return 是否继续执行接下来的操作
	 */
	boolean before(Object target, Method method, Object[] args);

	/**
	 * 目标方法执行后的操作<br>
	 * 如果 target.method 抛出异常且 {@link Aspect#afterException} 返回true,则不会执行此操作<br>
	 * 如果 {@link Aspect#afterException} 返回false,则无论target.method是否抛出异常，均会执行此操作<br>
	 *
	 * @param target    目标对象
	 * @param method    目标方法
	 * @param args      参数
	 * @param returnVal 目标方法执行返回值
	 * @return 是否允许返回值（接下来的操作）
	 */
	boolean after(Object target, Method method, Object[] args, Object returnVal);

	/**
	 * 目标方法抛出异常时的操作
	 *
	 * @param target 目标对象
	 * @param method 目标方法
	 * @param args   参数
	 * @param e      异常
	 * @return 是否允许抛出异常
	 */
	boolean afterException(Object target, Method method, Object[] args, Throwable e);
}
