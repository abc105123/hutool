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

package org.dromara.hutool.core.text;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * UnicodeUtil 单元测试
 *
 * @author Looly
 *
 */
public class UnicodeUtilTest {
	@Test
	public void convertTest() {
		final String s = UnicodeUtil.toUnicode("aaa123中文", true);
		Assertions.assertEquals("aaa123\\u4e2d\\u6587", s);

		final String s1 = UnicodeUtil.toString(s);
		Assertions.assertEquals("aaa123中文", s1);
	}

	@Test
	public void convertTest2() {
		final String str = "aaaa\\u0026bbbb\\u0026cccc";
		final String unicode = UnicodeUtil.toString(str);
		Assertions.assertEquals("aaaa&bbbb&cccc", unicode);
	}

	@Test
	public void convertTest3() {
		final String str = "aaa\\u111";
		final String res = UnicodeUtil.toString(str);
		Assertions.assertEquals("aaa\\u111", res);
	}

	@Test
	public void convertTest4() {
		final String str = "aaa\\U4e2d\\u6587\\u111\\urtyu\\u0026";
		final String res = UnicodeUtil.toString(str);
		Assertions.assertEquals("aaa中文\\u111\\urtyu&", res);
	}

	@Test
	public void convertTest5() {
		final String str = "{\"code\":403,\"enmsg\":\"Product not found\",\"cnmsg\":\"\\u4ea7\\u54c1\\u4e0d\\u5b58\\u5728\\uff0c\\u6216\\u5df2\\u5220\\u9664\",\"data\":null}";
		final String res = UnicodeUtil.toString(str);
		Assertions.assertEquals("{\"code\":403,\"enmsg\":\"Product not found\",\"cnmsg\":\"产品不存在，或已删除\",\"data\":null}", res);
	}

	@Test
	public void issueI50MI6Test(){
		final String s = UnicodeUtil.toUnicode("烟", true);
		Assertions.assertEquals("\\u70df", s);
	}
}
