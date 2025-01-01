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

package org.dromara.hutool.db.config;

/**
 * 数据源配置的字段名
 *
 * @author Looly
 * @since 6.0.0
 */
public interface DSKeys {

	/**
	 * 配置文件中配置属性名：是否显示SQL
	 */
	String KEY_SHOW_SQL = "showSql";
	/**
	 * 配置文件中配置属性名：是否格式化SQL
	 */
	String KEY_FORMAT_SQL = "formatSql";
	/**
	 * 配置文件中配置属性名：是否显示参数
	 */
	String KEY_SHOW_PARAMS = "showParams";
	/**
	 * 配置文件中配置属性名：显示的日志级别
	 */
	String KEY_SQL_LEVEL = "sqlLevel";
	/**
	 * 配置文件中配置属性名：是否忽略大小写
	 */
	String KEY_CASE_INSENSITIVE = "caseInsensitive";

	/**
	 * 某些数据库需要的特殊配置项需要的配置项
	 */
	String[] KEY_CONN_PROPS = {"remarks", "useInformationSchema"};

	/**
	 * 别名字段名：URL
	 */
	String[] KEY_ALIAS_URL = {"url", "jdbcUrl"};
	/**
	 * 别名字段名：驱动名
	 */
	String[] KEY_ALIAS_DRIVER = {"driver", "driverClassName"};
	/**
	 * 别名字段名：用户名
	 */
	String[] KEY_ALIAS_USER = {"user", "username"};
	/**
	 * 别名字段名：密码
	 */
	String[] KEY_ALIAS_PASSWORD = {"pass", "password"};
}
