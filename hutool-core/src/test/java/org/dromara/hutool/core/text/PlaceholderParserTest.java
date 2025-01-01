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

import org.dromara.hutool.core.text.placeholder.PlaceholderParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * test for {@link PlaceholderParser}
 *
 * @author huangchengxing
 */
public class PlaceholderParserTest {

	@Test
	public void testParse() {
		String text = "i {a}{m} a {jvav} programmer";
		PlaceholderParser parser = new PlaceholderParser(str -> str, "{", "}");
		Assertions.assertEquals(
			"i am a jvav programmer",
			parser.apply(text)
		);

		text = "i [a][m] a [jvav] programmer";
		parser = new PlaceholderParser(str -> str, "[", "]");
		Assertions.assertEquals(
			"i am a jvav programmer",
			parser.apply(text)
		);

		text = "i \\[a][[m\\]] a [jvav] programmer";
		parser = new PlaceholderParser(str -> str, "[", "]");
		Assertions.assertEquals(
			"i [a][m] a jvav programmer",
			parser.apply(text)
		);

		text = "i /[a][[m/]] a [jvav] programmer";
		parser = new PlaceholderParser(str -> str, "[", "]", '/');
		Assertions.assertEquals(
			"i [a][m] a jvav programmer",
			parser.apply(text)
		);

		text = "select * from #[tableName] where id = #[id]";
		parser = new PlaceholderParser(str -> "?", "#[", "]");
		Assertions.assertEquals(
				"select * from ? where id = ?",
				parser.apply(text)
		);
	}

}
