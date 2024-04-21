/*
 * Copyright (c) 2023 looly(loolly@aliyun.com)
 * Hutool is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          https://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.dromara.hutool.cron.pattern.matcher;

import org.dromara.hutool.core.date.DateUtil;
import org.dromara.hutool.cron.pattern.Part;

import java.time.Year;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * 单一表达式的匹配器，匹配器由7个{@link PartMatcher}组成，分别是：
 * <pre>
 *         0      1     2        3         4       5        6
 *      SECOND MINUTE HOUR DAY_OF_MONTH MONTH DAY_OF_WEEK YEAR
 * </pre>
 *
 * @author looly
 * @since 5.8.0
 */
public class PatternMatcher {

	private final PartMatcher[] matchers;

	/**
	 * 构造
	 *
	 * @param secondMatcher     秒匹配器
	 * @param minuteMatcher     分匹配器
	 * @param hourMatcher       时匹配器
	 * @param dayOfMonthMatcher 日匹配器
	 * @param monthMatcher      月匹配器
	 * @param dayOfWeekMatcher  周匹配器
	 * @param yearMatcher       年匹配器
	 */
	public PatternMatcher(final PartMatcher secondMatcher,
						  final PartMatcher minuteMatcher,
						  final PartMatcher hourMatcher,
						  final PartMatcher dayOfMonthMatcher,
						  final PartMatcher monthMatcher,
						  final PartMatcher dayOfWeekMatcher,
						  final PartMatcher yearMatcher) {

		matchers = new PartMatcher[]{
			secondMatcher,
			minuteMatcher,
			hourMatcher,
			dayOfMonthMatcher,
			monthMatcher,
			dayOfWeekMatcher,
			yearMatcher
		};
	}

	/**
	 * 根据表达式位置，获取对应的{@link PartMatcher}
	 *
	 * @param part 表达式位置
	 * @return {@link PartMatcher}
	 */
	public PartMatcher get(final Part part) {
		return matchers[part.ordinal()];
	}

	//region match

