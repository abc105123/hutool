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

package org.dromara.hutool.poi.excel.style;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.util.ObjUtil;

/**
 * Excel样式工具类
 *
 * @author Looly
 * @since 4.0.0
 */
public class StyleUtil {

	// region ----- create or clone style

	/**
	 * 创建单元格样式
	 *
	 * @param workbook {@link Workbook} 工作簿
	 * @return {@link CellStyle}
	 * @see Workbook#createCellStyle()
	 * @since 5.4.0
	 */
	public static CellStyle createCellStyle(final Workbook workbook) {
		if (null == workbook) {
			return null;
		}
		return workbook.createCellStyle();
	}

	/**
	 * 创建默认普通单元格样式
	 *
	 * <pre>
	 * 1. 文字上下左右居中
	 * 2. 细边框，黑色
	 * </pre>
	 *
	 * @param workbook {@link Workbook} 工作簿
	 * @return {@link CellStyle}
	 */
	public static CellStyle createDefaultCellStyle(final Workbook workbook) {
		final CellStyle cellStyle = createCellStyle(workbook);
		setAlign(cellStyle, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
		setBorder(cellStyle, BorderStyle.THIN, IndexedColors.BLACK);
		return cellStyle;
	}

	/**
	 * 创建默认头部样式
	 *
	 * @param workbook {@link Workbook} 工作簿
	 * @return {@link CellStyle}
	 */
	public static CellStyle createHeadCellStyle(final Workbook workbook) {
		final CellStyle cellStyle = createCellStyle(workbook);
		setAlign(cellStyle, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
		setBorder(cellStyle, BorderStyle.THIN, IndexedColors.BLACK);
		setColor(cellStyle, IndexedColors.GREY_25_PERCENT, FillPatternType.SOLID_FOREGROUND);
		return cellStyle;
	}

	/**
	 * 给定样式是否为null（无样式）或默认样式，默认样式为{@code workbook.getCellStyleAt(0)}
	 *
	 * @param workbook 工作簿
	 * @param style    被检查的样式
	 * @return 是否为null（无样式）或默认样式
	 * @since 4.6.3
	 */
	public static boolean isNullOrDefaultStyle(final Workbook workbook, final CellStyle style) {
		return (null == style) || style.equals(workbook.getCellStyleAt(0));
	}

	/**
	 * 克隆新的{@link CellStyle}
	 *
	 * @param cell      单元格
	 * @param cellStyle 被复制的样式
	 * @return {@link CellStyle}
	 */
	public static CellStyle cloneCellStyle(final Cell cell, final CellStyle cellStyle) {
		return cloneCellStyle(cell.getSheet().getWorkbook(), cellStyle);
	}

	/**
	 * 克隆新的{@link CellStyle}
	 *
	 * @param workbook  工作簿
	 * @param cellStyle 被复制的样式
	 * @return {@link CellStyle}
	 */
	public static CellStyle cloneCellStyle(final Workbook workbook, final CellStyle cellStyle) {
		final CellStyle newCellStyle = createCellStyle(workbook);
		newCellStyle.cloneStyleFrom(cellStyle);
		return newCellStyle;
	}

	// endregion

	/**
	 * 设置cell文本对齐样式
	 *
	 * @param cellStyle {@link CellStyle}
	 * @param halign    横向位置
	 * @param valign    纵向位置
	 * @return {@link CellStyle}
	 */
	public static CellStyle setAlign(final CellStyle cellStyle, final HorizontalAlignment halign, final VerticalAlignment valign) {
		cellStyle.setAlignment(halign);
		cellStyle.setVerticalAlignment(valign);
		return cellStyle;
	}

	/**
	 * 设置cell的四个边框粗细和颜色
	 *
	 * @param cellStyle  {@link CellStyle}
	 * @param borderSize 边框粗细{@link BorderStyle}枚举
	 * @param colorIndex 预定义颜色的short值，见{@link IndexedColors}枚举
	 * @return {@link CellStyle}
	 */
	public static CellStyle setBorder(final CellStyle cellStyle, final BorderStyle borderSize, final IndexedColors colorIndex) {
		return setBorder(cellStyle, CellBorderStyle.of(borderSize, colorIndex));
	}

	/**
	 * 设置cell的四个边框粗细和颜色
	 *
	 * @param cellStyle       {@link CellStyle}
	 * @param cellBorderStyle {@link CellBorderStyle}单元格边框样式和颜色
	 *                        }
	 * @return {@link CellStyle}
	 * @since 6.0.0
	 */
	public static CellStyle setBorder(final CellStyle cellStyle, final CellBorderStyle cellBorderStyle) {
		return cellBorderStyle.setTo(cellStyle);
	}

	/**
	 * 根据{@link CellStyle}设置指定范围边框样式
	 *
	 * @param sheet            {@link Sheet}
	 * @param cellRangeAddress 边框样式范围
	 * @param cellBorderStyle  边框风格，包括边框样式、颜色
	 */
	public static void setBorderStyle(final Sheet sheet, final CellRangeAddress cellRangeAddress, final CellBorderStyle cellBorderStyle) {
		if (null != cellBorderStyle) {
			RegionUtil.setBorderTop(cellBorderStyle.getTopStyle(), cellRangeAddress, sheet);
			RegionUtil.setBorderRight(cellBorderStyle.getRightStyle(), cellRangeAddress, sheet);
			RegionUtil.setBorderBottom(cellBorderStyle.getBottomStyle(), cellRangeAddress, sheet);
			RegionUtil.setBorderLeft(cellBorderStyle.getLeftStyle(), cellRangeAddress, sheet);

			RegionUtil.setTopBorderColor(cellBorderStyle.getTopColor(), cellRangeAddress, sheet);
			RegionUtil.setRightBorderColor(cellBorderStyle.getRightColor(), cellRangeAddress, sheet);
			RegionUtil.setLeftBorderColor(cellBorderStyle.getLeftColor(), cellRangeAddress, sheet);
			RegionUtil.setBottomBorderColor(cellBorderStyle.getBottomColor(), cellRangeAddress, sheet);
		}
	}

	/**
	 * 根据{@link CellStyle}设置指定范围边框样式
	 *
	 * @param sheet            {@link Sheet}
	 * @param cellRangeAddress {@link CellRangeAddress}
	 * @param cellStyle        {@link CellStyle}
	 */
	public static void setBorderStyle(final Sheet sheet, final CellRangeAddress cellRangeAddress, final CellStyle cellStyle) {
		if (null != cellStyle) {
			final CellBorderStyle cellBorderStyle = CellBorderStyle.of(cellStyle);
			setBorderStyle(sheet, cellRangeAddress, cellBorderStyle);
		}
	}

	// region ----- color

	/**
	 * 给cell设置颜色
	 *
	 * @param cellStyle   {@link CellStyle}
	 * @param color       预定义的背景颜色，见{@link IndexedColors}枚举
	 * @param fillPattern 填充方式 {@link FillPatternType}枚举
	 * @return {@link CellStyle}
	 */
	public static CellStyle setColor(final CellStyle cellStyle, final IndexedColors color, final FillPatternType fillPattern) {
		return setColor(cellStyle, color.index, fillPattern);
	}

	/**
	 * 给cell设置颜色（即单元格背景色）
	 *
	 * @param cellStyle   {@link CellStyle}
	 * @param color       预定义的背景颜色，见{@link IndexedColors}枚举
	 * @param fillPattern 填充方式 {@link FillPatternType}枚举
	 * @return {@link CellStyle}
	 */
	public static CellStyle setColor(final CellStyle cellStyle, final short color, final FillPatternType fillPattern) {
		cellStyle.setFillForegroundColor(color);
		cellStyle.setFillPattern(ObjUtil.defaultIfNull(fillPattern, FillPatternType.SOLID_FOREGROUND));
		return cellStyle;
	}

	/**
	 * 给cell设置颜色（即单元格背景色）
	 *
	 * @param cellStyle   {@link CellStyle}
	 * @param color       背景颜色
	 * @param fillPattern 填充方式 {@link FillPatternType}枚举
	 * @return {@link CellStyle}
	 */
	public static CellStyle setColor(final XSSFCellStyle cellStyle, final XSSFColor color, final FillPatternType fillPattern) {
		cellStyle.setFillForegroundColor(color);
		cellStyle.setFillPattern(ObjUtil.defaultIfNull(fillPattern, FillPatternType.SOLID_FOREGROUND));
		return cellStyle;
	}

	// endregion

	// region ----- font

	/**
	 * 创建字体
	 *
	 * @param workbook {@link Workbook}
	 * @param color    字体颜色
	 * @param fontSize 字体大小
	 * @param fontName 字体名称，可以为null使用默认字体
	 * @return {@link Font}
	 */
	public static Font createFont(final Workbook workbook, final short color, final short fontSize, final String fontName) {
		final Font font = workbook.createFont();
		return setFontStyle(font, color, fontSize, fontName);
	}

	/**
	 * 设置字体样式
	 *
	 * @param font     字体{@link Font}
	 * @param color    字体颜色
	 * @param fontSize 字体大小
	 * @param fontName 字体名称，可以为null使用默认字体
	 * @return {@link Font}
	 */
	public static Font setFontStyle(final Font font, final short color, final short fontSize, final String fontName) {
		if (color > 0) {
			font.setColor(color);
		}
		if (fontSize > 0) {
			font.setFontHeightInPoints(fontSize);
		}
		if (StrUtil.isNotBlank(fontName)) {
			font.setFontName(fontName);
		}
		return font;
	}

	// endregion

	/**
	 * 创建数据格式并获取格式
	 *
	 * @param workbook {@link Workbook}
	 * @param format   数据格式
	 * @return 数据格式
	 * @since 5.5.5
	 */
	public static Short getFormat(final Workbook workbook, final String format) {
		final DataFormat dataFormat = workbook.createDataFormat();
		return dataFormat.getFormat(format);
	}
}
