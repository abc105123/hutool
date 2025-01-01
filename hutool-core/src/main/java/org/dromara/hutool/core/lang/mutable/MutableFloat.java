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

package org.dromara.hutool.core.lang.mutable;

import org.dromara.hutool.core.comparator.CompareUtil;

/**
 * 可变 {@code float} 类型
 *
 * @see Float
 * @since 3.0.1
 */
public class MutableFloat extends Number implements Comparable<MutableFloat>, Mutable<Number> {
	private static final long serialVersionUID = 1L;

	private float value;

	/**
	 * 构造，默认值0
	 */
	public MutableFloat() {
	}

	/**
	 * 构造
	 * @param value 值
	 */
	public MutableFloat(final float value) {
		this.value = value;
	}

	/**
	 * 构造
	 * @param value 值
	 */
	public MutableFloat(final Number value) {
		this(value.floatValue());
	}

	/**
	 * 构造
	 * @param value String值
	 * @throws NumberFormatException 数字转换错误
	 */
	public MutableFloat(final String value) throws NumberFormatException {
		this.value = Float.parseFloat(value);
	}

	@Override
	public Float get() {
		return this.value;
	}

	/**
	 * 设置值
	 * @param value 值
	 */
	public void set(final float value) {
		this.value = value;
	}

	@Override
	public void set(final Number value) {
		this.value = value.floatValue();
	}

	// -----------------------------------------------------------------------
	/**
	 * 值+1
	 * @return this
	 */
	public MutableFloat increment() {
		value++;
		return this;
	}

	/**
	 * 值减一
	 * @return this
	 */
	public MutableFloat decrement() {
		value--;
		return this;
	}

	// -----------------------------------------------------------------------
	/**
	 * 增加值
	 * @param operand 被增加的值
	 * @return this
	 */
	public MutableFloat add(final float operand) {
		this.value += operand;
		return this;
	}

	/**
	 * 增加值
	 * @param operand 被增加的值，非空
	 * @return this
	 * @throws NullPointerException if the object is null
	 */
	public MutableFloat add(final Number operand) {
		this.value += operand.floatValue();
		return this;
	}

	/**
	 * 减去值
	 *
	 * @param operand 被减的值
	 * @return this
	 */
	public MutableFloat subtract(final float operand) {
		this.value -= operand;
		return this;
	}

	/**
	 * 减去值
	 *
	 * @param operand 被减的值，非空
	 * @return this
	 * @throws NullPointerException if the object is null
	 */
	public MutableFloat subtract(final Number operand) {
		this.value -= operand.floatValue();
		return this;
	}

	// -----------------------------------------------------------------------
	@Override
	public int intValue() {
		return (int) value;
	}

	@Override
	public long longValue() {
		return (long) value;
	}

	@Override
	public float floatValue() {
		return value;
	}

	@Override
	public double doubleValue() {
		return value;
	}

	// -----------------------------------------------------------------------
	/**
	 * 相等需同时满足如下条件：
	 * <ol>
	 * 	<li>非空</li>
	 * 	<li>类型为 {@code MutableFloat}</li>
	 * 	<li>值相等</li>
	 * </ol>
	 *
	 * @param obj 比对的对象
	 * @return 相同返回<code>true</code>，否则 {@code false}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof MutableFloat) {
			return (Float.floatToIntBits(((MutableFloat)obj).value) == Float.floatToIntBits(value));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Float.floatToIntBits(value);
	}

	// -----------------------------------------------------------------------
	/**
	 * 比较
	 *
	 * @param other 其它 {@code MutableFloat} 对象
	 * @return x==y返回0，x&lt;y返回-1，x&gt;y返回1
	 */
	@Override
	public int compareTo(final MutableFloat other) {
		return CompareUtil.compare(this.value, other.value);
	}

	// -----------------------------------------------------------------------
	@Override
	public String toString() {
		return String.valueOf(value);
	}

}
