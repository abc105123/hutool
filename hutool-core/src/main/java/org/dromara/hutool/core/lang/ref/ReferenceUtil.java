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

package org.dromara.hutool.core.lang.ref;

import org.dromara.hutool.core.util.ObjUtil;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * 引用工具类，主要针对{@link Reference} 工具化封装<br>
 * 主要封装包括：
 * <pre>
 * 1. {@link SoftReference} 软引用，在GC报告内存不足时会被GC回收
 * 2. {@link WeakReference} 弱引用，在GC时发现弱引用会回收其对象
 * 3. {@link PhantomReference} 虚引用，在GC时发现虚引用对象，会将{@link PhantomReference}插入{@link ReferenceQueue}。 此时对象未被真正回收，要等到{@link ReferenceQueue}被真正处理后才会被回收。
 * </pre>
 *
 * @author Looly
 * @since 3.1.2
 */
public class ReferenceUtil {

	/**
	 * 获得引用
	 *
	 * @param <T>      被引用对象类型
	 * @param type     引用类型枚举
	 * @param referent 被引用对象
	 * @return {@link Reference}
	 */
	public static <T> Reference<T> of(final ReferenceType type, final T referent) {
		return of(type, referent, null);
	}

	/**
	 * 获得引用
	 *
	 * @param <T>      被引用对象类型
	 * @param type     引用类型枚举
	 * @param referent 被引用对象
	 * @param queue    引用队列
	 * @return {@link Reference}
	 */
	public static <T> Reference<T> of(final ReferenceType type, final T referent, final ReferenceQueue<T> queue) {
		switch (type) {
			case SOFT:
				return new SoftReference<>(referent, queue);
			case WEAK:
				return new WeakReference<>(referent, queue);
			case PHANTOM:
				return new PhantomReference<>(referent, queue);
			default:
				return null;
		}
	}

	/**
	 * {@code null}全的解包获取原始对象
	 *
	 * @param <T> 对象类型
	 * @param obj Reference对象
	 * @return 原始对象 or {@code null}
	 * @since 6.0.0
	 */
	public static <T> T get(final Reference<T> obj) {
		return ObjUtil.apply(obj, Reference::get);
	}

	/**
	 * {@code null}安全的解包获取原始对象
	 *
	 * @param <T> 对象类型
	 * @param obj Ref对象
	 * @return 原始对象 or {@code null}
	 * @since 6.0.0
	 */
	public static <T> T get(final Ref<T> obj) {
		return ObjUtil.apply(obj, Ref::get);
	}
}
