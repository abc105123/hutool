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

package org.dromara.hutool.db.ds;

import org.dromara.hutool.core.spi.SpiUtil;
import org.dromara.hutool.db.DbException;
import org.dromara.hutool.db.config.DbConfig;
import org.dromara.hutool.log.LogUtil;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * {@link DataSource}相关工具类<br>
 * 主要提供数据源工厂的创建和数据源的获取
 *
 * @author Looly
 * @since 6.0.0
 */
public class DSUtil {

	/**
	 * 获取默认数据源工厂
	 *
	 * @return 默认数据源工厂
	 */
	public static DSFactory getDefaultDsFactory() {
		return SpiUtil.loadFirstAvailable(DSFactory.class);
	}

	/**
	 * 获得JNDI数据源
	 *
	 * @param jndiName JNDI名称
	 * @return 数据源
	 */
	public static DataSource getJndiDSWithLog(final String jndiName) {
		try {
			return getJndiDS(jndiName);
		} catch (final DbException e) {
			LogUtil.error(e.getCause(), "Find JNDI datasource error!");
		}
		return null;
	}

	/**
	 * 获得JNDI数据源
	 *
	 * @param jndiName JNDI名称
	 * @return 数据源
	 */
	public static DataSource getJndiDS(final String jndiName) {
		try {
			return (DataSource) new InitialContext().lookup(jndiName);
		} catch (final NamingException e) {
			throw new DbException(e);
		}
	}

	/**
	 * 根据已有配置文件，创建数据源
	 *
	 * @param dbConfig 数据库配置
	 * @return 数据源
	 */
	public static DSWrapper createDS(final DbConfig dbConfig){
		DSFactory dsFactory = dbConfig.getDsFactory();
		if(null == dsFactory){
			dsFactory = getDefaultDsFactory();
		}

		return DSWrapper.wrap(dsFactory.createDataSource(dbConfig), dbConfig);
	}

	/**
	 * 获得数据源<br>
	 * 使用默认配置文件的无分组配置
	 *
	 * @return 数据源
	 */
	public static DSWrapper getDS() {
		return getDS(null);
	}

	/**
	 * 获得数据源
	 *
	 * @param group 配置文件中对应的分组
	 * @return 数据源
	 */
	public static DSWrapper getDS(final String group) {
		return DSPool.getInstance().getDataSource(group);
	}

	/**
	 * 设置全局的数据源工厂<br>
	 * 在项目中存在多个连接池库的情况下，我们希望使用低优先级的库时使用此方法自定义之<br>
	 * 重新定义全局的数据源工厂此方法可在以下两种情况下调用：
	 *
	 * <pre>
	 * 1. 在get方法调用前调用此方法来自定义全局的数据源工厂
	 * 2. 替换已存在的全局数据源工厂，当已存在时会自动关闭
	 * </pre>
	 *
	 * @param dsFactory 数据源工厂
	 * @return 自定义的数据源工厂
	 */
	@SuppressWarnings("resource")
	public static DSFactory setGlobalDSFactory(final DSFactory dsFactory) {
		DSPool.getInstance().setFactory(dsFactory);
		return dsFactory;
	}
}
