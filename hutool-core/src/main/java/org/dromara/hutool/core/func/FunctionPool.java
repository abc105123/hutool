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

package org.dromara.hutool.core.func;

import org.dromara.hutool.core.reflect.ConstructorUtil;
import org.dromara.hutool.core.util.JdkUtil;

import java.lang.reflect.Constructor;
import java.util.function.BiFunction;

/**
 * 常用Lambda函数封装<br>
 * 提供常用对象方法的Lambda包装，减少Lambda初始化时间。
 *
 * @author Looly
 */
@SuppressWarnings("unchecked")
public class FunctionPool {

	/**
	 * 通过{@code String(char[] value, boolean share)}这个内部构造生成一个Lambda函数<br>
	 * 此函数通过传入char[]，实现zero-copy的String创建，效率很高。但是要求传入的char[]不可以在其他地方修改。<br>
	 * 此函数只支持JKDK8
	 */
	public static final BiFunction<char[], Boolean, String> STRING_CREATOR_JDK8;

	static {
		final Constructor<String> constructor = ConstructorUtil.getConstructor(String.class, char[].class, boolean.class);
		STRING_CREATOR_JDK8 = JdkUtil.IS_JDK8 ? LambdaFactory.build(BiFunction.class, constructor) : null;
	}

	/**
	 * 通过{@code String(char[] value, boolean share)}这个内部构造创建String对象。<br>
	 * 此函数通过传入char[]，实现zero-copy的String创建，效率很高。但是要求传入的char[]不可以在其他地方修改。
	 *
	 * @param value char[]值，注意这个数组不可修改！！
	 * @return String
	 */
	public static String createString(final char[] value) {
		if(JdkUtil.IS_JDK8){
			return STRING_CREATOR_JDK8.apply(value, true);
		} else {
			// TODO JDK9+优化
			return new String(value);
		}
	}
}
