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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期间隔
 *
 * @author Looly
 */
public class DateBetween implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 开始日期
	 */
	private final Date begin;
	/**
	 * 结束日期
	 */
	private final Date end;

	/**
	 * 创建<br>
	 * 在前的日期做为起始时间，在后的做为结束时间，间隔只保留绝对值正数
	 *
	 * @param begin 起始时间
	 * @param end   结束时间
	 * @return DateBetween
	 * @since 3.2.3
	 */
	public static DateBetween of(final Date begin, final Date end) {
		return new DateBetween(begin, end);
	}

	/**
	 * 创建<br>
	 * 在前的日期做为起始时间，在后的做为结束时间，间隔只保留绝对值正数
	 *
	 * @param begin 起始时间
	 * @param end   结束时间
	 * @param isAbs 日期间隔是否只保留绝对值正数
	 * @return DateBetween
	 * @since 3.2.3
	 */
	public static DateBetween of(final Date begin, final Date end, final boolean isAbs) {
		return new DateBetween(begin, end, isAbs);
	}

	/**
	 * 构造<br>
	 * 在前的日期做为起始时间，在后的做为结束时间，间隔只保留绝对值正数
	 *
	 * @param begin 起始时间
	 * @param end   结束时间
	 */
	public DateBetween(final Date begin, final Date end) {
		this(begin, end, true);
	}

	/**
	 * 构造<br>
	 * 在前的日期做为起始时间，在后的做为结束时间
	 *
	 * @param begin 起始时间
	 * @param end   结束时间
	 * @param isAbs 日期间隔是否只保留绝对值正数
	 * @since 3.1.1
	 */
	public DateBetween(final Date begin, final Date end, final boolean isAbs) {
		Assert.notNull(begin, "Begin date is null !");
		Assert.notNull(end, "End date is null !");

		if (isAbs && begin.after(end)) {
			// 间隔只为正数的情况下，如果开始日期晚于结束日期，置换之
			this.begin = end;
			this.end = begin;
		} else {
			this.begin = begin;
			this.end = end;
		}
	}

	/**
	 * 判断两个日期相差的时长<br>
	 * 返回 给定单位的时长差
	 *
	 * @param unit 相差的单位：相差 天{@link DateUnit#DAY}、小时{@link DateUnit#HOUR} 等
	 * @return 时长差
	 */
	public long between(final DateUnit unit) {
		final long diff = end.getTime() - begin.getTime();
		return diff / unit.getMillis();
	}

	/**
	 * 计算两个日期相差月数<br>
	 * 在非重置情况下，如果起始日期的天大于结束日期的天，月数要少算1（不足1个月）
	 *
	 * @param isReset 是否重置时间为起始时间（重置天时分秒）
	 * @return 相差月数
	 * @since 3.0.8
	 */
	public long betweenMonth(final boolean isReset) {
		final Calendar beginCal = CalendarUtil.calendar(begin);
		final Calendar endCal = CalendarUtil.calendar(end);

		final int betweenYear = endCal.get(Calendar.YEAR) - beginCal.get(Calendar.YEAR);
		final int betweenMonthOfYear = endCal.get(Calendar.MONTH) - beginCal.get(Calendar.MONTH);

		final int result = betweenYear * 12 + betweenMonthOfYear;
		if (!isReset) {
			endCal.set(Calendar.YEAR, beginCal.get(Calendar.YEAR));
			endCal.set(Calendar.MONTH, beginCal.get(Calendar.MONTH));
			final long between = endCal.getTimeInMillis() - beginCal.getTimeInMillis();
			if (between < 0) {
				return result - 1;
			}
		}
		return result;
	}

	/**
	 * 计算两个日期相差年数<br>
	 * 在非重置情况下，如果起始日期的月大于结束日期的月，年数要少算1（不足1年）
	 *
	 * @param isReset 是否重置时间为起始时间（重置月天时分秒）
	 * @return 相差年数
	 * @since 3.0.8
	 */
	public long betweenYear(final boolean isReset) {
		final Calendar beginCal = CalendarUtil.calendar(begin);
		final Calendar endCal = CalendarUtil.calendar(end);

		final int result = endCal.get(Calendar.YEAR) - beginCal.get(Calendar.YEAR);
		if(isReset){
			return result;
		}

		final int beginMonthBase0 = beginCal.get(Calendar.MONTH);
		final int endMonthBase0 = endCal.get(Calendar.MONTH);
		if (beginMonthBase0 < endMonthBase0) {
			return result;
		} else if (beginMonthBase0 > endMonthBase0) {
			return result - 1;
		} else if (Calendar.FEBRUARY == beginMonthBase0
			&& CalendarUtil.isLastDayOfMonth(beginCal)
			&& CalendarUtil.isLastDayOfMonth(endCal)) {
			// 考虑闰年的2月情况
			// 两个日期都位于2月的最后一天，此时月数按照相等对待，此时都设置为1号
			beginCal.set(Calendar.DAY_OF_MONTH, 1);
			endCal.set(Calendar.DAY_OF_MONTH, 1);
		}

		endCal.set(Calendar.YEAR, beginCal.get(Calendar.YEAR));
		final long between = endCal.getTimeInMillis() - beginCal.getTimeInMillis();
		return between < 0 ? result - 1 : result;
	}

	/**
	 * 获取开始时间
	 *
	 * @return 获取开始时间
	 */
	public Date getBegin() {
		return begin;
	}

	/**
	 * 获取结束日期
	 *
	 * @return 结束日期
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * 格式化输出时间差
	 *
	 * @param unit  日期单位
	 * @param level 级别
	 * @return 字符串
	 * @since 5.7.17
	 */
	public String toString(final DateUnit unit, final BetweenFormatter.Level level) {
		return DateUtil.formatBetween(between(unit), level);
	}

	/**
	 * 格式化输出时间差
	 *
	 * @param level 级别
	 * @return 字符串
	 */
	public String toString(final BetweenFormatter.Level level) {
		return toString(DateUnit.MS, level);
	}

	@Override
	public String toString() {
		return toString(BetweenFormatter.Level.MILLISECOND);
	}
}