	/**
	 * 给定时间是否匹配定时任务表达式
	 *
	 * @param fields 时间字段值，{second, minute, hour, dayOfMonth, month, dayOfWeek, year}
	 * @return 如果匹配返回 {@code true}, 否则返回 {@code false}
	 */
	public boolean match(final int[] fields) {
		return match(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6]);
	}

	/**
	 * 给定周的值是否匹配定时任务表达式对应部分
	 *
	 * @param dayOfWeekValue dayOfMonth值，星期从0开始，0和7都表示周日
	 * @return 如果匹配返回 {@code true}, 否则返回 {@code false}
	 * @since 5.8.0
	 */
	public boolean matchWeek(final int dayOfWeekValue) {
		return matchers[5].test(dayOfWeekValue);
	}

	/**
	 * 给定时间是否匹配定时任务表达式
	 *
	 * @param second     秒数，-1表示不匹配此项
	 * @param minute     分钟
	 * @param hour       小时
	 * @param dayOfMonth 天
	 * @param month      月，从1开始
	 * @param dayOfWeek  周，从0开始，0和7都表示周日
	 * @param year       年
	 * @return 如果匹配返回 {@code true}, 否则返回 {@code false}
	 */
	private boolean match(final int second, final int minute, final int hour, final int dayOfMonth, final int month, final int dayOfWeek, final int year) {
		return ((second < 0) || matchers[0].test(second)) // 匹配秒（非秒匹配模式下始终返回true）
			&& matchers[1].test(minute)// 匹配分
			&& matchers[2].test(hour)// 匹配时
			&& matchDayOfMonth(matchers[3], dayOfMonth, month, Year.isLeap(year))// 匹配日
			&& matchers[4].test(month) // 匹配月
			&& matchers[5].test(dayOfWeek)// 匹配周
			&& matchers[6].test(year);// 匹配年
	}

	/**
	 * 是否匹配日（指定月份的第几天）
	 *
	 * @param matcher    {@link PartMatcher}
	 * @param dayOfMonth 日
	 * @param month      月
	 * @param isLeapYear 是否闰年
	 * @return 是否匹配
	 */
	private static boolean matchDayOfMonth(final PartMatcher matcher, final int dayOfMonth, final int month, final boolean isLeapYear) {
		return ((matcher instanceof DayOfMonthMatcher) //
			? ((DayOfMonthMatcher) matcher).match(dayOfMonth, month, isLeapYear) //
			: matcher.test(dayOfMonth));
	}
	//endregion

	//region nextMatchAfter

	/**
	 * 获取下一个匹配日期时间<br>
	 * 获取方法是，先从年开始查找对应部分的下一个值：
	 * <ul>
	 *     <li>如果此部分下个值不变，获取下一个部分</li>
	 *     <li>如果此部分下个值大于给定值，以下所有值置为最小值</li>
	 *     <li>如果此部分下个值小于给定值，回退到上一个值获取下一个新值，之后的值置为最小值</li>
	 * </ul>
	 *
	 * <pre>
	 *        秒 分 时 日 月(1) 周(0) 年
	 *     下 &lt;-----------------&gt; 上
	 * </pre>
	 *
	 * @param values 时间字段值，{second, minute, hour, dayOfMonth, monthBase1, dayOfWeekBase0, year}
	 * @param zone   时区
	 * @return {@link Calendar}，毫秒数为0
	 */
	public Calendar nextMatchAfter(final int[] values, final TimeZone zone) {
		final Calendar calendar = Calendar.getInstance(zone);
		calendar.set(Calendar.MILLISECOND, 0);

		final int[] newValues = nextMatchValuesAfter(values);
		for (int i = 0; i < newValues.length; i++) {
			// 周无需设置
			if (i != Part.DAY_OF_WEEK.ordinal()) {
				setValue(calendar, Part.of(i), newValues[i]);
			}
		}

		return calendar;
	}

	/**
	 * 获取下一个匹配日期时间<br>
	 * 获取方法是，先从年开始查找对应部分的下一个值：
	 * <ul>
	 *     <li>如果此部分下个值不变，获取下一个部分</li>
	 *     <li>如果此部分下个值大于给定值，以下所有值置为最小值</li>
	 *     <li>如果此部分下个值小于给定值，回退到上一个值获取下一个新值，之后的值置为最小值</li>
	 * </ul>
	 *
	 * <pre>
	 *        秒 分 时 日 月 周 年
	 *     下 &lt;-----------------&gt; 上
	 * </pre>
	 *
	 * @param values 时间字段值，{second, minute, hour, dayOfMonth, monthBase1, dayOfWeekBase0, year}
	 * @return {@link Calendar}，毫秒数为0
	 */
	private int[] nextMatchValuesAfter(final int[] values) {
		final int[] newValues = values.clone();

		int i = Part.YEAR.ordinal();
		// 新值，-1表示标识为回退
		int nextValue = 0;
		while (i >= 0) {
			if (i == Part.DAY_OF_WEEK.ordinal()) {
				// 周不参与计算
				i--;
				continue;
			}

			if (i == Part.DAY_OF_MONTH.ordinal()) {
				final boolean isLeapYear = DateUtil.isLeapYear(newValues[Part.YEAR.ordinal()]);
				final int month = newValues[Part.MONTH.ordinal()];
				nextValue = ((DayOfMonthMatcher) matchers[i]).nextAfter(values[i], month, isLeapYear);
			} else {
				nextValue = matchers[i].nextAfter(values[i]);
			}

			if (nextValue > values[i]) {
				// 此部分正常获取新值，结束循环，后续的部分置最小值
				newValues[i] = nextValue;
				i--;
				break;
			} else if (nextValue < values[i]) {
				// 此部分下一个值获取到的值产生回退，回到上一个部分，继续获取新值
				i++;
				nextValue = -1;// 标记回退查找
				break;
			}

			// 值不变，检查下一个部分
			i--;
		}

		// 值产生回退，向上查找变更值
		if (-1 == nextValue) {
			while (i <= Part.YEAR.ordinal()) {
				if (i == Part.DAY_OF_WEEK.ordinal()) {
					// 周不参与计算
					i++;
					continue;
				}

				if (i == Part.DAY_OF_MONTH.ordinal()) {
					final boolean isLeapYear = DateUtil.isLeapYear(newValues[Part.YEAR.ordinal()]);
					final int month = newValues[Part.MONTH.ordinal()];
					nextValue = ((DayOfMonthMatcher) matchers[i]).nextAfter(values[i] + 1, month, isLeapYear);
				} else {
					nextValue = matchers[i].nextAfter(values[i] + 1);
				}

				if (nextValue > values[i]) {
					newValues[i] = nextValue;
					i--;
					break;
				}
				i++;
			}
		}

		// 修改值以下的字段全部归最小值
		setToMin(newValues, i);
		return newValues;
	}

	/**
	 * 设置从{@link Part#SECOND}到指定部分，全部设置为最小值
	 *
	 * @param values 值数组
	 * @param toPart 截止的部分
	 */
	private void setToMin(final int[] values, final int toPart) {
		Part part;
		for (int i = 0; i <= toPart; i++) {
			part = Part.of(i);
			values[i] = getMin(part);
		}
	}

	/**
	 * 获取表达式部分的最小值
	 *
	 * @param part {@link Part}
	 * @return 最小值，如果匹配所有，返回对应部分范围的最小值
	 */
	private int getMin(final Part part) {
		final PartMatcher matcher = get(part);

		final int min;
		if (matcher instanceof AlwaysTrueMatcher) {
			// 匹配所有时，获取这个字段本身的最小值
			min = part.getMin();
		} else if (matcher instanceof BoolArrayMatcher) {
			// 获取用户定义的最小值
			min = ((BoolArrayMatcher) matcher).getMinValue();
		} else {
			throw new IllegalArgumentException("Invalid matcher: " + matcher.getClass().getName());
		}
		return min;
	}
	//endregion

	/**
	 * 设置对应部分修正后的值<br>
	 * <ul>
	 *     <li>月在表达式中从1开始，但是{@link Calendar}中是从0开始的，需要-1</li>
	 *     <li>周在表达式中从0开始（0表示周日），但是{@link Calendar}中是从1开始的（1表示周日），需要+1</li>
	 * </ul>
	 *
	 * @param calendar {@link Calendar}
	 * @param part     表达式部分
	 * @param value    值
	 * @return {@link Calendar}
	 */
	private Calendar setValue(final Calendar calendar, final Part part, int value) {
		switch (part) {
			case MONTH:
				value -= 1;
				break;
			case DAY_OF_WEEK:
				value += 1;
				break;
		}
		//noinspection MagicConstant
		calendar.set(part.getCalendarField(), value);
		//Console.log("Set [{}] as [{}]", part, value);
		return calendar;
	}
}
