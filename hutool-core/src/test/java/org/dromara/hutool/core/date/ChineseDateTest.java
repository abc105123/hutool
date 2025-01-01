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

import org.dromara.hutool.core.date.chinese.ChineseDate;
import org.dromara.hutool.core.date.chinese.ChineseDateFormat;
import org.dromara.hutool.core.text.StrUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class ChineseDateTest {

	@Test
	public void chineseDateTest() {
		ChineseDate date = new ChineseDate(Objects.requireNonNull(DateUtil.parse("2020-01-25")));
		Assertions.assertEquals("2020-01-25 00:00:00", date.getGregorianDate().toString());
		Assertions.assertEquals(2020, date.getChineseYear());

		Assertions.assertEquals(1, date.getMonth());
		Assertions.assertEquals("正月", date.getChineseMonth());
		Assertions.assertEquals("正月", date.getChineseMonthName());


		Assertions.assertEquals(1, date.getDay());
		Assertions.assertEquals("初一", date.getChineseDay());

		Assertions.assertEquals("庚子", date.getCyclical());
		Assertions.assertEquals("鼠", date.getChineseZodiac());
		Assertions.assertEquals("春节", date.getFestivals());
		Assertions.assertEquals("农历庚子鼠年正月初一", date.toString());

		date = new ChineseDate(Objects.requireNonNull(DateUtil.parse("2020-01-14")));
		Assertions.assertEquals("农历己亥猪年腊月二十", date.toString());
		date = new ChineseDate(Objects.requireNonNull(DateUtil.parse("2020-01-24")));
		Assertions.assertEquals("农历己亥猪年腊月三十", date.toString());

		Assertions.assertEquals("2019-12-30", date.toStringNormal());
	}

	@Test
	public void toStringNormalTest(){
		final ChineseDate date = new ChineseDate(Objects.requireNonNull(DateUtil.parse("2020-03-1")));
		Assertions.assertEquals("2020-02-08", date.toStringNormal());
	}

	@Test
	public void parseTest(){
		ChineseDate date = new ChineseDate(Objects.requireNonNull(DateUtil.parse("1996-07-14")));
		Assertions.assertEquals("农历丙子鼠年五月廿九", date.toString());

		date = new ChineseDate(Objects.requireNonNull(DateUtil.parse("1996-07-15")));
		Assertions.assertEquals("农历丙子鼠年五月三十", date.toString());
	}

	@Test
	public void getChineseMonthTest(){
		ChineseDate chineseDate = new ChineseDate(2020,6,15);
		Assertions.assertEquals("2020-08-04 00:00:00", chineseDate.getGregorianDate().toString());
		Assertions.assertEquals("六月", chineseDate.getChineseMonth());

		chineseDate = new ChineseDate(2020,4,15);
		Assertions.assertEquals("2020-06-06 00:00:00", chineseDate.getGregorianDate().toString());
		Assertions.assertEquals("闰四月", chineseDate.getChineseMonth());

		chineseDate = new ChineseDate(2020,5,15);
		Assertions.assertEquals("2020-07-05 00:00:00", chineseDate.getGregorianDate().toString());
		Assertions.assertEquals("五月", chineseDate.getChineseMonth());
	}

	@Test
	public void getFestivalsTest(){
		// issue#I1XHSF@Gitee，2023-01-20对应农历腊月29，非除夕
		final ChineseDate chineseDate = new ChineseDate(Objects.requireNonNull(DateUtil.parse("2023-01-20")));
		Assertions.assertTrue(StrUtil.isEmpty(chineseDate.getFestivals()));
	}

	@Test
	public void dateTest(){
		// 修复这两个日期不正确的问题
		// 问题出在计算与1900-01-31相差天数的问题上了，相差天数非整天
		ChineseDate date = new ChineseDate(Objects.requireNonNull(DateUtil.parse("1991-09-14")));
		Assertions.assertEquals("农历辛未羊年八月初七", date.toString());
		date = new ChineseDate(Objects.requireNonNull(DateUtil.parse("1991-09-15")));
		Assertions.assertEquals("农历辛未羊年八月初八", date.toString());
	}

	@Test
	public void dateTest2(){
		//noinspection ConstantConditions
		final ChineseDate date = new ChineseDate(DateUtil.parse("2020-10-19"));
		Assertions.assertEquals("农历庚子鼠年九月初三", date.toString());
	}

	@Test
	public void dateTest2_2(){
		//noinspection ConstantConditions
		final ChineseDate date = new ChineseDate(DateUtil.parse("2020-07-20"));
		Assertions.assertEquals("农历庚子鼠年五月三十", date.toString());
	}

	@Test
	public void dateTest3(){
		// 初一，offset为0测试
		//noinspection ConstantConditions
		final ChineseDate date = new ChineseDate(DateUtil.parse("2099-03-22"));
		Assertions.assertEquals("农历己未羊年闰二月初一", date.toString());
	}

	@Test
	public void leapMonthTest(){
		//noinspection ConstantConditions
		final ChineseDate c1 = new ChineseDate(DateUtil.parse("2028-05-28"));
		//noinspection ConstantConditions
		final ChineseDate c2 = new ChineseDate(DateUtil.parse("2028-06-27"));

		Assertions.assertEquals("农历戊申猴年五月初五", c1.toString());
		Assertions.assertEquals("农历戊申猴年闰五月初五", c2.toString());
	}

	@Test
	public void getChineseMonthTest2(){
		//https://github.com/dromara/hutool/issues/2112
		final ChineseDate springFestival = new ChineseDate(Objects.requireNonNull(DateUtil.parse("2022-02-01")));
		final String chineseMonth = springFestival.getChineseMonth();
		Assertions.assertEquals("正月", chineseMonth);
	}

	@Test
	public void day19700101Test(){
		// https://gitee.com/dromara/hutool/issues/I4UTPK
		Date date = DateUtil.parse("1970-01-01");
		//noinspection ConstantConditions
		ChineseDate chineseDate = new ChineseDate(date);
		Assertions.assertEquals("农历己酉鸡年冬月廿四", chineseDate.toString());

		date = DateUtil.parse("1970-01-02");
		//noinspection ConstantConditions
		chineseDate = new ChineseDate(date);
		Assertions.assertEquals("农历己酉鸡年冬月廿五", chineseDate.toString());

		date = DateUtil.parse("1970-01-03");
		//noinspection ConstantConditions
		chineseDate = new ChineseDate(date);
		Assertions.assertEquals("农历己酉鸡年冬月廿六", chineseDate.toString());
	}

	@Test
	public void day19000101Test(){
		// 1900-01-31之前不支持
		final Date date = DateUtil.parse("1900-01-31");
		//noinspection ConstantConditions
		final ChineseDate chineseDate = new ChineseDate(date);
		Assertions.assertEquals("农历庚子鼠年正月初一", chineseDate.toString());
	}

	@Test
	public void getGregorianDateTest(){
		// https://gitee.com/dromara/hutool/issues/I4ZSGJ
		ChineseDate chineseDate = new ChineseDate(1998, 5, 1);
		Assertions.assertEquals("1998-06-24 00:00:00", chineseDate.getGregorianDate().toString());

		chineseDate = new ChineseDate(1998, 5, 1, false);
		Assertions.assertEquals("1998-05-26 00:00:00", chineseDate.getGregorianDate().toString());
	}

	@Test
	public void equalsTest(){
		// 二月初一
		final Date date1 = DateUtil.date(LocalDate.of(2023, 2, 20));
		// 润二月初一
		final Date date2 = DateUtil.date(LocalDate.of(2023, 3, 22));
		// 三月初一
		final Date date4 = DateUtil.date(LocalDate.of(2023, 4, 20));


		final ChineseDate chineseDate1 = new ChineseDate(date1);
		final ChineseDate chineseDate2 = new ChineseDate(date2);
		final ChineseDate chineseDate3 = new ChineseDate(date2);
		final ChineseDate chineseDate4 = new ChineseDate(date4);

		Assertions.assertEquals("2023-02-01", chineseDate1.toStringNormal());
		Assertions.assertEquals("2023-02-01", chineseDate2.toStringNormal());
		Assertions.assertEquals("2023-02-01", chineseDate3.toStringNormal());
		Assertions.assertEquals("2023-03-01", chineseDate4.toStringNormal());

		Assertions.assertNotEquals(chineseDate1, chineseDate2);
		Assertions.assertEquals(chineseDate2, chineseDate3);
		Assertions.assertNotEquals(chineseDate2, chineseDate4);
	}

	@Test
	public void getNormalizedDateTest(){
		final Date date = DateUtil.parse("2024-4-24");
		final ChineseDate chineseDate = new ChineseDate(Objects.requireNonNull(date));

		Assertions.assertEquals("农历甲辰年三月十六", chineseDate.toString(ChineseDateFormat.GSS));
		Assertions.assertEquals("农历龙年三月十六", chineseDate.toString(ChineseDateFormat.XSS));
		Assertions.assertEquals("农历甲辰年三月戊午日", chineseDate.toString(ChineseDateFormat.GSG));
		Assertions.assertEquals("公元2024年农历甲辰年三月十六", chineseDate.toString(ChineseDateFormat.MIX));

		final Date date2 = DateUtil.parse("2024-4-30");
		final ChineseDate chineseDate2 = new ChineseDate(Objects.requireNonNull(date2));
		Assertions.assertEquals("农历甲辰年三月廿二", chineseDate2.toString(ChineseDateFormat.GSS));
		Assertions.assertEquals("农历龙年三月廿二", chineseDate2.toString(ChineseDateFormat.XSS));
		Assertions.assertEquals("农历甲辰年三月甲子日", chineseDate2.toString(ChineseDateFormat.GSG));
		Assertions.assertEquals("公元2024年农历甲辰年三月廿二", chineseDate2.toString(ChineseDateFormat.MIX));
	}
}
