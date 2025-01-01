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

package org.dromara.hutool.log.engine.console;

import org.dromara.hutool.core.date.DateUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.core.map.Dict;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.log.AbstractLog;
import org.dromara.hutool.log.level.Level;

/**
 * 利用System.out.println()打印日志
 *
 * @author Looly
 */
public class ConsoleLog extends AbstractLog {
	private static final long serialVersionUID = -6843151523380063975L;

	private static final String logFormat = "[{date}] [{level}] {name}: {msg}";
	private static Level currentLevel = Level.DEBUG;

	private final String name;

	//------------------------------------------------------------------------- Constructor

	/**
	 * 构造
	 *
	 * @param clazz 类
	 */
	public ConsoleLog(final Class<?> clazz) {
		this.name = (null == clazz) ? StrUtil.NULL : clazz.getName();
	}

	/**
	 * 构造
	 *
	 * @param name 类名
	 */
	public ConsoleLog(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * 设置自定义的日志显示级别
	 *
	 * @param customLevel 自定义级别
	 * @since 4.1.10
	 */
	public static void setLevel(final Level customLevel) {
		Assert.notNull(customLevel);
		currentLevel = customLevel;
	}

	//------------------------------------------------------------------------- Trace
	@Override
	public boolean isTraceEnabled() {
		return isEnabled(Level.TRACE);
	}

	@Override
	public void trace(final String fqcn, final Throwable t, final String format, final Object... arguments) {
		log(fqcn, Level.TRACE, t, format, arguments);
	}

	//------------------------------------------------------------------------- Debug
	@Override
	public boolean isDebugEnabled() {
		return isEnabled(Level.DEBUG);
	}

	@Override
	public void debug(final String fqcn, final Throwable t, final String format, final Object... arguments) {
		log(fqcn, Level.DEBUG, t, format, arguments);
	}

	//------------------------------------------------------------------------- Info
	@Override
	public boolean isInfoEnabled() {
		return isEnabled(Level.INFO);
	}

	@Override
	public void info(final String fqcn, final Throwable t, final String format, final Object... arguments) {
		log(fqcn, Level.INFO, t, format, arguments);
	}

	//------------------------------------------------------------------------- Warn
	@Override
	public boolean isWarnEnabled() {
		return isEnabled(Level.WARN);
	}

	@Override
	public void warn(final String fqcn, final Throwable t, final String format, final Object... arguments) {
		log(fqcn, Level.WARN, t, format, arguments);
	}

	//------------------------------------------------------------------------- Error
	@Override
	public boolean isErrorEnabled() {
		return isEnabled(Level.ERROR);
	}

	@Override
	public void error(final String fqcn, final Throwable t, final String format, final Object... arguments) {
		log(fqcn, Level.ERROR, t, format, arguments);
	}

	//------------------------------------------------------------------------- Log
	@Override
	public void log(final String fqcn, final Level level, final Throwable t, final String format, final Object... arguments) {
		// fqcn 无效
		if (!isEnabled(level)) {
			return;
		}


		final Dict dict = Dict.of()
				.set("date", DateUtil.formatNow())
				.set("level", level.toString())
				.set("name", this.name)
				.set("msg", StrUtil.format(format, arguments));

		final String logMsg = StrUtil.formatByMap(logFormat, dict);

		//WARN以上级别打印至System.err
		if (level.ordinal() >= Level.WARN.ordinal()) {
			Console.error(t, logMsg);
		} else {
			Console.log(t, logMsg);
		}
	}

	@Override
	public boolean isEnabled(final Level level) {
		return currentLevel.compareTo(level) <= 0;
	}
}
