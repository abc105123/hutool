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

package org.dromara.hutool.extra.management;

import org.dromara.hutool.core.io.file.FileUtil;

import java.io.Serializable;

/**
 * 运行时信息，包括内存总大小、已用大小、可用大小等
 *
 * @author Looly
 */
public class RuntimeInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Runtime currentRuntime = Runtime.getRuntime();

	/**
	 * 获得运行时对象
	 *
	 * @return {@link Runtime}
	 */
	public final Runtime getRuntime() {
		return currentRuntime;
	}

	/**
	 * 获得JVM最大内存
	 *
	 * @return 最大内存
	 */
	public final long getMaxMemory() {
		return currentRuntime.maxMemory();
	}

	/**
	 * 获得JVM已分配内存
	 *
	 * @return 已分配内存
	 */
	public final long getTotalMemory() {
		return currentRuntime.totalMemory();
	}

	/**
	 * 获得JVM已分配内存中的剩余空间
	 *
	 * @return 已分配内存中的剩余空间
	 */
	public final long getFreeMemory() {
		return currentRuntime.freeMemory();
	}

	/**
	 * 获得JVM最大可用内存
	 *
	 * @return 最大可用内存
	 */
	public final long getUsableMemory() {
		return currentRuntime.maxMemory() - currentRuntime.totalMemory() + currentRuntime.freeMemory();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		ManagementUtil.append(builder, "Max Memory:    ", FileUtil.readableFileSize(getMaxMemory()));
		ManagementUtil.append(builder, "Total Memory:     ", FileUtil.readableFileSize(getTotalMemory()));
		ManagementUtil.append(builder, "Free Memory:     ", FileUtil.readableFileSize(getFreeMemory()));
		ManagementUtil.append(builder, "Usable Memory:     ", FileUtil.readableFileSize(getUsableMemory()));

		return builder.toString();
	}
}
