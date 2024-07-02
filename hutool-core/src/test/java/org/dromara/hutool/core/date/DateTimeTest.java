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

package org.dromara.hutool.core.date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.TimeZone;

/**
 * DateTime单元测试
 *
 * @author Looly
 *
 */
public class DateTimeTest {

	@Test
	public void datetimeTest() {
		final DateTime dateTime = new DateTime("2017-01-05 12:34:23", DatePattern.NORM_DATETIME_FORMAT);

		// 年
		final int year = dateTime.year();
		Assertions.assertEquals(2017, year);

		// 季度（非季节）
		final Quarter season = dateTime.quarterEnum();
		Assertions.assertEquals(Quarter.Q1, season);

		// 月份
		final Month month = dateTime.monthEnum();
		Assertions.assertEquals(Month.JANUARY, month);

		// 日
		final int day = dateTime.dayOfMonth();
		Assertions.assertEquals(5, day);
	}

	@Test
	public void datetimeTest2() {
		final DateTime dateTime = new DateTime("2017-01-05 12:34:23");

		// 年
		final int year = dateTime.year();
		Assertions.assertEquals(2017, year);

		// 季度（非季节）
		final Quarter season = dateTime.quarterEnum();
		Assertions.assertEquals(Quarter.Q1, season);

		// 月份
		final Month month = dateTime.monthEnum();
		Assertions.assertEquals(Month.JANUARY, month);

		// 日
		final int day = dateTime.dayOfMonth();
		Assertions.assertEquals(5, day);
	}

	@Test
	public void quarterTest() {
		DateTime dateTime = new DateTime("2017-01-05 12:34:23", DatePattern.NORM_DATETIME_FORMAT);
		Quarter quarter = dateTime.quarterEnum();
		Assertions.assertEquals(Quarter.Q1, quarter);

		dateTime = new DateTime("2017-04-05 12:34:23", DatePattern.NORM_DATETIME_FORMAT);
		quarter = dateTime.quarterEnum();
		Assertions.assertEquals(Quarter.Q2, quarter);

		dateTime = new DateTime("2017-07-05 12:34:23", DatePattern.NORM_DATETIME_FORMAT);
		quarter = dateTime.quarterEnum();
		Assertions.assertEquals(Quarter.Q3, quarter);

		dateTime = new DateTime("2017-10-05 12:34:23", DatePattern.NORM_DATETIME_FORMAT);
		quarter = dateTime.quarterEnum();
		Assertions.assertEquals(Quarter.Q4, quarter);

		// 精确到毫秒
		final DateTime beginTime = new DateTime("2017-10-01 00:00:00.000", DatePattern.NORM_DATETIME_MS_FORMAT);
		dateTime = DateUtil.beginOfQuarter(dateTime);
		Assertions.assertEquals(beginTime, dateTime);

		// 精确到毫秒
		final DateTime endTime = new DateTime("2017-12-31 23:59:59.999", DatePattern.NORM_DATETIME_MS_FORMAT);
		dateTime = DateUtil.endOfQuarter(dateTime);
		Assertions.assertEquals(endTime, dateTime);
	}

	@Test
	public void mutableTest() {
		final DateTime dateTime = new DateTime("2017-01-05 12:34:23", DatePattern.NORM_DATETIME_FORMAT);

		// 默认情况下DateTime为可变对象
		DateTime offsite = dateTime.offset(DateField.YEAR, 0);
		Assertions.assertSame(offsite, dateTime);

		// 设置为不可变对象后变动将返回新对象
		dateTime.setMutable(false);
		offsite = dateTime.offset(DateField.YEAR, 0);
		Assertions.assertNotSame(offsite, dateTime);
	}

	@Test
	public void toStringTest() {
		final DateTime dateTime = new DateTime("2017-01-05 12:34:23", DatePattern.NORM_DATETIME_FORMAT);
		Assertions.assertEquals("2017-01-05 12:34:23", dateTime.toString());

		final String dateStr = dateTime.toString("yyyy/MM/dd");
		Assertions.assertEquals("2017/01/05", dateStr);
	}

	@Test
	public void toStringTest2() {
		final DateTime dateTime = new DateTime("2017-01-05 12:34:23", DatePattern.NORM_DATETIME_FORMAT);

		String dateStr = dateTime.toString(DatePattern.ISO8601_WITH_ZONE_OFFSET_PATTERN);
		Assertions.assertEquals("2017-01-05T12:34:23+0800", dateStr);

		dateStr = dateTime.toString(DatePattern.ISO8601_WITH_XXX_OFFSET_PATTERN);
		Assertions.assertEquals("2017-01-05T12:34:23+08:00", dateStr);
	}

	@Test
	public void toStringWithTimeZoneTest() {
		final DateTime dateTime = new DateTime("2017-01-05 12:34:23", DatePattern.NORM_DATETIME_FORMAT);

		final String dateStr = dateTime.toString(TimeZone.getTimeZone("UTC"));
		Assertions.assertEquals("2017-01-05 04:34:23", dateStr);

		dateTime.setTimeZone(TimeZone.getTimeZone("UTC"));
		Assertions.assertEquals("2017-01-05 04:34:23", dateTime.toString());
	}

	@Test
	public void monthTest() {
		//noinspection ConstantConditions
		final int month = DateUtil.date(DateUtil.parse("2017-07-01")).month();
		Assertions.assertEquals(6, month);
	}

	@Test
	public void weekOfYearTest() {
		final DateTime date = DateUtil.date(DateUtil.parse("2016-12-27"));

		Assertions.assertEquals(2016, date.year());
		//跨年的周返回的总是1
		Assertions.assertEquals(1, date.weekOfYear());
	}

	/**
	 * 严格模式下，不允许非常规的数字，如秒部分最多59，99则报错
	 */
	@Test
	public void ofTest(){
		Assertions.assertThrows(IllegalArgumentException.class, ()->{
			final String a = "2021-09-27 00:00:99";
			new DateTime(a, DatePattern.NORM_DATETIME_FORMAT, false);
		});

	}

}
