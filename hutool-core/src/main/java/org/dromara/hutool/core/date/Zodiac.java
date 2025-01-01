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

import java.util.Calendar;
import java.util.Date;

/**
 * 星座 来自：<a href="https://blog.csdn.net/u010758605/article/details/48317881">https://blog.csdn.net/u010758605/article/details/48317881</a>
 *
 * @author Looly
 * @since 4.4.3
 */
public class Zodiac {

	/** 星座分隔时间日 */
	private static final int[] DAY_ARR = new int[] { 20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22 };
	/** 星座 */
	private static final String[] ZODIACS = new String[] { "摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };
	private static final String[] CHINESE_ZODIACS = new String[] { "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪" };

	/**
	 * 通过生日计算星座
	 *
	 * @param date 出生日期
	 * @return 星座名
	 */
	public static String getZodiac(final Date date) {
		return getZodiac(CalendarUtil.calendar(date));
	}

	/**
	 * 通过生日计算星座
	 *
	 * @param calendar 出生日期
	 * @return 星座名
	 */
	public static String getZodiac(final Calendar calendar) {
		if (null == calendar) {
			return null;
		}
		return getZodiac(calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * 通过生日计算星座
	 *
	 * @param month 月，从0开始计数
	 * @param day 天
	 * @return 星座名
	 * @since 4.5.0
	 */
	public static String getZodiac(final Month month, final int day) {
		return getZodiac(month.getValue(), day);
	}

	/**
	 * 通过生日计算星座
	 *
	 * @param month 月，从0开始计数，见{@link Month#getValue()}
	 * @param day 天
	 * @return 星座名
	 */
	public static String getZodiac(final int month, final int day) {
		Assert.checkBetween(month,
			Month.JANUARY.getValue(),
			Month.DECEMBER.getValue(),
			"Unsupported month value, must be [0,12]");
		// 在分隔日前为前一个星座，否则为后一个星座
		return day < DAY_ARR[month] ? ZODIACS[month] : ZODIACS[month + 1];
	}

	// ----------------------------------------------------------------------------------------------------------- 生肖
	/**
	 * 通过生日计算生肖，只计算1900年后出生的人
	 *
	 * @param date 出生日期（年需农历）
	 * @return 星座名
	 */
	public static String getChineseZodiac(final Date date) {
		return getChineseZodiac(CalendarUtil.calendar(date));
	}

	/**
	 * 通过生日计算生肖，只计算1900年后出生的人
	 *
	 * @param calendar 出生日期（年需农历）
	 * @return 星座名
	 */
	public static String getChineseZodiac(final Calendar calendar) {
		if (null == calendar) {
			return null;
		}
		return getChineseZodiac(calendar.get(Calendar.YEAR));
	}

	/**
	 * 计算生肖，只计算1900年后出生的人
	 *
	 * @param year 农历年
	 * @return 生肖名
	 */
	public static String getChineseZodiac(final int year) {
		if (year < 1900) {
			return null;
		}
		return CHINESE_ZODIACS[(year - 1900) % CHINESE_ZODIACS.length];
	}
}
