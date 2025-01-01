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

package org.dromara.hutool.json.issues;

import lombok.Data;
import org.dromara.hutool.json.JSONUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 测试同一对象作为对象的字段是否会有null的问题，
 * 此问题原来出在BeanCopier中，判断循环引用使用了equals，并不严谨。
 * 修复后使用==判断循环引用。
 */
public class IssueI1H2VNTest {

	@Test
	public void toBeanTest() {
		final String jsonStr = "{'conditionsVo':[{'column':'StockNo','value':'abc','type':'='},{'column':'CheckIncoming','value':'1','type':'='}]," +
				"'queryVo':{'conditionsVo':[{'column':'StockNo','value':'abc','type':'='},{'column':'CheckIncoming','value':'1','type':'='}],'queryVo':null}}";
		final QueryVo vo = JSONUtil.toBean(jsonStr, QueryVo.class);
		Assertions.assertEquals(2, vo.getConditionsVo().size());
		final QueryVo subVo = vo.getQueryVo();
		Assertions.assertNotNull(subVo);
		Assertions.assertEquals(2, subVo.getConditionsVo().size());
		Assertions.assertNull(subVo.getQueryVo());
	}

	@Data
	public static class ConditionVo {
		private String column;
		private String value;
		private String type;
	}

	@Data
	public static class QueryVo {
		private List<ConditionVo> conditionsVo;
		private QueryVo queryVo;
	}
}
