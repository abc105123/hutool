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

package org.dromara.hutool.core.lang.test.bean;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author 质量过关
 *
 */
@Data
public class ExamInfoDict implements Serializable {
	private static final long serialVersionUID = 3640936499125004525L;

	// 主键
	private Integer id; // 可当作题号
	// 试题类型 客观题 0主观题 1
	private Integer examType;
	// 试题是否作答
	private Integer answerIs;

	public Integer getId(final Integer defaultValue) {
		return this.id == null ? defaultValue : this.id;
	}
}
