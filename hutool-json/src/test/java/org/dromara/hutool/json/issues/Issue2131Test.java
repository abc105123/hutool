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
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.dromara.hutool.core.collection.ListUtil;
import org.dromara.hutool.json.JSONObject;
import org.dromara.hutool.json.JSONUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.beans.Transient;
import java.util.List;
import java.util.stream.Stream;

/**
 * https://github.com/dromara/hutool/issues/2131<br>
 * 字段定义成final，意味着setCollections无效，因此JSON转Bean的时候无法调用setCollections注入，所以是空的。
 */
public class Issue2131Test {

	@Test
	public void strToBean() {
		final GoodsResponse goodsResponse = new GoodsResponse();
		final GoodsItem apple = new GoodsItem().setGoodsId(1L).setGoodsName("apple").setChannel("wechat");
		final GoodsItem pear = new GoodsItem().setGoodsId(2L).setGoodsName("pear").setChannel("jd");
		final List<GoodsItem> collections = goodsResponse.getCollections();
		Stream.of(apple, pear).forEach(collections::add);

		final String jsonStr = JSONUtil.toJsonStr(goodsResponse);
		final JSONObject jsonObject = JSONUtil.parseObj(jsonStr);

		final GoodsResponse result = jsonObject.toBean(GoodsResponse.class);
		Assertions.assertEquals(0, result.getCollections().size());
	}

	@Data
	static class BaseResponse {

		@SuppressWarnings("unused")
		@Transient
		public final boolean successful() {
			return code == 200 || code == 201;
		}

		private int code = 200;
		private String message;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	static class GoodsResponse extends BaseResponse {
		// 由于定义成了final形式，setXXX无效，导致无法注入。
		private final List<GoodsItem> collections = ListUtil.of(false);
	}

	@Data
	@Accessors(chain = true)
	static class GoodsItem{
		private long goodsId;
		private String goodsName;
		private String channel;
	}
}
