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

package org.dromara.hutool.log;

import org.dromara.hutool.core.lang.caller.CallerUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.log.level.Level;

/**
 * 静态日志类，用于在不引入日志对象的情况下打印日志
 *
 * @author Looly
 */
public class LogUtil {
	private static final String FQCN = LogUtil.class.getName();

	// ----------------------------------------------------------- Log method start
	// ------------------------ Trace

	/**
	 * Trace等级日志，小于debug<br>
	 * 由于动态获取Log，效率较低，建议在非频繁调用的情况下使用！！
	 *
	 * @param format    格式文本，{} 代表变量
	 * @param arguments 变量对应的参数
	 */
	public static void trace(final String format, final Object... arguments) {
		trace(LogFactory.getLog(CallerUtil.getCallerCaller()), format, arguments);
	}

	/**
	 * Trace等级日志，小于Debug
	 *
	 * @param log       日志对象
	 * @param format    格式文本，{} 代表变量
	 * @param arguments 变量对应的参数
	 */
	public static void trace(final Log log, final String format, final Object... arguments) {
		log.trace(FQCN, null, format, arguments);
	}

	// ------------------------ debug

	/**
	 * Debug等级日志，小于Info<br>
	 * 由于动态获取Log，效率较低，建议在非频繁调用的情况下使用！！
	 *
	 * @param format    格式文本，{} 代表变量
	 * @param arguments 变量对应的参数
	 */
	public static void debug(final String format, final Object... arguments) {
		debug(LogFactory.getLog(CallerUtil.getCallerCaller()), format, arguments);
	}

	/**
	 * Debug等级日志，小于Info
	 *
	 * @param log       日志对象
	 * @param format    格式文本，{} 代表变量
	 * @param arguments 变量对应的参数
	 */
	public static void debug(final Log log, final String format, final Object... arguments) {
		log.debug(FQCN, null, format, arguments);
	}

	// ------------------------ info

	/**
	 * Info等级日志，小于Warn<br>
	 * 由于动态获取Log，效率较低，建议在非频繁调用的情况下使用！！
	 *
	 * @param format    格式文本，{} 代表变量
	 * @param arguments 变量对应的参数
	 */
	public static void info(final String format, final Object... arguments) {
		info(LogFactory.getLog(CallerUtil.getCallerCaller()), format, arguments);
	}

	/**
	 * Info等级日志，小于Warn
	 *
	 * @param log       日志对象
	 * @param format    格式文本，{} 代表变量
	 * @param arguments 变量对应的参数
	 */
	public static void info(final Log log, final String format, final Object... arguments) {
		log.info(FQCN, null, format, arguments);
	}

	// ------------------------ warn

	/**
	 * Warn等级日志，小于Error<br>
	 * 由于动态获取Log，效率较低，建议在非频繁调用的情况下使用！！
	 *
	 * @param format    格式文本，{} 代表变量
	 * @param arguments 变量对应的参数
	 */
	public static void warn(final String format, final Object... arguments) {
		warn(LogFactory.getLog(CallerUtil.getCallerCaller()), format, arguments);
	}

	/**
	 * Warn等级日志，小于Error<br>
	 * 由于动态获取Log，效率较低，建议在非频繁调用的情况下使用！！
	 *
	 * @param e         需在日志中堆栈打印的异常
	 * @param format    格式文本，{} 代表变量
	 * @param arguments 变量对应的参数
	 */
	public static void warn(final Throwable e, final String format, final Object... arguments) {
		warn(LogFactory.getLog(CallerUtil.getCallerCaller()), e, StrUtil.format(format, arguments));
	}

	/**
	 * Warn等级日志，小于Error
	 *
	 * @param log       日志对象
	 * @param format    格式文本，{} 代表变量
	 * @param arguments 变量对应的参数
	 */
	public static void warn(final Log log, final String format, final Object... arguments) {
		warn(log, null, format, arguments);
	}

	/**
	 * Warn等级日志，小于Error
	 *
	 * @param log       日志对象
	 * @param e         需在日志中堆栈打印的异常
	 * @param format    格式文本，{} 代表变量
	 * @param arguments 变量对应的参数
	 */
	public static void warn(final Log log, final Throwable e, final String format, final Object... arguments) {
		log.warn(FQCN, e, format, arguments);
	}

	// ------------------------ error

	/**
	 * Error等级日志<br>
	 * 由于动态获取Log，效率较低，建议在非频繁调用的情况下使用！！
	 *
	 * @param e 需在日志中堆栈打印的异常
	 */
	public static void error(final Throwable e) {
		error(LogFactory.getLog(CallerUtil.getCallerCaller()), e);
	}

	/**
	 * Error等级日志<br>
	 * 由于动态获取Log，效率较低，建议在非频繁调用的情况下使用！！
	 *
	 * @param format    格式文本，{} 代表变量
	 * @param arguments 变量对应的参数
	 */
	public static void error(final String format, final Object... arguments) {
		error(LogFactory.getLog(CallerUtil.getCallerCaller()), format, arguments);
	}

	/**
	 * Error等级日志<br>
	 * 由于动态获取Log，效率较低，建议在非频繁调用的情况下使用！！
	 *
	 * @param e         需在日志中堆栈打印的异常
	 * @param format    格式文本，{} 代表变量
	 * @param arguments 变量对应的参数
	 */
	public static void error(final Throwable e, final String format, final Object... arguments) {
		error(LogFactory.getLog(CallerUtil.getCallerCaller()), e, format, arguments);
	}

	/**
	 * Error等级日志<br>
	 *
	 * @param log 日志对象
	 * @param e   需在日志中堆栈打印的异常
	 */
	public static void error(final Log log, final Throwable e) {
		error(log, e, e.getMessage());
	}

	/**
	 * Error等级日志<br>
	 *
	 * @param log       日志对象
	 * @param format    格式文本，{} 代表变量
	 * @param arguments 变量对应的参数
	 */
	public static void error(final Log log, final String format, final Object... arguments) {
		error(log, null, format, arguments);
	}

	/**
	 * Error等级日志<br>
	 *
	 * @param log       日志对象
	 * @param e         需在日志中堆栈打印的异常
	 * @param format    格式文本，{} 代表变量
	 * @param arguments 变量对应的参数
	 */
	public static void error(final Log log, final Throwable e, final String format, final Object... arguments) {
		log.error(FQCN, e, format, arguments);
	}

	// ------------------------ Log

	/**
	 * 打印日志<br>
	 *
	 * @param level     日志级别
	 * @param t         需在日志中堆栈打印的异常
	 * @param format    格式文本，{} 代表变量
	 * @param arguments 变量对应的参数
	 */
	public static void log(final Level level, final Throwable t, final String format, final Object... arguments) {
		LogFactory.getLog(CallerUtil.getCallerCaller()).log(FQCN, level, t, format, arguments);
	}

	// ----------------------------------------------------------- Log method end
}
