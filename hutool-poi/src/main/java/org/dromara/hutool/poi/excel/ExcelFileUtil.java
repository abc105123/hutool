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

package org.dromara.hutool.poi.excel;

import org.dromara.hutool.core.io.IORuntimeException;
import org.apache.poi.poifs.filesystem.FileMagic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Excel文件工具类
 *
 * @author Looly
 * @since 4.2.1
 */
public class ExcelFileUtil {
	// ------------------------------------------------------------------------------------------------ isXls

	/**
	 * 是否为XLS格式的Excel文件（HSSF）<br>
	 * XLS文件主要用于Excel 97~2003创建<br>
	 * 此方法会自动调用{@link InputStream#reset()}方法
	 *
	 * @param in excel输入流
	 * @return 是否为XLS格式的Excel文件（HSSF）
	 */
	public static boolean isXls(final InputStream in) {
		return FileMagic.OLE2 == getFileMagic(in);
	}

	/**
	 * 是否为XLSX格式的Excel文件（XSSF）<br>
	 * XLSX文件主要用于Excel 2007+创建<br>
	 * 此方法会自动调用{@link InputStream#reset()}方法
	 *
	 * @param in excel输入流
	 * @return 是否为XLSX格式的Excel文件（XSSF）
	 */
	public static boolean isXlsx(final InputStream in) {
		return FileMagic.OOXML == getFileMagic(in);
	}

	/**
	 * 是否为XLSX格式的Excel文件（XSSF）<br>
	 * XLSX文件主要用于Excel 2007+创建
	 *
	 * @param file excel文件
	 * @return 是否为XLSX格式的Excel文件（XSSF）
	 * @since 5.4.4
	 */
	public static boolean isXlsx(final File file) {
		try {
			return FileMagic.valueOf(file) == FileMagic.OOXML;
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * {@link java.io.PushbackInputStream}
	 * PushbackInputStream的markSupported()为false，并不支持mark和reset
	 * 如果强转成PushbackInputStream在调用FileMagic.valueOf(inputStream)时会报错
	 * {@link FileMagic}
	 * 报错内容：getFileMagic() only operates on streams which support mark(int)
	 * 此处修改成 final InputStream in = FileMagic.prepareToCheckMagic(in)
	 *
	 * @param in {@link InputStream}
	 * @author kefan.qu
	 */
	private static FileMagic getFileMagic(InputStream in) {
		final FileMagic magic;
		in = FileMagic.prepareToCheckMagic(in);
		try {
			magic = FileMagic.valueOf(in);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}

		return magic;
	}
}
