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

package org.dromara.hutool.core.lang.range;

/**
 * 无限小的左边界
 *
 * @param <T> 边界值类型
 * @author huangchengxing
 * @since 6.0.0
 */
class NoneLowerBound<T extends Comparable<? super T>> implements Bound<T> {
	/**
	 * 无限小的左边界单例
	 */
	@SuppressWarnings("rawtypes")
	static final NoneLowerBound INSTANCE = new NoneLowerBound();

	private NoneLowerBound() {
	}

	/**
	 * 获取边界值
	 *
	 * @return 边界值
	 */
	@Override
	public T getValue() {
		return null;
	}

	/**
	 * 获取边界类型
	 *
	 * @return 边界类型
	 */
	@Override
	public BoundType getType() {
		return BoundType.OPEN_LOWER_BOUND;
	}

	/**
	 * 检验指定值是否在当前边界表示的范围内
	 *
	 * @param t 要检验的值，不允许为{@code null}
	 * @return 是否
	 */
	@Override
	public boolean test(final T t) {
		return true;
	}

	/**
	 * <p>比较另一边界与当前边界在坐标轴上位置的先后顺序。<br>
	 * 若令当前边界为<em>t1</em>，另一边界为<em>t2</em>，则有
	 * <ul>
	 *     <li>-1：<em>t1</em>在<em>t2</em>的左侧；</li>
	 *     <li>0：<em>t1</em>与<em>t2</em>的重合；</li>
	 *     <li>-1：<em>t1</em>在<em>t2</em>的右侧；（不存在）</li>
	 * </ul>
	 *
	 * @param bound 边界
	 * @return 位置
	 */
	@SuppressWarnings("ComparatorMethodParameterNotUsed")
	@Override
	public int compareTo(final Bound<T> bound) {
		return bound instanceof NoneLowerBound ? 0 : -1;
	}

	/**
	 * 获取{@code "[value"}或{@code "(value"}格式的字符串
	 *
	 * @return 字符串
	 */
	@Override
	public String descBound() {
		return getType().getSymbol() + INFINITE_MIN;
	}

	/**
	 * 对当前边界取反
	 *
	 * @return 取反后的边界
	 */
	@Override
	public Bound<T> negate() {
		return this;
	}

	/**
	 * 将当前实例转为一个区间
	 *
	 * @return 区间
	 */
	@Override
	public BoundedRange<T> toRange() {
		return BoundedRange.all();
	}

	/**
	 * 获得当前实例对应的{@code { x | x >= xxx}}格式的不等式字符串
	 *
	 * @return 字符串
	 */
	@Override
	public String toString() {
		return "{x | x > -∞}";
	}

}
