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

package org.dromara.hutool.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BooleanUtilTest {

	@Test
	public void toBooleanTest() {
		Assertions.assertTrue(BooleanUtil.toBoolean("true"));
		Assertions.assertTrue(BooleanUtil.toBoolean("yes"));
		Assertions.assertTrue(BooleanUtil.toBoolean("t"));
		Assertions.assertTrue(BooleanUtil.toBoolean("OK"));
		Assertions.assertTrue(BooleanUtil.toBoolean("1"));
		Assertions.assertTrue(BooleanUtil.toBoolean("On"));
		Assertions.assertTrue(BooleanUtil.toBoolean("是"));
		Assertions.assertTrue(BooleanUtil.toBoolean("对"));
		Assertions.assertTrue(BooleanUtil.toBoolean("真"));

		Assertions.assertFalse(BooleanUtil.toBoolean("false"));
		Assertions.assertFalse(BooleanUtil.toBoolean("6455434"));
		Assertions.assertFalse(BooleanUtil.toBoolean(""));
	}

	@Test
	public void andTest() {
		Assertions.assertFalse(BooleanUtil.and(true, false));
		Assertions.assertFalse(BooleanUtil.andOfWrap(true, false));
	}

	@Test
	public void orTest() {
		Assertions.assertTrue(BooleanUtil.or(true, false));
		Assertions.assertTrue(BooleanUtil.orOfWrap(true, false));
	}

	@Test
	public void xorTest() {
		Assertions.assertTrue(BooleanUtil.xor(true, false));
		Assertions.assertTrue(BooleanUtil.xor(true, true, true));
		Assertions.assertFalse(BooleanUtil.xor(true, true, false));
		Assertions.assertTrue(BooleanUtil.xor(true, false, false));
		Assertions.assertFalse(BooleanUtil.xor(false, false, false));

		Assertions.assertTrue(BooleanUtil.xorOfWrap(true, false));
		Assertions.assertTrue(BooleanUtil.xorOfWrap(true, true, true));
		Assertions.assertFalse(BooleanUtil.xorOfWrap(true, true, false));
		Assertions.assertTrue(BooleanUtil.xorOfWrap(true, false, false));
		Assertions.assertFalse(BooleanUtil.xorOfWrap(false, false, false));
	}

	@SuppressWarnings("ConstantConditions")
	@Test
	public void isTrueIsFalseTest() {
		Assertions.assertFalse(BooleanUtil.isTrue(null));
		Assertions.assertFalse(BooleanUtil.isFalse(null));
	}

	@Test
	public void orOfWrapTest() {
		Assertions.assertFalse(BooleanUtil.orOfWrap(Boolean.FALSE, null));
		Assertions.assertTrue(BooleanUtil.orOfWrap(Boolean.TRUE, null));
	}

	@SuppressWarnings("ConstantConditions")
	@Test
	public void negateTest() {
		Assertions.assertFalse(BooleanUtil.negate(Boolean.TRUE));
		Assertions.assertTrue(BooleanUtil.negate(Boolean.FALSE));

		Assertions.assertFalse(BooleanUtil.negate(Boolean.TRUE.booleanValue()));
		Assertions.assertTrue(BooleanUtil.negate(Boolean.FALSE.booleanValue()));
	}

	@Test
	public void toStringTest() {
		Assertions.assertEquals("true", BooleanUtil.toStringTrueFalse(true));
		Assertions.assertEquals("false", BooleanUtil.toStringTrueFalse(false));

		Assertions.assertEquals("yes", BooleanUtil.toStringYesNo(true));
		Assertions.assertEquals("no", BooleanUtil.toStringYesNo(false));

		Assertions.assertEquals("on", BooleanUtil.toStringOnOff(true));
		Assertions.assertEquals("off", BooleanUtil.toStringOnOff(false));
	}

	@SuppressWarnings("ConstantConditions")
	@Test
	public void toBooleanObjectTest(){
		Assertions.assertTrue(BooleanUtil.toBooleanObject("yes"));
		Assertions.assertTrue(BooleanUtil.toBooleanObject("真"));
		Assertions.assertTrue(BooleanUtil.toBooleanObject("是"));
		Assertions.assertTrue(BooleanUtil.toBooleanObject("√"));

		Assertions.assertNull(BooleanUtil.toBooleanObject(null));
		Assertions.assertNull(BooleanUtil.toBooleanObject("不识别"));
	}

	@Test
	public void issue3587Test() {
		final Boolean boolean1 = true;
		final Boolean boolean2 = null;
		final Boolean result = BooleanUtil.andOfWrap(boolean1, boolean2);
		Assertions.assertFalse(result);
	}
}
