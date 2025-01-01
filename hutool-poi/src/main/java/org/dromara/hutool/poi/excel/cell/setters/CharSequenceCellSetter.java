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

package org.dromara.hutool.poi.excel.cell.setters;

import org.apache.poi.ss.usermodel.Cell;

/**
 * {@link CharSequence} 值单元格设置器
 *
 * @author Looly
 * @since 5.7.8
 */
public class CharSequenceCellSetter implements CellSetter {

	private final CharSequence value;

	/**
	 * 构造
	 *
	 * @param value 值
	 */
	CharSequenceCellSetter(final CharSequence value) {
		this.value = value;
	}

	@Override
	public void setValue(final Cell cell) {
		cell.setCellValue(value.toString());
	}
}
