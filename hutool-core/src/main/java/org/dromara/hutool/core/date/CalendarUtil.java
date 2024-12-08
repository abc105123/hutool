/*
 * Copyright (c) 2013-2024 Hutool Team and hutool.cn
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

import org.dromara.hutool.core.comparator.CompareUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.math.ChineseNumberFormatter;
import org.dromara.hutool.core.date.format.DateFormatManager;
import org.dromara.hutool.core.date.format.parser.DateParser;
import org.dromara.hutool.core.date.format.parser.FastDateParser;
import org.dromara.hutool.core.date.format.parser.PositionDateParser;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.util.ObjUtil;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParsePosition;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 针对{@link Calendar} 对象封装工具类
 *
 * @author looly
 * @since 5.3.0
 */
public class CalendarUtil {

	// region ----- calendar

	/**
	 * 创建Calendar对象，时间为默认时区的当前时间
	 *
	 * @return Calendar对象
	 * @since 4.6.6
	 */
	public static Calendar calendar() {
		return Calendar.getInstance();
	}

	/**
	 * 转换为Calendar对象
	 *
	 * @param date 日期对象
	 * @return Calendar对象
	 */
	public static Calendar calendar(final Date date) {
		if (date instanceof DateTime) {
			return ((DateTime) date).toCalendar();
		} else {
			return calendar(date.getTime());
		}
	}

	/**
	 * 转换为Calendar对象
	 *
	 * @param calendar 日期对象
	 * @return Calendar对象
	 */
	public static Calendar calendar(final XMLGregorianCalendar calendar) {
		return calendar.toGregorianCalendar();
	}

	/**
	 * 转换为Calendar对象，使用当前默认时区
	 *
	 * @param millis 时间戳
	 * @return Calendar对象
	 */
	public static Calendar calendar(final long millis) {
		return calendar(millis, TimeZone.getDefault());
	}

	/**
	 * 转换为Calendar对象
	 *
	 * @param millis   时间戳
	 * @param timeZone 时区
	 * @return Calendar对象
	 * @since 5.7.22
	 */
	public static Calendar calendar(final long millis, final TimeZone timeZone) {
		final Calendar cal = Calendar.getInstance(timeZone);
		cal.setTimeInMillis(millis);
		return cal;
	}

	/**
	 * 转换为指定时区的Calendar，返回新的Calendar
	 *
	 * @param calendar 时间
	 * @param timeZone 新时区
	 * @return 指定时区的新的calendar对象
	 */
	public static Calendar calendar(Calendar calendar, final TimeZone timeZone) {
		// 转换到统一时区，例如UTC
		calendar = (Calendar) calendar.clone();
		calendar.setTimeZone(timeZone);
		return calendar;
	}
	// endregion

	/**
	 * 是否为上午
	 *
	 * @param calendar {@link Calendar}
	 * @return 是否为上午
	 */
	public static boolean isAM(final Calendar calendar) {
		return Calendar.AM == calendar.get(Calendar.AM_PM);
	}

	/**
	 * 是否为下午
	 *
	 * @param calendar {@link Calendar}
	 * @return 是否为下午
	 */
	public static boolean isPM(final Calendar calendar) {
		return Calendar.PM == calendar.get(Calendar.AM_PM);
	}

	// region ----- modify 时间修改

