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

package org.dromara.hutool.poi.word;

import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.collection.ListUtil;
import org.dromara.hutool.core.collection.iter.IterUtil;
import org.dromara.hutool.core.convert.ConvertUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.map.MapUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Word中表格相关工具
 *
 * @author Looly
 * @since 4.5.14
 */
public class TableUtil {

	/**
	 * 创建空表，只有一行
	 *
	 * @param doc {@link XWPFDocument}
	 * @return {@link XWPFTable}
	 */
	public static XWPFTable createTable(final XWPFDocument doc) {
		return createTable(doc, null);
	}

	/**
	 * 创建表格并填充数据，默认表格
	 *
	 * @param doc {@link XWPFDocument}
	 * @param data 数据
	 * @return {@link XWPFTable}
	 */
	public static XWPFTable createTable(final XWPFDocument doc, final Iterable<?> data) {
		Assert.notNull(doc, "XWPFDocument must be not null !");
		final XWPFTable table = doc.createTable();
		// 新建table的时候默认会新建一行，此处移除之
		table.removeRow(0);
		return writeTable(table, data);
	}

	/**
	 * 为table填充数据
	 *
	 * @param table {@link XWPFTable}
	 * @param data 数据
	 * @return {@link XWPFTable}
	 * @since 5.5.6
	 */
	public static XWPFTable writeTable(final XWPFTable table, final Iterable<?> data){
		Assert.notNull(table, "XWPFTable must be not null !");
		if (IterUtil.isEmpty(data)) {
			// 数据为空，返回空表
			return table;
		}

		boolean isFirst = true;
		for (final Object rowData : data) {
			writeRow(table.createRow(), rowData, isFirst);
			if(isFirst){
				isFirst = false;
			}
		}

		return table;
	}

	/**
	 * 写一行数据
	 *
	 * @param row 行
	 * @param rowBean 行数据
	 * @param isWriteKeyAsHead 如果为Map或者Bean，是否写标题
	 */
	@SuppressWarnings("rawtypes")
	public static void writeRow(final XWPFTableRow row, final Object rowBean, final boolean isWriteKeyAsHead) {
		if (rowBean instanceof Iterable) {
			writeRow(row, (Iterable<?>) rowBean);
			return;
		}

		final Map rowMap;
		if(rowBean instanceof Map) {
			rowMap = (Map) rowBean;
		} else if (BeanUtil.isWritableBean(rowBean.getClass())) {
			rowMap = BeanUtil.beanToMap(rowBean, new LinkedHashMap<>(), false, false);
		} else {
			// 其它转为字符串默认输出
			writeRow(row, ListUtil.of(rowBean), isWriteKeyAsHead);
			return;
		}

		writeRow(row, rowMap, isWriteKeyAsHead);
	}

	/**
	 * 写行数据
	 *
	 * @param row 行
	 * @param rowMap 行数据
	 * @param isWriteKeyAsHead 是否写标题
	 */
	public static void writeRow(XWPFTableRow row, final Map<?, ?> rowMap, final boolean isWriteKeyAsHead) {
		if (MapUtil.isEmpty(rowMap)) {
			return;
		}

		if (isWriteKeyAsHead) {
			writeRow(row, rowMap.keySet());
			row = row.getTable().createRow();
		}
		writeRow(row, rowMap.values());
	}

	/**
	 * 写行数据
	 *
	 * @param row 行
	 * @param rowData 行数据
	 */
	public static void writeRow(final XWPFTableRow row, final Iterable<?> rowData) {
		XWPFTableCell cell;
		int index = 0;
		for (final Object cellData : rowData) {
			cell = getOrCreateCell(row, index);
			cell.setText(ConvertUtil.toStr(cellData));
			index++;
		}
	}

	/**
	 * 获取或创建新行<br>
	 * 存在则直接返回，不存在创建新的行
	 *
	 * @param table {@link XWPFTable}
	 * @param index 索引（行号），从0开始
	 * @return {@link XWPFTableRow}
	 */
	public static XWPFTableRow getOrCreateRow(final XWPFTable table, final int index) {
		XWPFTableRow row = table.getRow(index);
		if (null == row) {
			row = table.createRow();
		}

		return row;
	}

	/**
	 * 获取或创建新单元格<br>
	 * 存在则直接返回，不存在创建新的单元格
	 *
	 * @param row {@link XWPFTableRow} 行
	 * @param index index 索引（列号），从0开始
	 * @return {@link XWPFTableCell}
	 */
	public static XWPFTableCell getOrCreateCell(final XWPFTableRow row, final int index) {
		XWPFTableCell cell = row.getCell(index);
		if (null == cell) {
			cell = row.createCell();
		}
		return cell;
	}
}
