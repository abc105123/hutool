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

package org.dromara.hutool.poi.excel.sax.handler;

import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.collection.iter.IterUtil;
import org.dromara.hutool.core.collection.ListUtil;
import org.dromara.hutool.core.convert.ConvertUtil;
import org.dromara.hutool.core.lang.Assert;

import java.util.List;

/**
 * Bean形式的行处理器<br>
 * 将一行数据转换为Map，key为指定行，value为当前行对应位置的值
 *
 * @author Looly
 * @since 5.4.4
 * @param <T> 结果类型
 */
public abstract class BeanRowHandler<T> extends AbstractRowHandler<T> {

	/**
	 * 标题所在行（从0开始计数）
	 */
	private final int headerRowIndex;
	/**
	 * 标题行
	 */
	List<String> headerList;

	/**
	 * 构造
	 *
	 * @param headerRowIndex 标题所在行（从0开始计数）
	 * @param startRowIndex  读取起始行（包含，从0开始计数）
	 * @param endRowIndex    读取结束行（包含，从0开始计数）
	 * @param clazz          Bean类型
	 */
	public BeanRowHandler(final int headerRowIndex, final int startRowIndex, final int endRowIndex, final Class<T> clazz) {
		super(startRowIndex, endRowIndex);
		Assert.isTrue(headerRowIndex <= startRowIndex, "Header row must before the start row!");
		this.headerRowIndex = headerRowIndex;
		this.convertFunc = (rowList) -> BeanUtil.toBean(IterUtil.toMap(headerList, rowList), clazz);
	}

	@Override
	public void handle(final int sheetIndex, final long rowIndex, final List<Object> rowCells) {
		if (rowIndex == this.headerRowIndex) {
			this.headerList = ListUtil.view(ConvertUtil.toList(String.class, rowCells));
			return;
		}
		super.handle(sheetIndex, rowIndex, rowCells);
	}
}