	/**
	 * 修改日期为某个时间字段起始时间
	 *
	 * @param calendar  {@link Calendar}
	 * @param dateField 保留到的时间字段，如定义为 {@link DateField#SECOND}，表示这个字段不变，这个字段以下字段全部归0
	 * @return 原{@link Calendar}
	 */
	public static Calendar truncate(final Calendar calendar, final DateField dateField) {
		return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.TRUNCATE);
	}

	/**
	 * 修改日期为某个时间字段四舍五入时间
	 *
	 * @param calendar  {@link Calendar}
	 * @param dateField 时间字段，即保留到哪个日期字段
	 * @return 原{@link Calendar}
	 */
	public static Calendar round(final Calendar calendar, final DateField dateField) {
		return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.ROUND);
	}

	/**
	 * 修改日期为某个时间字段结束时间<br>
	 * 可选是否归零毫秒。
	 *
	 * <p>
	 * 有时候由于毫秒部分必须为0（如MySQL数据库中），因此在此加上选项。
	 * </p>
	 *
	 * @param calendar            {@link Calendar}
	 * @param dateField           时间字段
	 * @param truncateMillisecond 是否毫秒归零
	 * @return 原{@link Calendar}
	 */
	public static Calendar ceiling(final Calendar calendar, final DateField dateField, final boolean truncateMillisecond) {
		return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.CEILING, truncateMillisecond);
	}

	/**
	 * 修改秒级别的开始时间，即忽略毫秒部分
	 *
	 * @param calendar 日期 {@link Calendar}
	 * @return {@link Calendar}
	 * @since 4.6.2
	 */
	public static Calendar beginOfSecond(final Calendar calendar) {
		return truncate(calendar, DateField.SECOND);
	}

	/**
	 * 修改秒级别的结束时间，即毫秒设置为999
	 *
	 * @param calendar            日期 {@link Calendar}
	 * @param truncateMillisecond 是否毫秒归零
	 * @return {@link Calendar}
	 * @since 4.6.2
	 */
	public static Calendar endOfSecond(final Calendar calendar, final boolean truncateMillisecond) {
		return ceiling(calendar, DateField.SECOND, truncateMillisecond);
	}

	/**
	 * 修改某小时的开始时间
	 *
	 * @param calendar 日期 {@link Calendar}
	 * @return {@link Calendar}
	 */
	public static Calendar beginOfHour(final Calendar calendar) {
		return truncate(calendar, DateField.HOUR_OF_DAY);
	}

	/**
	 * 修改某小时的结束时间
	 *
	 * @param calendar            日期 {@link Calendar}
	 * @param truncateMillisecond 是否毫秒归零
	 * @return {@link Calendar}
	 */
	public static Calendar endOfHour(final Calendar calendar, final boolean truncateMillisecond) {
		return ceiling(calendar, DateField.HOUR_OF_DAY, truncateMillisecond);
	}

	/**
	 * 修改某分钟的开始时间
	 *
	 * @param calendar 日期 {@link Calendar}
	 * @return {@link Calendar}
	 */
	public static Calendar beginOfMinute(final Calendar calendar) {
		return truncate(calendar, DateField.MINUTE);
	}

	/**
	 * 修改某分钟的结束时间
	 *
	 * @param calendar            日期 {@link Calendar}
	 * @param truncateMillisecond 是否毫秒归零
	 * @return {@link Calendar}
	 */
	public static Calendar endOfMinute(final Calendar calendar, final boolean truncateMillisecond) {
		return ceiling(calendar, DateField.MINUTE, truncateMillisecond);
	}

	/**
	 * 修改某天的开始时间
	 *
	 * @param calendar 日期 {@link Calendar}
	 * @return {@link Calendar}
	 */
	public static Calendar beginOfDay(final Calendar calendar) {
		return truncate(calendar, DateField.DAY_OF_MONTH);
	}

	/**
	 * 修改某天的结束时间
	 *
	 * @param calendar            日期 {@link Calendar}
	 * @param truncateMillisecond 是否毫秒归零
	 * @return {@link Calendar}
	 */
	public static Calendar endOfDay(final Calendar calendar, final boolean truncateMillisecond) {
		return ceiling(calendar, DateField.DAY_OF_MONTH, truncateMillisecond);
	}

	/**
	 * 修改给定日期当前周的开始时间，周一定为一周的开始时间
	 *
	 * @param calendar 日期 {@link Calendar}
	 * @return {@link Calendar}
	 */
	public static Calendar beginOfWeek(final Calendar calendar) {
		return beginOfWeek(calendar, true);
	}

	/**
	 * 修改给定日期当前周的开始时间
	 *
	 * @param calendar           日期 {@link Calendar}
	 * @param isMondayAsFirstDay 是否周一做为一周的第一天（false表示周日做为第一天）
	 * @return {@link Calendar}
	 * @since 3.1.2
	 */
	public static Calendar beginOfWeek(final Calendar calendar, final boolean isMondayAsFirstDay) {
		calendar.setFirstDayOfWeek(isMondayAsFirstDay ? Calendar.MONDAY : Calendar.SUNDAY);
		// WEEK_OF_MONTH为上限的字段（不包括），实际调整的为DAY_OF_MONTH
		return truncate(calendar, DateField.WEEK_OF_MONTH);
	}

	/**
	 * 修改某周的结束时间
	 *
	 * @param calendar            日期 {@link Calendar}
	 * @param isSundayAsLastDay   是否周日做为一周的最后一天（false表示周六做为最后一天）
	 * @param truncateMillisecond 是否毫秒归零
	 * @return {@link Calendar}
	 */
	public static Calendar endOfWeek(final Calendar calendar, final boolean isSundayAsLastDay, final boolean truncateMillisecond) {
		calendar.setFirstDayOfWeek(isSundayAsLastDay ? Calendar.MONDAY : Calendar.SUNDAY);
		// WEEK_OF_MONTH为上限的字段（不包括），实际调整的为DAY_OF_MONTH
		return ceiling(calendar, DateField.WEEK_OF_MONTH, truncateMillisecond);
	}

	/**
	 * 修改某月的开始时间
	 *
	 * @param calendar 日期 {@link Calendar}
	 * @return {@link Calendar}
	 */
	public static Calendar beginOfMonth(final Calendar calendar) {
		return truncate(calendar, DateField.MONTH);
	}

	/**
	 * 修改某月的结束时间
	 *
	 * @param calendar            日期 {@link Calendar}
	 * @param truncateMillisecond 是否毫秒归零
	 * @return {@link Calendar}
	 */
	public static Calendar endOfMonth(final Calendar calendar, final boolean truncateMillisecond) {
		return ceiling(calendar, DateField.MONTH, truncateMillisecond);
	}

	/**
	 * 修改某季度的开始时间
	 *
	 * @param calendar 日期 {@link Calendar}
	 * @return {@link Calendar}
	 * @since 4.1.0
	 */
	public static Calendar beginOfQuarter(final Calendar calendar) {
		//noinspection MagicConstant
		calendar.set(Calendar.MONTH, calendar.get(DateField.MONTH.getValue()) / 3 * 3);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return beginOfDay(calendar);
	}

	/**
	 * 获取某季度的结束时间
	 *
	 * @param calendar            日期 {@link Calendar}
	 * @param truncateMillisecond 是否毫秒归零
	 * @return {@link Calendar}
	 * @since 4.1.0
	 */
	@SuppressWarnings({"MagicConstant", "ConstantConditions"})
	public static Calendar endOfQuarter(final Calendar calendar, final boolean truncateMillisecond) {
		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(DateField.MONTH.getValue()) / 3 * 3 + 2;

		final Calendar resultCal = Calendar.getInstance(calendar.getTimeZone());
		resultCal.set(year, month, Month.of(month).getLastDay(DateUtil.isLeapYear(year)));

		return endOfDay(resultCal, truncateMillisecond);
	}

	/**
	 * 修改某年的开始时间
	 *
	 * @param calendar 日期 {@link Calendar}
	 * @return {@link Calendar}
	 */
	public static Calendar beginOfYear(final Calendar calendar) {
		return truncate(calendar, DateField.YEAR);
	}

	/**
	 * 修改某年的结束时间
	 *
	 * @param calendar            日期 {@link Calendar}
	 * @param truncateMillisecond 是否毫秒归零
	 * @return {@link Calendar}
	 */
	public static Calendar endOfYear(final Calendar calendar, final boolean truncateMillisecond) {
		return ceiling(calendar, DateField.YEAR, truncateMillisecond);
	}
	// endregion

	// region ----- isSame

	/**
	 * 比较两个日期是否为同一天
	 *
	 * @param cal1 日期1
	 * @param cal2 日期2
	 * @return 是否为同一天
	 */
	public static boolean isSameDay(final Calendar cal1, Calendar cal2) {
		if (ObjUtil.notEquals(cal1.getTimeZone(), cal2.getTimeZone())) {
			// 统一时区
			cal2 = calendar(cal2, cal1.getTimeZone());
		}
		return isSameYear(cal1, cal2) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 比较两个日期是否为同一周<br>
	 * 同一个周的意思是：ERA（公元）、year（年）、month（月）、week（周）都一致。
	 *
	 * @param cal1  日期1
	 * @param cal2  日期2
	 * @param isMon 一周的第一天是否为周一。国内第一天为星期一，国外第一天为星期日
	 * @return 是否为同一周
	 * @since 5.7.21
	 */
	public static boolean isSameWeek(Calendar cal1, Calendar cal2, final boolean isMon) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The date must not be null");
		}

		// 防止比较前修改原始Calendar对象
		cal1 = (Calendar) cal1.clone();

		if (ObjUtil.notEquals(cal1.getTimeZone(), cal2.getTimeZone())) {
			// 统一时区
			cal2 = calendar(cal2, cal1.getTimeZone());
		} else {
			cal2 = (Calendar) cal2.clone();
		}

		// 把所传日期设置为其当前周的第一天
		// 比较设置后的两个日期是否是同一天：true 代表同一周
		if (isMon) {
			cal1.setFirstDayOfWeek(Calendar.MONDAY);
			cal1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			cal2.setFirstDayOfWeek(Calendar.MONDAY);
			cal2.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		} else {
			cal1.setFirstDayOfWeek(Calendar.SUNDAY);
			cal1.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			cal2.setFirstDayOfWeek(Calendar.SUNDAY);
			cal2.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		}
		return isSameDay(cal1, cal2);
	}

	/**
	 * 比较两个日期是否为同一月<br>
	 * 同一个月的意思是：ERA（公元）、year（年）、month（月）都一致。
	 *
	 * @param cal1 日期1
	 * @param cal2 日期2
	 * @return 是否为同一月
	 * @since 5.4.1
	 */
	public static boolean isSameMonth(final Calendar cal1, Calendar cal2) {
		if (ObjUtil.notEquals(cal1.getTimeZone(), cal2.getTimeZone())) {
			// 统一时区
			cal2 = calendar(cal2, cal1.getTimeZone());
		}

		return isSameYear(cal1, cal2) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
	}

	/**
	 * 比较两个日期是否为同一年<br>
	 * 同一个年的意思是：ERA（公元）、year（年）都一致。
	 *
	 * @param cal1 日期1
	 * @param cal2 日期2
	 * @return 是否为同一年
	 */
	public static boolean isSameYear(final Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The date must not be null");
		}

		if (ObjUtil.notEquals(cal1.getTimeZone(), cal2.getTimeZone())) {
			// 统一时区
			cal2 = calendar(cal2, cal1.getTimeZone());
		}

		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && //
			// issue#3011@Github
			cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA);
	}

	/**
	 * <p>检查两个Calendar时间戳是否相同。</p>
	 *
	 * <p>此方法检查两个Calendar的毫秒数时间戳是否相同。</p>
	 *
	 * @param date1 时间1
	 * @param date2 时间2
	 * @return 两个Calendar时间戳是否相同。如果两个时间都为{@code null}返回true，否则有{@code null}返回false
	 * @since 5.3.11
	 */
	public static boolean isSameInstant(final Calendar date1, final Calendar date2) {
		if (null == date1) {
			return null == date2;
		}
		if (null == date2) {
			return false;
		}

		return date1.getTimeInMillis() == date2.getTimeInMillis();
	}
	// endregion

	/**
	 * 获得指定日期年份和季度<br>
	 * 格式：[20131]表示2013年第一季度
	 *
	 * @param cal 日期
	 * @return 年和季度，格式类似于20131
	 */
	public static String yearAndQuarter(final Calendar cal) {
		return StrUtil.builder().append(cal.get(Calendar.YEAR)).append(cal.get(Calendar.MONTH) / 3 + 1).toString();
	}

	/**
	 * 获取指定日期字段的最小值，例如分钟的最小值是0
	 *
	 * @param calendar  {@link Calendar}
	 * @param dateField {@link DateField}
	 * @return 字段最小值
	 * @see Calendar#getActualMinimum(int)
	 * @since 5.4.2
	 */
	public static int getBeginValue(final Calendar calendar, final DateField dateField) {
		return getBeginValue(calendar, dateField.getValue());
	}

	/**
	 * 获取指定日期字段的最小值，例如分钟的最小值是0
	 *
	 * @param calendar  {@link Calendar}
	 * @param dateField {@link DateField}
	 * @return 字段最小值
	 * @see Calendar#getActualMinimum(int)
	 * @since 4.5.7
	 */
	public static int getBeginValue(final Calendar calendar, final int dateField) {
		if (Calendar.DAY_OF_WEEK == dateField) {
			return calendar.getFirstDayOfWeek();
		}
		return calendar.getActualMinimum(dateField);
	}

	/**
	 * 获取指定日期字段的最大值，例如分钟的最大值是59
	 *
	 * @param calendar  {@link Calendar}
	 * @param dateField {@link DateField}
	 * @return 字段最大值
	 * @see Calendar#getActualMaximum(int)
	 * @since 5.4.2
	 */
	public static int getEndValue(final Calendar calendar, final DateField dateField) {
		return getEndValue(calendar, dateField.getValue());
	}

	/**
	 * 获取指定日期字段的最大值，例如分钟的最大值是59
	 *
	 * @param calendar  {@link Calendar}
	 * @param dateField {@link DateField}
	 * @return 字段最大值
	 * @see Calendar#getActualMaximum(int)
	 * @since 4.5.7
	 */
	public static int getEndValue(final Calendar calendar, final int dateField) {
		if (Calendar.DAY_OF_WEEK == dateField) {
			return (calendar.getFirstDayOfWeek() + 6) % 7;
		}
		return calendar.getActualMaximum(dateField);
	}

	/**
	 * 获得日期的某个部分<br>
	 * 例如获得年的部分，则使用 getField(DatePart.YEAR)
	 *
	 * @param calendar {@link Calendar}
	 * @param field    表示日期的哪个部分的枚举 {@link DateField}
	 * @return 某个部分的值
	 * @since 6.0.0
	 */
	@SuppressWarnings("MagicConstant")
	public static int getField(final Calendar calendar, final DateField field) {
		return Assert.notNull(calendar).get(Assert.notNull(field).getValue());
	}

	/**
	 * Calendar{@link Instant}对象
	 *
	 * @param calendar Date对象
	 * @return {@link Instant}对象
	 * @since 5.0.5
	 */
	public static Instant toInstant(final Calendar calendar) {
		return null == calendar ? null : calendar.toInstant();
	}

	/**
	 * {@link Calendar} 转换为 {@link LocalDateTime}，使用系统默认时区
	 *
	 * @param calendar {@link Calendar}
	 * @return {@link LocalDateTime}
	 * @since 5.0.5
	 */
	public static LocalDateTime toLocalDateTime(final Calendar calendar) {
		if (null == calendar) {
			return null;
		}
		return LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
	}

	/**
	 * {@code null}安全的{@link Calendar}比较，{@code null}小于任何日期
	 *
	 * @param calendar1 日期1
	 * @param calendar2 日期2
	 * @return 比较结果，如果calendar1 &lt; calendar2，返回数小于0，calendar1==calendar2返回0，calendar1 &gt; calendar2 大于0
	 * @since 4.6.2
	 */
	public static int compare(final Calendar calendar1, final Calendar calendar2) {
		return CompareUtil.compare(calendar1, calendar2);
	}

	/**
	 * 计算相对于dateToCompare的年龄，常用于计算指定生日在某年的年龄<br>
	 * 按照《最高人民法院关于审理未成年人刑事案件具体应用法律若干问题的解释》第二条规定刑法第十七条规定的“周岁”，按照公历的年、月、日计算，从周岁生日的第二天起算。
	 * <ul>
	 *     <li>2022-03-01出生，则相对2023-03-01，周岁为0，相对于2023-03-02才是1岁。</li>
	 *     <li>1999-02-28出生，则相对2000-02-29，周岁为1</li>
	 * </ul>
	 *
	 * @param birthday      生日
	 * @param dateToCompare 需要对比的日期
	 * @return 年龄
	 */
	public static int age(final Calendar birthday, final Calendar dateToCompare) {
		return age(birthday.getTimeInMillis(), dateToCompare.getTimeInMillis());
	}

	/**
	 * 将指定Calendar时间格式化为纯中文形式，比如：
	 *
	 * <pre>
	 *     2018-02-24 12:13:14 转换为 二〇一八年二月二十四日（withTime为false）
	 *     2018-02-24 12:13:14 转换为 二〇一八年二月二十四日十二时十三分十四秒（withTime为true）
	 * </pre>
	 *
	 * @param calendar {@link Calendar}
	 * @param withTime 是否包含时间部分
	 * @return 格式化后的字符串
	 * @since 5.3.9
	 */
	public static String formatChineseDate(final Calendar calendar, final boolean withTime) {
		final StringBuilder result = StrUtil.builder();

		// 年
		final String year = String.valueOf(calendar.get(Calendar.YEAR));
		final int length = year.length();
		for (int i = 0; i < length; i++) {
			result.append(ChineseNumberFormatter.formatChar(year.charAt(i), false));
		}
		result.append('年');

		// 月
		final int month = calendar.get(Calendar.MONTH) + 1;
		result.append(ChineseNumberFormatter.of().setColloquialMode(true).format(month));
		result.append('月');

		// 日
		final int day = calendar.get(Calendar.DAY_OF_MONTH);
		result.append(ChineseNumberFormatter.of().setColloquialMode(true).format(day));
		result.append('日');

		// 只替换年月日，时分秒中零不需要替换
		final String temp = result.toString().replace('零', '〇');
		result.delete(0, result.length());
		result.append(temp);


		if (withTime) {
			// 时
			final int hour = calendar.get(Calendar.HOUR_OF_DAY);
			result.append(ChineseNumberFormatter.of().setColloquialMode(true).format(hour));
			result.append('时');
			// 分
			final int minute = calendar.get(Calendar.MINUTE);
			result.append(ChineseNumberFormatter.of().setColloquialMode(true).format(minute));
			result.append('分');
			// 秒
			final int second = calendar.get(Calendar.SECOND);
			result.append(ChineseNumberFormatter.of().setColloquialMode(true).format(second));
			result.append('秒');
		}

		return result.toString();
	}

	// region ----- parse

	/**
	 * 通过给定的日期格式解析日期时间字符串。<br>
	 * 传入的日期格式会逐个尝试，直到解析成功，返回{@link Calendar}对象，否则抛出{@link DateException}异常。
	 * 方法来自：Apache Commons-Lang3
	 *
	 * @param str           日期时间字符串，非空
	 * @param parsePatterns 需要尝试的日期时间格式数组，非空, 见SimpleDateFormat
	 * @return 解析后的Calendar
	 * @throws IllegalArgumentException if the date string or pattern array is null
	 * @throws DateException            if none of the date patterns were suitable
	 * @since 5.3.11
	 */
	public static Calendar parseByPatterns(final CharSequence str, final String... parsePatterns) throws DateException {
		return parseByPatterns(str, null, parsePatterns);
	}

	/**
	 * 通过给定的日期格式解析日期时间字符串。<br>
	 * 传入的日期格式会逐个尝试，直到解析成功，返回{@link Calendar}对象，否则抛出{@link DateException}异常。
	 * 方法来自：Apache Commons-Lang3
	 *
	 * @param str           日期时间字符串，非空
	 * @param locale        地区，当为{@code null}时使用{@link Locale#getDefault()}
	 * @param parsePatterns 需要尝试的日期时间格式数组，非空, 见SimpleDateFormat
	 * @return 解析后的Calendar
	 * @throws IllegalArgumentException if the date string or pattern array is null
	 * @throws DateException            if none of the date patterns were suitable
	 * @since 5.3.11
	 */
	public static Calendar parseByPatterns(final CharSequence str, final Locale locale, final String... parsePatterns) throws DateException {
		return parseByPatterns(str, locale, true, parsePatterns);
	}

	/**
	 * 通过给定的日期格式解析日期时间字符串。<br>
	 * 传入的日期格式会逐个尝试，直到解析成功，返回{@link Calendar}对象，否则抛出{@link DateException}异常。
	 * 方法来自：Apache Commons-Lang3
	 *
	 * @param str           日期时间字符串，非空
	 * @param locale        地区，当为{@code null}时使用{@link Locale#getDefault()}
	 * @param lenient       日期时间解析是否使用严格模式
	 * @param parsePatterns 需要尝试的日期时间格式数组，非空, 见SimpleDateFormat
	 * @return 解析后的Calendar
	 * @throws IllegalArgumentException if the date string or pattern array is null
	 * @throws DateException            if none of the date patterns were suitable
	 * @see java.util.Calendar#isLenient()
	 * @since 5.3.11
	 */
	public static Calendar parseByPatterns(final CharSequence str, final Locale locale, final boolean lenient, final String... parsePatterns) throws DateException {
		if (str == null || parsePatterns == null) {
			throw new IllegalArgumentException("Date and Patterns must not be null");
		}

		final TimeZone tz = TimeZone.getDefault();
		final Locale lcl = ObjUtil.defaultIfNull(locale, Locale.getDefault());
		final ParsePosition pos = new ParsePosition(0);
		final Calendar calendar = Calendar.getInstance(tz, lcl);
		calendar.setLenient(lenient);

		final DateFormatManager formatManager = DateFormatManager.getInstance();
		for (final String parsePattern : parsePatterns) {
			if (formatManager.isCustomParse(parsePattern)) {
				final Date parse = formatManager.parse(str, parsePattern);
				if (null == parse) {
					continue;
				}
				calendar.setTime(parse);
				return calendar;
			}

			final FastDateParser fdp = new FastDateParser(parsePattern, tz, lcl);
			calendar.clear();
			try {
				if (fdp.parse(str, pos, calendar) && pos.getIndex() == str.length()) {
					return calendar;
				}
			} catch (final IllegalArgumentException ignore) {
				// leniency is preventing calendar from being set
			}
			pos.setIndex(0);
		}

		throw new DateException("Unable to parse the date: {}", str);
	}

	/**
	 * 使用指定{@link DateParser}解析字符串为{@link Calendar}
	 *
	 * @param str     日期字符串
	 * @param parser  {@link DateParser}
	 * @param lenient 是否宽容模式
	 * @return 解析后的 {@link Calendar}，解析失败抛出异常
	 * @throws DateException 解析失败抛出此异常
	 */
	public static Calendar parse(final CharSequence str, final PositionDateParser parser,
								 final boolean lenient) throws DateException{
		Assert.notBlank(str, "Date str must be not blank!");
		Assert.notNull(parser, "Parser must be not null!");
		final Calendar calendar = Calendar.getInstance(parser.getTimeZone(), parser.getLocale());
		calendar.clear();
		calendar.setLenient(lenient);

		final ParsePosition position = new ParsePosition(0);
		if (parser.parse(str.toString(), position, calendar)) {
			return calendar;
		}

		throw new DateException("Parse [{}] with format [{}] error, at: {}",
			str, parser.getPattern(), position.getErrorIndex());
	}
	// endregion

	/**
	 * 计算相对于dateToCompare的年龄（周岁），常用于计算指定生日在某年的年龄<br>
	 * 按照《最高人民法院关于审理未成年人刑事案件具体应用法律若干问题的解释》第二条规定刑法第十七条规定的“周岁”，按照公历的年、月、日计算，从周岁生日的第二天起算。
	 * <ul>
	 *     <li>2022-03-01出生，则相对2023-03-01，周岁为0，相对于2023-03-02才是1岁。</li>
	 *     <li>1999-02-28出生，则相对2000-02-29，周岁为1</li>
	 * </ul>
	 *
	 * @param birthday      生日
	 * @param dateToCompare 需要对比的日期
	 * @return 年龄
	 */
	protected static int age(final long birthday, final long dateToCompare) {
		if (birthday > dateToCompare) {
			throw new IllegalArgumentException("Birthday is after dateToCompare!");
		}

		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateToCompare);

		final int year = cal.get(Calendar.YEAR);
		final int month = cal.get(Calendar.MONTH);
		final int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

		// 复用cal
		cal.setTimeInMillis(birthday);
		int age = year - cal.get(Calendar.YEAR);
		//当前日期，则为0岁
		if (age == 0) {
			return 0;
		}

		final int monthBirth = cal.get(Calendar.MONTH);
		if (month == monthBirth) {
			final int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
			// issue#I6E6ZG，法定生日当天不算年龄，从第二天开始计算
			if (dayOfMonth <= dayOfMonthBirth) {
				// 如果生日在当月，但是未达到生日当天的日期，年龄减一
				age--;
			}
		} else if (month < monthBirth) {
			// 如果当前月份未达到生日的月份，年龄计算减一
			age--;
		}

		return age;
	}

	/**
	 * 是否为本月第一天
	 *
	 * @param calendar {@link Calendar}
	 * @return 是否为本月最后一天
	 * @since 6.0.0
	 */
	public static boolean isFirstDayOfMonth(final Calendar calendar) {
		return 1 == calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 是否为本月最后一天
	 *
	 * @param calendar {@link Calendar}
	 * @return 是否为本月最后一天
	 * @since 5.8.27
	 */
	public static boolean isLastDayOfMonth(final Calendar calendar) {
		return calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
}
