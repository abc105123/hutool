/*
 * Copyright (c) 2025 Hutool Team and hutool.cn
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

package org.dromara.hutool.poi.excel.shape;

import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.poi.excel.WorkbookUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ExcelPicUtilTest {
	@Test
	@Disabled
	void readPicTest() {
		final Workbook book = WorkbookUtil.createBook("d:/test/poi/a.xlsx");
		final List<Picture> picMap = ExcelPicUtil.getShapePics(
			WorkbookUtil.createBook("d:/test/poi/a.xlsx"), 0);
		Console.log(picMap);

//		final List<? extends PictureData> allPictures = book.getAllPictures();
//		for (PictureData shape : allPictures) {
//			Console.log(shape);
//		}

		IoUtil.closeQuietly(book);
	}
}
