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

package org.dromara.hutool.poi.excel.cell.values;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.poi.excel.cell.editors.CellEditor;
import org.dromara.hutool.poi.excel.cell.CellUtil;
import org.dromara.hutool.poi.excel.cell.NullCell;

/**
 * 复合单元格值，用于根据单元格类型读取不同的值
 *
 * @author Looly
 * @since 6.0.0
 */
public class CompositeCellValue implements CellValue<Object> {

	/**
	 * 创建CompositeCellValue
	 *
	 * @param cell       {@link Cell}单元格
	 * @param cellType   单元格值类型{@link CellType}枚举，如果为{@code null}默认使用cell的类型
	 * @param cellEditor 单元格值编辑器。可以通过此编辑器对单元格值做自定义操作
	 * @return CompositeCellValue
	 */
	public static CompositeCellValue of(final Cell cell, final CellType cellType, final CellEditor cellEditor){
		return new CompositeCellValue(cell, cellType, cellEditor);
	}

	private final Cell cell;
	private final CellType cellType;
	private final CellEditor cellEditor;

	/**
	 * 构造
	 *
	 * @param cell       {@link Cell}单元格
	 * @param cellType   单元格值类型{@link CellType}枚举，如果为{@code null}默认使用cell的类型
	 * @param cellEditor 单元格值编辑器。可以通过此编辑器对单元格值做自定义操作
	 */
	public CompositeCellValue(final Cell cell, final CellType cellType, final CellEditor cellEditor) {
		this.cell = cell;
		this.cellType = cellType;
		this.cellEditor = cellEditor;
	}

	@Override
	public Object getValue() {
		Cell cell = this.cell;
		CellType cellType = this.cellType;
		final CellEditor cellEditor = this.cellEditor;

		if (null == cell) {
			return null;
		}
		if (cell instanceof NullCell) {
			return null == cellEditor ? null : cellEditor.edit(cell, null);
		}
		if (null == cellType) {
			cellType = cell.getCellType();
		}

		// 尝试获取合并单元格，如果是合并单元格，则重新获取单元格类型
		final Cell mergedCell = CellUtil.getFirstCellOfMerged(cell);
		if (mergedCell != cell) {
			cell = mergedCell;
			cellType = cell.getCellType();
		}

		final Object value;
		switch (cellType) {
			case NUMERIC:
				value = new NumericCellValue(cell).getValue();
				break;
			case BOOLEAN:
				value = cell.getBooleanCellValue();
				break;
			case FORMULA:
				value = of(cell, cell.getCachedFormulaResultType(), cellEditor).getValue();
				break;
			case BLANK:
				value = StrUtil.EMPTY;
				break;
			case ERROR:
				value = new ErrorCellValue(cell).getValue();
				break;
			default:
				value = cell.getStringCellValue();
		}

		return null == cellEditor ? value : cellEditor.edit(cell, value);
	}
}
