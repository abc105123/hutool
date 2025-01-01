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

package org.dromara.hutool.log.engine.jdk;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.log.AbstractLog;

/**
 * <a href="http://java.sun.com/javase/6/docs/technotes/guides/logging/index.html">java.util.logging</a> log.
 *
 * @author Looly
 *
 */
public class JdkLog extends AbstractLog {
	private static final long serialVersionUID = -6843151523380063975L;

	private final transient Logger logger;

	// ------------------------------------------------------------------------- Constructor
	public JdkLog(final Logger logger) {
		this.logger = logger;
	}

	public JdkLog(final Class<?> clazz) {
		this((null == clazz) ? StrUtil.NULL : clazz.getName());
	}

	public JdkLog(final String name) {
		this(Logger.getLogger(name));
	}

	@Override
	public String getName() {
		return logger.getName();
	}

	// ------------------------------------------------------------------------- Trace
	@Override
	public boolean isTraceEnabled() {
		return logger.isLoggable(Level.FINEST);
	}

	@Override
	public void trace(final String fqcn, final Throwable t, final String format, final Object... arguments) {
		logIfEnabled(fqcn, Level.FINEST, t, format, arguments);
	}

	// ------------------------------------------------------------------------- Debug
	@Override
	public boolean isDebugEnabled() {
		return logger.isLoggable(Level.FINE);
	}

	@Override
	public void debug(final String fqcn, final Throwable t, final String format, final Object... arguments) {
		logIfEnabled(fqcn, Level.FINE, t, format, arguments);
	}

	// ------------------------------------------------------------------------- Info
	@Override
	public boolean isInfoEnabled() {
		return logger.isLoggable(Level.INFO);
	}

	@Override
	public void info(final String fqcn, final Throwable t, final String format, final Object... arguments) {
		logIfEnabled(fqcn, Level.INFO, t, format, arguments);
	}

	// ------------------------------------------------------------------------- Warn
	@Override
	public boolean isWarnEnabled() {
		return logger.isLoggable(Level.WARNING);
	}

	@Override
	public void warn(final String fqcn, final Throwable t, final String format, final Object... arguments) {
		logIfEnabled(fqcn, Level.WARNING, t, format, arguments);
	}

	// ------------------------------------------------------------------------- Error
	@Override
	public boolean isErrorEnabled() {
		return logger.isLoggable(Level.SEVERE);
	}

	@Override
	public void error(final String fqcn, final Throwable t, final String format, final Object... arguments) {
		logIfEnabled(fqcn, Level.SEVERE, t, format, arguments);
	}

	// ------------------------------------------------------------------------- Log
	@Override
	public void log(final String fqcn, final org.dromara.hutool.log.level.Level level, final Throwable t, final String format, final Object... arguments) {
		final Level jdkLevel;
		switch (level) {
			case TRACE:
				jdkLevel = Level.FINEST;
				break;
			case DEBUG:
				jdkLevel = Level.FINE;
				break;
			case INFO:
				jdkLevel = Level.INFO;
				break;
			case WARN:
				jdkLevel = Level.WARNING;
				break;
			case ERROR:
				jdkLevel = Level.SEVERE;
				break;
			default:
				throw new Error(StrUtil.format("Can not identify level: {}", level));
		}
		logIfEnabled(fqcn, jdkLevel, t, format, arguments);
	}

	// ------------------------------------------------------------------------- Private method
	/**
	 * 打印对应等级的日志
	 *
	 * @param callerFQCN 调用者的完全限定类名(Fully Qualified Class Name)
	 * @param level 等级
	 * @param throwable 异常对象
	 * @param format 消息模板
	 * @param arguments 参数
	 */
	private void logIfEnabled(final String callerFQCN, final Level level, final Throwable throwable, final String format, final Object[] arguments){
		if(logger.isLoggable(level)){
			final LogRecord record = new LogRecord(level, StrUtil.format(format, arguments));
			record.setLoggerName(getName());
			record.setThrown(throwable);
			fillCallerData(callerFQCN, record);
			logger.log(record);
		}
	}

	/**
	 * 传入调用日志类的信息
	 * @param callerFQCN 调用者全限定类名
	 * @param record The record to update
	 */
	private static void fillCallerData(final String callerFQCN, final LogRecord record) {
		final StackTraceElement[] steArray = Thread.currentThread().getStackTrace();

		int found = -1;
		String className;
		for (int i = steArray.length -2; i > -1; i--) {
			// 此处初始值为length-2，表示从倒数第二个堆栈开始检查，如果是倒数第一个，那调用者就获取不到
			className = steArray[i].getClassName();
			if (callerFQCN.equals(className)) {
				found = i;
				break;
			}
		}

		if (found > -1) {
			final StackTraceElement ste = steArray[found+1];
			record.setSourceClassName(ste.getClassName());
			record.setSourceMethodName(ste.getMethodName());
		}
	}
}
