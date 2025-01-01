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

import org.dromara.hutool.core.exception.ExceptionUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.log.level.Level;

import java.io.Serializable;

/**
 * 抽象日志类<br>
 * 实现了一些通用的接口
 *
 * @author Looly
 *
 */
public abstract class AbstractLog implements Log, Serializable{

	private static final long serialVersionUID = -3211115409504005616L;
	private static final String FQCN = AbstractLog.class.getName();

	@Override
	public boolean isEnabled(final Level level) {
		switch (level) {
			case TRACE:
				return isTraceEnabled();
			case DEBUG:
				return isDebugEnabled();
			case INFO:
				return isInfoEnabled();
			case WARN:
				return isWarnEnabled();
			case ERROR:
				return isErrorEnabled();
			default:
				throw new Error(StrUtil.format("Can not identify level: {}", level));
		}
	}

	@Override
	public void trace(final Throwable t) {
		trace(t, ExceptionUtil.getSimpleMessage(t));
	}

	@Override
	public void trace(final String format, final Object... arguments) {
		trace(null, format, arguments);
	}

	@Override
	public void trace(final Throwable t, final String format, final Object... arguments) {
		trace(FQCN, t, format, arguments);
	}

	@Override
	public void debug(final Throwable t) {
		debug(t, ExceptionUtil.getSimpleMessage(t));
	}

	@Override
	public void debug(final String format, final Object... arguments) {
		if(null != arguments && 1 == arguments.length && arguments[0] instanceof Throwable) {
			// 兼容Slf4j中的xxx(String message, Throwable e)
			debug((Throwable)arguments[0], format);
		} else {
			debug(null, format, arguments);
		}
	}

	@Override
	public void debug(final Throwable t, final String format, final Object... arguments) {
		debug(FQCN, t, format, arguments);
	}

	@Override
	public void info(final Throwable t) {
		info(t, ExceptionUtil.getSimpleMessage(t));
	}

	@Override
	public void info(final String format, final Object... arguments) {
		if(null != arguments && 1 == arguments.length && arguments[0] instanceof Throwable) {
			// 兼容Slf4j中的xxx(String message, Throwable e)
			info((Throwable)arguments[0], format);
		} else {
			info(null, format, arguments);
		}
	}

	@Override
	public void info(final Throwable t, final String format, final Object... arguments) {
		info(FQCN, t, format, arguments);
	}

	@Override
	public void warn(final Throwable t) {
		warn(t, ExceptionUtil.getSimpleMessage(t));
	}

	@Override
	public void warn(final String format, final Object... arguments) {
		if(null != arguments && 1 == arguments.length && arguments[0] instanceof Throwable) {
			// 兼容Slf4j中的xxx(String message, Throwable e)
			warn((Throwable)arguments[0], format);
		} else {
			warn(null, format, arguments);
		}
	}

	@Override
	public void warn(final Throwable t, final String format, final Object... arguments) {
		warn(FQCN, t, format, arguments);
	}

	@Override
	public void error(final Throwable t) {
		this.error(t, ExceptionUtil.getSimpleMessage(t));
	}

	@Override
	public void error(final String format, final Object... arguments) {
		if(null != arguments && 1 == arguments.length && arguments[0] instanceof Throwable) {
			// 兼容Slf4j中的xxx(String message, Throwable e)
			error((Throwable)arguments[0], format);
		} else {
			error(null, format, arguments);
		}
	}

	@Override
	public void error(final Throwable t, final String format, final Object... arguments) {
		error(FQCN, t, format, arguments);
	}

	@Override
	public void log(final Level level, final String format, final Object... arguments) {
		if(null != arguments && 1 == arguments.length && arguments[0] instanceof Throwable) {
			// 兼容Slf4j中的xxx(String message, Throwable e)
			log(level, (Throwable)arguments[0], format);
		} else {
			log(level, null, format, arguments);
		}
	}

	@Override
	public void log(final Level level, final Throwable t, final String format, final Object... arguments) {
		this.log(FQCN, level, t, format, arguments);
	}
}
