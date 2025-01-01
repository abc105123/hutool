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

package org.dromara.hutool.core.codec;

import java.nio.ByteOrder;
import java.util.Objects;

/**
 * 128位数字表示，分为：
 * <ul>
 *     <li>最高有效位（Most Significant Bit），64 bit（8 bytes）</li>
 *     <li>最低有效位（Least Significant Bit），64 bit（8 bytes）</li>
 * </ul>
 *
 * @author hexiufeng
 * @since 5.2.5
 */
public class Number128 extends Number implements Comparable<Number128>{
	private static final long serialVersionUID = 1L;

	/**
	 * 最高有效位（Most Significant Bit），64 bit（8 bytes）
	 */
	private long mostSigBits;
	/**
	 * 最低有效位（Least Significant Bit），64 bit（8 bytes）
	 */
	private long leastSigBits;

	/**
	 * 构造
	 *
	 * @param mostSigBits 高位
	 * @param leastSigBits  低位
	 */
	public Number128(final long mostSigBits, final long leastSigBits) {
		this.mostSigBits = mostSigBits;
		this.leastSigBits = leastSigBits;
	}

	/**
	 * 获取最高有效位（Most Significant Bit），64 bit（8 bytes）
	 *
	 * @return 最高有效位（Most Significant Bit），64 bit（8 bytes）
	 */
	public long getMostSigBits() {
		return mostSigBits;
	}

	/**
	 * 设置最高有效位（Most Significant Bit），64 bit（8 bytes）
	 *
	 * @param hiValue 最高有效位（Most Significant Bit），64 bit（8 bytes）
	 */
	public void setMostSigBits(final long hiValue) {
		this.mostSigBits = hiValue;
	}

	/**
	 * 获取最低有效位（Least Significant Bit），64 bit（8 bytes）
	 *
	 * @return 最低有效位（Least Significant Bit），64 bit（8 bytes）
	 */
	public long getLeastSigBits() {
		return leastSigBits;
	}

	/**
	 * 设置最低有效位（Least Significant Bit），64 bit（8 bytes）
	 *
	 * @param leastSigBits 最低有效位（Least Significant Bit），64 bit（8 bytes）
	 */
	public void setLeastSigBits(final long leastSigBits) {
		this.leastSigBits = leastSigBits;
	}

	/**
	 * 获取高低位数组，规则为：
	 * <ul>
	 *     <li>{@link ByteOrder#LITTLE_ENDIAN}，则long[0]：低位，long[1]：高位</li>
	 *     <li>{@link ByteOrder#BIG_ENDIAN}，则long[0]：高位，long[1]：低位</li>
	 * </ul>
	 *
	 *
	 * @param byteOrder 端续
	 * @return 高低位数组，long[0]：低位，long[1]：高位
	 */
	public long[] getLongArray(final ByteOrder byteOrder) {
		if(byteOrder == ByteOrder.BIG_ENDIAN){
			return new long[]{leastSigBits, mostSigBits};
		} else{
			return new long[]{mostSigBits, leastSigBits};
		}
	}

	@Override
	public int intValue() {
		return (int) longValue();
	}

	@Override
	public long longValue() {
		return this.leastSigBits;
	}

	@Override
	public float floatValue() {
		return longValue();
	}

	@Override
	public double doubleValue() {
		return longValue();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof Number128) {
			final Number128 number128 = (Number128) o;
			return leastSigBits == number128.leastSigBits && mostSigBits == number128.mostSigBits;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(leastSigBits, mostSigBits);
	}

	@Override
	public int compareTo(final Number128 o) {
		final int mostSigBits = Long.compare(this.mostSigBits, o.mostSigBits);
		return mostSigBits != 0 ? mostSigBits : Long.compare(this.leastSigBits, o.leastSigBits);
	}


}
