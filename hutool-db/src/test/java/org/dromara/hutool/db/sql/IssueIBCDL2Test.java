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

package org.dromara.hutool.db.sql;

import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.db.Db;
import org.dromara.hutool.db.Entity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

public class IssueIBCDL2Test {

	@Test
	@Disabled
	void regexpTest(){
		final List<Entity> all = Db.of("mariadb_local").findAll(
			Entity.of("user").addCondition(
				new Condition("name", "REGEXP", "张.*")));
		Console.log(all);
	}
}
