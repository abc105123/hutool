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

package org.dromara.hutool.log.engine;

import org.dromara.hutool.core.io.resource.ResourceUtil;
import org.dromara.hutool.core.lang.Singleton;
import org.dromara.hutool.core.reflect.ConstructorUtil;
import org.dromara.hutool.core.spi.SpiUtil;
import org.dromara.hutool.log.LogFactory;
import org.dromara.hutool.log.engine.commons.ApacheCommonsLogEngine;
import org.dromara.hutool.log.engine.console.ConsoleLogEngine;
import org.dromara.hutool.log.engine.jdk.JdkLogEngine;
import org.dromara.hutool.log.engine.log4j.Log4jLogEngine;
import org.dromara.hutool.log.engine.log4j2.Log4j2LogEngine;
import org.dromara.hutool.log.engine.slf4j.Slf4jLogEngine;

import java.net.URL;

/**
 * 日志引擎简单工厂（静态工厂）类
 *
 * @author Looly
 * @since 6.0.0
 */
public class LogEngineFactory {

	/**
	 * issue#I7PHNT，嵌套使用Singleton.get时在JDK9+会引起Recursive update问题，此处日志单独使用单例
	 */
	private static class InstanceHolder {
		public static final LogEngine INSTANCE = createEngine();
	}

	/**
	 * 根据用户引入的模板引擎jar，自动创建对应的模板引擎对象<br>
	 * 获得的是单例的TemplateEngine
	 *
	 * @return 单例的TemplateEngine
	 */
	public static LogEngine getEngine() {
		return InstanceHolder.INSTANCE;
	}

	/**
	 * 自定义默认日志实现
	 *
	 * @param logEngineClass 日志工厂类
	 * @see Slf4jLogEngine
	 * @see Log4jLogEngine
	 * @see Log4j2LogEngine
	 * @see ApacheCommonsLogEngine
	 * @see JdkLogEngine
	 * @see ConsoleLogEngine
	 */
	public static void setDefaultEngine(final Class<? extends LogEngine> logEngineClass) {
		try {
			setDefaultEngine(ConstructorUtil.newInstance(logEngineClass));
		} catch (final Exception e) {
			throw new IllegalArgumentException("Can not instance LogFactory class!", e);
		}
	}

	/**
	 * 自定义日志实现
	 *
	 * @param logEngine 日志引擎对象
	 * @see Slf4jLogEngine
	 * @see Log4jLogEngine
	 * @see Log4j2LogEngine
	 * @see ApacheCommonsLogEngine
	 * @see JdkLogEngine
	 * @see ConsoleLogEngine
	 */
	public static void setDefaultEngine(final LogEngine logEngine) {
		Singleton.put(LogEngineFactory.class.getName(), logEngine);
		logEngine.createLog(LogEngineFactory.class).debug("Custom Use [{}] Logger.", logEngine.getName());
	}

	/**
	 * 创建指定日志实现引擎
	 *
	 * @param logEngineClass 引擎类
	 * @return {@link LogEngine}
	 */
	public static LogEngine createEngine(final Class<? extends LogEngine> logEngineClass) {
		return ConstructorUtil.newInstance(logEngineClass);
	}

	/**
	 * 决定日志实现
	 * <p>
	 * 依次按照顺序检查日志库的jar是否被引入，如果未引入任何日志库，则检查ClassPath下的logging.properties，<br>
	 * 存在则使用JdkLogFactory，否则使用ConsoleLogFactory
	 *
	 * @return 日志实现类
	 */
	public static LogEngine createEngine() {
		final LogEngine engine = doCreateEngine();
		engine.createLog(LogFactory.class).debug("Use [{}] Logger As Default.", engine.getName());
		return engine;
	}

	/**
	 * 决定日志实现
	 * <p>
	 * 依次按照顺序检查日志库的jar是否被引入，如果未引入任何日志库，则检查ClassPath下的logging.properties，存在则使用JdkLogFactory，否则使用ConsoleLogFactory
	 *
	 * @return 日志实现类
	 */
	private static LogEngine doCreateEngine() {
		final LogEngine engine = SpiUtil.loadFirstAvailable(LogEngine.class);
		if (null != engine) {
			return engine;
		}

		// 未找到任何可支持的日志库时判断依据：当JDK Logging的配置文件位于classpath中，使用JDK Logging，否则使用Console
		final URL url = ResourceUtil.getResourceUrl("logging.properties");
		return (null != url) ? new JdkLogEngine() : new ConsoleLogEngine();
	}
}
