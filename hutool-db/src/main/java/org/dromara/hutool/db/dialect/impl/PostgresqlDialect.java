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

import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.CharUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.db.Entity;
import org.dromara.hutool.db.sql.StatementUtil;
import org.dromara.hutool.db.config.DbConfig;
import org.dromara.hutool.db.dialect.DialectName;
import org.dromara.hutool.db.sql.QuoteWrapper;
import org.dromara.hutool.db.sql.SqlBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;


/**
 * Postgree方言
 *
 * @author loolly
 */
public class PostgresqlDialect extends AnsiSqlDialect {
	private static final long serialVersionUID = 3889210427543389642L;

	/**
	 * 构造
	 * @param dbConfig 数据库配置
	 */
	public PostgresqlDialect(final DbConfig dbConfig) {
		super(dbConfig);
		quoteWrapper = new QuoteWrapper(CharUtil.DOUBLE_QUOTES);
	}

	@Override
	public String dialectName() {
		return DialectName.POSTGRESQL.name();
	}

	@Override
	public PreparedStatement psForUpsert(final Connection conn, final Entity entity, String... keys) {
		Assert.notEmpty(keys, "Keys must be not empty for Postgres.");
		SqlBuilder.validateEntity(entity);
		final SqlBuilder builder = SqlBuilder.of(quoteWrapper);

		final StringBuilder fieldsPart = new StringBuilder();
		final StringBuilder placeHolder = new StringBuilder();
		final StringBuilder updateHolder = new StringBuilder();

		// 构建字段部分和参数占位符部分
		entity.forEach((field, value) -> {
			if (StrUtil.isNotBlank(field)) {
				if (fieldsPart.length() > 0) {
					// 非第一个参数，追加逗号
					fieldsPart.append(", ");
					placeHolder.append(", ");
					updateHolder.append(", ");
				}

				final String wrapedField = (null != quoteWrapper) ? quoteWrapper.wrap(field) : field;
				fieldsPart.append(wrapedField);
				updateHolder.append(wrapedField).append("=EXCLUDED.").append(wrapedField);
				placeHolder.append("?");
				builder.addParams(value);
			}
		});

		String tableName = entity.getTableName();
		if (null != this.quoteWrapper) {
			tableName = this.quoteWrapper.wrap(tableName);
			keys = quoteWrapper.wrap(keys);
		}
		builder.append("INSERT INTO ").append(tableName)
			// 字段列表
			.append(" (").append(fieldsPart)
			// 更新值列表
			.append(") VALUES (").append(placeHolder)
			// 定义检查冲突的主键或字段
			.append(") ON CONFLICT (").append(ArrayUtil.join(keys, ", "))
			// 主键冲突后的更新操作
			.append(") DO UPDATE SET ").append(updateHolder);

		return StatementUtil.prepareStatement(false, this.dbConfig, conn, builder.build(), builder.getParamValueArray());
	}
}
