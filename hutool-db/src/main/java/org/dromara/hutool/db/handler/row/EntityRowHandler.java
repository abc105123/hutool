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

package org.dromara.hutool.db.handler.row;

import org.dromara.hutool.db.Entity;
import org.dromara.hutool.db.handler.ResultSetUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * 将{@link ResultSet}结果中的某行处理为{@link Entity}对象
 *
 * @author Looly
 */
public class EntityRowHandler extends AbsRowHandler<Entity> {

	private final boolean caseInsensitive;
	private final boolean withMetaInfo;

	/**
	 * 构造
	 *
	 * @param meta            {@link ResultSetMetaData}
	 * @param caseInsensitive 是否大小写不敏感
	 * @param withMetaInfo    是否包含表名、字段名等元信息
	 * @throws SQLException SQL异常
	 */
	public EntityRowHandler(final ResultSetMetaData meta, final boolean caseInsensitive, final boolean withMetaInfo) throws SQLException {
		super(meta);
		this.caseInsensitive = caseInsensitive;
		this.withMetaInfo = withMetaInfo;
	}

	@Override
	public Entity handle(final ResultSet rs) throws SQLException {
		final Entity entity = new Entity((Entity.Meta)null, caseInsensitive);
		return fillEntity(entity, rs);
	}

	/**
	 * 处理单条数据
	 *
	 * @param <T>          Entity及其子对象
	 * @param row          Entity对象
	 * @param rs           数据集
	 * @return 每一行的Entity
	 * @throws SQLException SQL执行异常
	 * @since 3.3.1
	 */
	private <T extends Entity> T fillEntity(final T row, final ResultSet rs) throws SQLException {
		int type;
		String columnLabel;
		for (int i = 1; i <= columnCount; i++) {
			type = meta.getColumnType(i);
			columnLabel = meta.getColumnLabel(i);
			if("rownum_".equalsIgnoreCase(columnLabel)){
				// issue#2618@Github
				// 分页时会查出rownum字段，此处忽略掉读取
				continue;
			}
			row.put(columnLabel, ResultSetUtil.getColumnValue(rs, i, type, null));
		}
		if (withMetaInfo) {
			try {
				row.setTableName(meta.getTableName(1));
			} catch (final SQLException ignore) {
				//issue#I2AGLU@Gitee
				// Hive等NoSQL中无表的概念，此处报错，跳过。
			}
			row.setFieldNames(row.keySet());
		}
		return row;
	}
}
