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

import java.io.Serializable;

public class UserInfoRedundCount implements Serializable {

	private static final long serialVersionUID = -8397291070139255181L;
	private String finishedRatio; // 完成率

	private Integer ownershipExamCount; // 自己有多少道题

	private Integer answeredExamCount; // 当前回答了多少道题

	public Integer getOwnershipExamCount() {
		return ownershipExamCount;
	}

	public void setOwnershipExamCount(final Integer ownershipExamCount) {
		this.ownershipExamCount = ownershipExamCount;
	}

	public Integer getAnsweredExamCount() {
		return answeredExamCount;
	}

	public void setAnsweredExamCount(final Integer answeredExamCount) {
		this.answeredExamCount = answeredExamCount;
	}

	public String getFinishedRatio() {
		return finishedRatio;
	}

	public void setFinishedRatio(final String finishedRatio) {
		this.finishedRatio = finishedRatio;
	}

}
