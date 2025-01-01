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
import org.dromara.hutool.core.convert.ConvertUtil;
import org.dromara.hutool.json.JSONUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

/**
 * https://gitee.com/dromara/hutool/issues/I49VZB
 */
public class IssueI49VZBTest {
	public enum NBCloudKeyType {
		/**
		 * 指纹
		 */
		fingerPrint,
		/**
		 * 密码
		 */
		password,
		/**
		 * 卡片
		 */
		card,
		/**
		 * 临时密码
		 */
		snapKey;

		public static NBCloudKeyType find(final String value) {
			return Stream.of(values()).filter(e -> e.getValue().equalsIgnoreCase(value)).findFirst()
					.orElse(null);
		}


		public static NBCloudKeyType downFind(final String keyType) {
			if (fingerPrint.name().equals(keyType.toLowerCase())) {
				return NBCloudKeyType.fingerPrint;
			} else {
				return find(keyType);
			}
		}

		public String getValue() {
			return super.toString().toLowerCase();
		}

	}

	@Data
	@EqualsAndHashCode(callSuper = false)
	public static class UPOpendoor  {

		private String keyId;
		private NBCloudKeyType type;
		private String time;
		private int result;

	}

	@Test
	public void toBeanTest(){
		final String str = "{type: \"password\"}";
		final UPOpendoor upOpendoor = JSONUtil.toBean(str, UPOpendoor.class);
		Assertions.assertEquals(NBCloudKeyType.password, upOpendoor.getType());
	}

	@Test
	public void enumConvertTest(){
		final NBCloudKeyType type = ConvertUtil.toEnum(NBCloudKeyType.class, "snapKey");
		Assertions.assertEquals(NBCloudKeyType.snapKey, type);
	}
}
