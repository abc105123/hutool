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

package org.dromara.hutool.db.dialect.impl;

import org.dromara.hutool.db.DbException;
import org.dromara.hutool.db.Entity;
import org.dromara.hutool.db.config.DbConfig;
import org.dromara.hutool.db.dialect.DialectName;
import org.dromara.hutool.db.sql.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Phoenix数据库方言
 *
 * @author loolly
 * @since 5.7.2
 */
public class PhoenixDialect extends AnsiSqlDialect {
	private static final long serialVersionUID = 1L;

	/**
	 * 构造
	 * @param dbConfig 数据库配置
	 */
	public PhoenixDialect(final DbConfig dbConfig) {
		super(dbConfig);
//		wrapper = new Wrapper('"');
	}

	@Override
	public PreparedStatement psForUpdate(final Connection conn, final Entity entity, final Query query) throws DbException {
		// Phoenix的插入、更新语句是统一的，统一使用upsert into关键字
		// Phoenix只支持通过主键更新操作，因此query无效，自动根据entity中的主键更新
		return super.psForInsert(true, conn, entity);
	}

	@Override
	public String dialectName() {
		return DialectName.PHOENIX.name();
	}

	@Override
	public PreparedStatement psForUpsert(final Connection conn, final Entity entity, final String... keys) throws DbException {
		// Phoenix只支持通过主键更新操作，因此query无效，自动根据entity中的主键更新
		return psForInsert(true, conn, entity);
	}
}
