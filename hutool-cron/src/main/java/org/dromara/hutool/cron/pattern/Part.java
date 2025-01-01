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

package org.dromara.hutool.cron.pattern;

import org.dromara.hutool.core.date.Month;
import org.dromara.hutool.core.date.Week;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.cron.CronException;

import java.util.Calendar;

/**
 * 表达式各个部分的枚举，用于限定在表达式中的位置和规则（如最小值和最大值）<br>
 * {@link #ordinal()}表示此部分在表达式中的位置，如0表示秒<br>
 * 表达式各个部分的枚举位置为：
 * <pre>
 *         0       1    2        3         4       5         6
 *     [SECOND] MINUTE HOUR DAY_OF_MONTH MONTH DAY_OF_WEEK [YEAR]
 * </pre>
 *
 * @author Looly
 * @since 5.8.0
 */
public enum Part {
	/**
	 * 秒[0-59]
	 */
	SECOND(Calendar.SECOND, 0, 59),
	/**
	 * 分[0-59]
	 */
	MINUTE(Calendar.MINUTE, 0, 59),
	/**
	 * 时[0-23]
	 */
	HOUR(Calendar.HOUR_OF_DAY, 0, 23),
	/**
	 * 日[1-31]
	 */
	DAY_OF_MONTH(Calendar.DAY_OF_MONTH, 1, 31),
	/**
	 * 月[1-12]
	 */
	MONTH(Calendar.MONTH, Month.JANUARY.getValueBaseOne(), Month.DECEMBER.getValueBaseOne()),
	/**
	 * 周中的天，如周一、周日等，[0-6]即[SUNDAY-SATURDAY]
	 */
	DAY_OF_WEEK(Calendar.DAY_OF_WEEK, Week.SUNDAY.ordinal(), Week.SATURDAY.ordinal()),
	/**
	 * 年[1970-2099]
	 */
	YEAR(Calendar.YEAR, 1970, 2099);

	// ---------------------------------------------------------------
	private static final Part[] ENUMS = Part.values();

	private final int calendarField;
	private final int min;
	private final int max;

	/**
	 * 构造
	 *
	 * @param calendarField Calendar中对应字段项
	 * @param min           限定最小值（包含）
	 * @param max           限定最大值（包含）
	 */
	Part(final int calendarField, final int min, final int max) {
		this.calendarField = calendarField;
		if (min > max) {
			this.min = max;
			this.max = min;
		} else {
			this.min = min;
			this.max = max;
		}
	}

	/**
	 * 获取Calendar中对应字段项
	 *
	 * @return Calendar中对应字段项
	 */
	public int getCalendarField() {
		return this.calendarField;
	}

	/**
	 * 获取最小值
	 *
	 * @return 最小值
	 */
	public int getMin() {
		return this.min;
	}

	/**
	 * 获取最大值
	 *
	 * @return 最大值
	 */
	public int getMax() {
		return this.max;
	}

	/**
	 * 检查单个值是否有效
	 *
	 * @param value 值
	 * @return 检查后的值
	 * @throws CronException 检查无效抛出此异常
	 */
	public int checkValue(final int value) throws CronException {
		Assert.checkBetween(value, min, max,
				() -> new CronException("{} value {} out of range: [{} , {}]", this.name(), value, min, max));
		return value;
	}

	/**
	 * 根据位置获取Part
	 *
	 * @param i 位置，从0开始
	 * @return Part
	 */
	public static Part of(final int i) {
		return ENUMS[i];
	}
}
