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

package org.dromara.hutool.core.lang;

import org.dromara.hutool.core.convert.ConvertUtil;
import org.dromara.hutool.core.text.CharUtil;
import org.dromara.hutool.core.text.StrUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 控制台打印表格工具
 *
 * @author 孙宇
 * @since 5.4.4
 */
public class ConsoleTable {

	private static final char ROW_LINE = '－';
	private static final char COLUMN_LINE = '|';
	private static final char CORNER = '+';
	private static final char SPACE = '\u3000';
	private static final char LF = CharUtil.LF;

	private boolean isSBCMode = true;

	/**
	 * 创建ConsoleTable对象
	 *
	 * @return ConsoleTable
	 * @since 5.4.5
	 */
	public static ConsoleTable of() {
		return new ConsoleTable();
	}

	/**
	 * 表格头信息
	 */
	private final List<List<String>> headerList = new ArrayList<>();
	/**
	 * 表格体信息
	 */
	private final List<List<String>> bodyList = new ArrayList<>();
	/**
	 * 每列最大字符个数
	 */
	private List<Integer> columnCharNumber;

	/**
	 * 设置是否使用全角模式<br>
	 * 当包含中文字符时，输出的表格可能无法对齐，因此当设置为全角模式时，全部字符转为全角。
	 *
	 * @param isSBCMode 是否全角模式
	 * @return this
	 * @since 5.8.0
	 */
	public ConsoleTable setSBCMode(final boolean isSBCMode) {
		this.isSBCMode = isSBCMode;
		return this;
	}

	/**
	 * 添加头信息
	 *
	 * @param titles 列名
	 * @return 自身对象
	 */
	public ConsoleTable addHeader(final String... titles) {
		if (columnCharNumber == null) {
			columnCharNumber = new ArrayList<>(Collections.nCopies(titles.length, 0));
		}
		final List<String> l = new ArrayList<>();
		fillColumns(l, titles);
		headerList.add(l);
		return this;
	}

	/**
	 * 添加体信息
	 *
	 * @param values 列值
	 * @return 自身对象
	 */
	public ConsoleTable addBody(final String... values) {
		final List<String> l = new ArrayList<>();
		bodyList.add(l);
		fillColumns(l, values);
		return this;
	}

	/**
	 * 填充表格头或者体
	 *
	 * @param l       被填充列表
	 * @param columns 填充内容
	 */
	private void fillColumns(final List<String> l, final String[] columns) {
		String column;
		for (int i = 0; i < columns.length; i++) {
			column = StrUtil.toString(columns[i]);
			if (isSBCMode) {
				column = ConvertUtil.toSBC(column);
			}
			l.add(column);
			final int width = column.length();
			if (width > columnCharNumber.get(i)) {
				columnCharNumber.set(i, width);
			}
		}
	}

	/**
	 * 获取表格字符串
	 *
	 * @return 表格字符串
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		fillBorder(sb);
		fillRows(sb, headerList);
		fillBorder(sb);
		fillRows(sb, bodyList);
		fillBorder(sb);
		return sb.toString();
	}

	/**
	 * 填充表头或者表体信息（多行）
	 *
	 * @param sb   内容
	 * @param list 表头列表或者表体列表
	 */
	private void fillRows(final StringBuilder sb, final List<List<String>> list) {
		for (final List<String> row : list) {
			sb.append(COLUMN_LINE);
			fillRow(sb, row);
			sb.append(LF);
		}
	}

	/**
	 * 填充一行数据
	 *
	 * @param sb  内容
	 * @param row 一行数据
	 */
	private void fillRow(final StringBuilder sb, final List<String> row) {
		final int size = row.size();
		String value;
		for (int i = 0; i < size; i++) {
			value = row.get(i);
			sb.append(SPACE);
			sb.append(value);
			final int length = value.length();
			final int sbcCount = sbcCount(value);
			if(sbcCount % 2 == 1){
				sb.append(CharUtil.SPACE);
			}
			sb.append(SPACE);
			final int maxLength = columnCharNumber.get(i);
			for (int j = 0; j < (maxLength - length + (sbcCount / 2)); j++) {
				sb.append(SPACE);
			}
			sb.append(COLUMN_LINE);
		}
	}

	/**
	 * 拼装边框
	 *
	 * @param sb StringBuilder
	 */
	private void fillBorder(final StringBuilder sb) {
		sb.append(CORNER);
		for (final Integer width : columnCharNumber) {
			sb.append(StrUtil.repeat(ROW_LINE, width + 2));
			sb.append(CORNER);
		}
		sb.append(LF);
	}

	/**
	 * 打印到控制台
	 */
	public void print() {
		Console.print(toString());
	}

	/**
	 * 半角字符数量
	 *
	 * @param value 字符串
	 * @return 填充空格数量
	 */
	private int sbcCount(final String value) {
		int count = 0;
		for (int i = 0; i < value.length(); i++) {
			if (value.charAt(i) < '\177') {
				count++;
			}
		}

		return count;
	}
}
