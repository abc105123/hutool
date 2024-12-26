/*
 * Copyright (c) 2024 Hutool Team and hutool.cn
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

package org.dromara.hutool.core.data;

import org.dromara.hutool.core.date.DateTime;
import org.dromara.hutool.core.date.DateUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 身份证单元测试
 *
 * @author Looly
 *
 */
public class IdcardUtilTest {

	private static final String ID_18 = "321083197812162119";
	private static final String ID_15 = "150102880730303";

	@Test
	public void isValidCardTest() {
		final boolean valid = IdcardUtil.isValidCard(ID_18);
		Assertions.assertTrue(valid);

		final boolean valid15 = IdcardUtil.isValidCard(ID_15);
		Assertions.assertTrue(valid15);

		// 无效
		String idCard = "360198910283844";
		assertFalse(IdcardUtil.isValidCard(idCard));

		// 生日无效
		idCard = "201511221897205960";
		assertFalse(IdcardUtil.isValidCard(idCard));

		// 生日无效
		idCard = "815727834224151";
		assertFalse(IdcardUtil.isValidCard(idCard));
	}

	@Test
	public void convert15To18Test() {
		final String convert15To18 = IdcardUtil.convert15To18(ID_15);
		assertEquals("150102198807303035", convert15To18);

		final String convert15To18Second = IdcardUtil.convert15To18("330102200403064");
		assertEquals("33010219200403064X", convert15To18Second);
	}

	@Test
	public void convert18To15Test() {
		final String idcard15 = IdcardUtil.convert18To15("150102198807303035");
		assertEquals(ID_15, idcard15);
	}

	@Test
	public void getAgeTest() {
		final Date date = DateUtil.parse("2017-04-10");

		final int age = IdcardUtil.getAge(ID_18, date);
		assertEquals(38, age);

		final int age2 = IdcardUtil.getAge(ID_15, date);
		assertEquals(28, age2);
	}

	@Test
	public void issue3651Test() {
		DateTime date = DateUtil.parse("2014-07-11");
		int age = IdcardUtil.getAge("321083200807112111", date);
		assertEquals(5, age);

		date = DateUtil.parse("2014-07-31");
		age = IdcardUtil.getAge("321083200807312113", date);
		assertEquals(5, age);
	}

	@Test
	public void getBirthTest() {
		final String birth = IdcardUtil.getBirth(ID_18);
		assertEquals("19781216", birth);

		final String birth2 = IdcardUtil.getBirth(ID_15);
		assertEquals("19880730", birth2);
	}

	@Test
	public void getProvinceTest() {
		final String province = IdcardUtil.getProvince(ID_18);
		assertEquals("江苏", province);

		final String province2 = IdcardUtil.getProvince(ID_15);
		assertEquals("内蒙古", province2);
	}

	@Test
	public void getCityCodeTest() {
		final String code = IdcardUtil.getCityCode(ID_18);
		assertEquals("3210", code);
	}

	@Test
	public void getDistrictCodeTest() {
		final String code = IdcardUtil.getDistrictCode(ID_18);
		assertEquals("321083", code);
	}

	@Test
	public void getGenderTest() {
		final int gender = IdcardUtil.getGender(ID_18);
		assertEquals(1, gender);
	}

	@Test
	public void isValidCard18Test(){
		boolean isValidCard18 = IdcardUtil.isValidCard18("3301022011022000D6");
		assertFalse(isValidCard18);

		// 不忽略大小写情况下，X严格校验必须大写
		isValidCard18 = IdcardUtil.isValidCard18("33010219200403064x", false);
		assertFalse(isValidCard18);
		isValidCard18 = IdcardUtil.isValidCard18("33010219200403064X", false);
		assertTrue(isValidCard18);

		// 非严格校验下大小写皆可
		isValidCard18 = IdcardUtil.isValidCard18("33010219200403064x");
		assertTrue(isValidCard18);
		isValidCard18 = IdcardUtil.isValidCard18("33010219200403064X");
		assertTrue(isValidCard18);

		// 香港人在大陆身份证
		isValidCard18 = IdcardUtil.isValidCard18("81000019980902013X");
		assertTrue(isValidCard18);

		// 澳门人在大陆身份证
		isValidCard18 = IdcardUtil.isValidCard18("820000200009100032");
		assertTrue(isValidCard18);

		// 台湾人在大陆身份证
		isValidCard18 = IdcardUtil.isValidCard18("830000200209060065");
		assertTrue(isValidCard18);

		// 身份证允许调用为空null
		isValidCard18 = !IdcardUtil.isValidCard18(null);
		assertTrue(isValidCard18);
	}

	@Test
	public void isValidHKCardIdTest(){
		final String hkCard="P174468(6)";
		final boolean flag=IdcardUtil.isValidCard10(hkCard);
		assertTrue(flag);
	}

	@Test
	public void isValidTWCardIdTest() {
		final String twCard = "B221690311";
		boolean flag = IdcardUtil.isValidCard10(twCard);
		assertTrue(flag);
		final String errTwCard1 = "M517086311";
		flag = IdcardUtil.isValidCard10(errTwCard1);
		assertFalse(flag);
		final String errTwCard2 = "B2216903112";
		flag = IdcardUtil.isValidCard10(errTwCard2);
		assertFalse(flag);
	}

	@Test
	void foreignTest() {
		// 新版外国人永久居留身份证号码
		final String FOREIGN_ID_18 = "932682198501010017";
		assertTrue(IdcardUtil.isValidCard(FOREIGN_ID_18));

		final Date date = DateUtil.parse("2017-04-10");
		assertEquals(IdcardUtil.getAge(FOREIGN_ID_18, date), 32);

		// 新版外国人永久居留身份证
		assertTrue(IdcardUtil.isValidCard18("932682198501010017"));
	}

	@Test
	public void issueIAFOLITest() {
		final String idcard = "H01487002";
		assertFalse(IdcardUtil.isValidCard10(idcard));
		assertFalse(IdcardUtil.isValidCard(idcard));
	}
}
