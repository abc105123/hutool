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

package org.dromara.hutool.core.date;

import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;

import java.time.DayOfWeek;
import java.util.Calendar;

/**
 * 星期枚举<br>
 * 与Calendar中的星期int值对应
 *
 * @author Looly
 * @see #SUNDAY
 * @see #MONDAY
 * @see #TUESDAY
 * @see #WEDNESDAY
 * @see #THURSDAY
 * @see #FRIDAY
 * @see #SATURDAY
 */
public enum Week {

	/**
	 * 周日
	 */
	SUNDAY(Calendar.SUNDAY),
	/**
	 * 周一
	 */
	MONDAY(Calendar.MONDAY),
	/**
	 * 周二
	 */
	TUESDAY(Calendar.TUESDAY),
	/**
	 * 周三
	 */
	WEDNESDAY(Calendar.WEDNESDAY),
	/**
	 * 周四
	 */
	THURSDAY(Calendar.THURSDAY),
	/**
	 * 周五
	 */
	FRIDAY(Calendar.FRIDAY),
	/**
	 * 周六
	 */
	SATURDAY(Calendar.SATURDAY);

	// ---------------------------------------------------------------
	private static final Week[] ENUMS = Week.values();

	/**
	 * 星期对应{@link Calendar} 中的Week值
	 */
	private final int value;

	/**
	 * 构造
	 *
	 * @param value 星期对应{@link Calendar} 中的Week值
	 */
	Week(final int value) {
		this.value = value;
	}

	/**
	 * 获得星期对应{@link Calendar} 中的Week值
	 *
	 * @return 星期对应 {@link Calendar} 中的Week值
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * 获取ISO8601规范的int值，from 1 (Monday) to 7 (Sunday).
	 *
	 * @return ISO8601规范的int值
	 * @since 5.8.0
	 */
	public int getIso8601Value() {
		int iso8601IntValue = getValue() - 1;
		if (0 == iso8601IntValue) {
			iso8601IntValue = 7;
		}
		return iso8601IntValue;
	}

	/**
	 * 转换为中文名
	 *
	 * @return 星期的中文名
	 * @since 3.3.0
	 */
	public String toChinese() {
		return toChinese("星期");
	}

	/**
	 * 转换为中文名
	 *
	 * @param weekNamePre 表示星期的前缀，例如前缀为“星期”，则返回结果为“星期一”；前缀为”周“，结果为“周一”
	 * @return 星期的中文名
	 * @since 4.0.11
	 */
	public String toChinese(final String weekNamePre) {
		switch (this) {
			case SUNDAY:
				return weekNamePre + "日";
			case MONDAY:
				return weekNamePre + "一";
			case TUESDAY:
				return weekNamePre + "二";
			case WEDNESDAY:
				return weekNamePre + "三";
			case THURSDAY:
				return weekNamePre + "四";
			case FRIDAY:
				return weekNamePre + "五";
			case SATURDAY:
				return weekNamePre + "六";
			default:
				return null;
		}
	}

	/**
	 * 转换为{@link DayOfWeek}
	 *
	 * @return {@link DayOfWeek}
	 * @since 5.8.0
	 */
	public DayOfWeek toJdkDayOfWeek() {
		return DayOfWeek.of(getIso8601Value());
	}

	/**
	 * 将 {@link Calendar}星期相关值转换为Week枚举对象<br>
	 *
	 * @param calendarWeekIntValue Calendar中关于Week的int值，1表示Sunday
	 * @return Week
	 * @see #SUNDAY
	 * @see #MONDAY
	 * @see #TUESDAY
	 * @see #WEDNESDAY
	 * @see #THURSDAY
	 * @see #FRIDAY
	 * @see #SATURDAY
	 */
	public static Week of(final int calendarWeekIntValue) {
		if (calendarWeekIntValue > ENUMS.length || calendarWeekIntValue < 1) {
			return null;
		}
		return ENUMS[calendarWeekIntValue - 1];
	}

	/**
	 * 解析别名为Week对象，别名如：sun或者SUNDAY，不区分大小写<br>
	 * 参考：https://github.com/sisyphsu/dateparser/blob/master/src/main/java/com/github/sisyphsu/dateparser/DateParser.java#L319
	 *
	 * @param name 别名值
	 * @return 周枚举Week，非空
	 * @throws IllegalArgumentException 如果别名无对应的枚举，抛出此异常
	 * @since 5.8.0
	 */
	public static Week of(final String name) throws IllegalArgumentException {
		if (null != name && name.length() > 1) {
			// issue#3637
			if (StrUtil.startWithAny(name, "星期", "周")) {
				final char chineseNumber = name.charAt(name.length() - 1);
				switch (chineseNumber) {
					case '一':
						return MONDAY;
					case '二':
						return TUESDAY;
					case '三':
						return WEDNESDAY;
					case '四':
						return THURSDAY;
					case '五':
						return FRIDAY;
					case '六':
						return SATURDAY;
					case '日':
						return SUNDAY;
				}
				throw new IllegalArgumentException("Invalid week name: " + name);
			}

			switch (Character.toLowerCase(name.charAt(0))) {
				case 'm':
					return MONDAY; // monday
				case 'w':
					return WEDNESDAY; // wednesday
				case 'f':
					return FRIDAY; // friday
				case 't':
					switch (Character.toLowerCase(name.charAt(1))) {
						case 'u':
							return TUESDAY; // tuesday
						case 'h':
							return THURSDAY; // thursday
					}
					break;
				case 's':
					switch (Character.toLowerCase(name.charAt(1))) {
						case 'a':
							return SATURDAY; // saturday
						case 'u':
							return SUNDAY; // sunday
					}
					break;
			}
		}

		throw new IllegalArgumentException("Invalid Week name: " + name);
	}

	/**
	 * 将 {@link DayOfWeek}星期相关值转换为Week枚举对象<br>
	 *
	 * @param dayOfWeek DayOfWeek星期值
	 * @return Week
	 * @see #SUNDAY
	 * @see #MONDAY
	 * @see #TUESDAY
	 * @see #WEDNESDAY
	 * @see #THURSDAY
	 * @see #FRIDAY
	 * @see #SATURDAY
	 * @since 5.7.14
	 */
	public static Week of(final DayOfWeek dayOfWeek) {
		Assert.notNull(dayOfWeek);
		int week = dayOfWeek.getValue() + 1;
		if (8 == week) {
			// 周日
			week = 1;
		}
		return of(week);
	}
}
