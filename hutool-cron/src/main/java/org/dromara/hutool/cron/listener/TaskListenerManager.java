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

package org.dromara.hutool.cron.listener;

import org.dromara.hutool.cron.TaskExecutor;
import org.dromara.hutool.log.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 监听调度器，统一管理监听
 * @author Looly
 *
 */
public class TaskListenerManager implements Serializable {
	private static final long serialVersionUID = 1L;

	private final List<TaskListener> listeners = new ArrayList<>();

	/**
	 * 增加监听器
	 * @param listener {@link TaskListener}
	 * @return this
	 */
	public TaskListenerManager addListener(final TaskListener listener){
		synchronized (listeners) {
			this.listeners.add(listener);
		}
		return this;
	}

	/**
	 * 移除监听器
	 * @param listener {@link TaskListener}
	 * @return this
	 */
	public TaskListenerManager removeListener(final TaskListener listener){
		synchronized (listeners) {
			this.listeners.remove(listener);
		}
		return this;
	}

	/**
	 * 通知所有监听任务启动器启动
	 * @param executor {@link TaskExecutor}
	 */
	public void notifyTaskStart(final TaskExecutor executor) {
		synchronized (listeners) {
			TaskListener listener;
			for (final TaskListener taskListener : listeners) {
				listener = taskListener;
				if (null != listener) {
					listener.onStart(executor);
				}
			}
		}
	}

	/**
	 * 通知所有监听任务启动器成功结束
	 * @param executor {@link TaskExecutor}
	 */
	public void notifyTaskSucceeded(final TaskExecutor executor) {
		synchronized (listeners) {
			for (final TaskListener listener : listeners) {
				listener.onSucceeded(executor);
			}
		}
	}

	/**
	 * 通知所有监听任务启动器结束并失败<br>
	 * 无监听将打印堆栈到命令行
	 * @param executor {@link TaskExecutor}
	 * @param exception 失败原因
	 */
	public void notifyTaskFailed(final TaskExecutor executor, final Throwable exception) {
		synchronized (listeners) {
			final int size = listeners.size();
			if(size > 0){
				for (final TaskListener listener : listeners) {
					listener.onFailed(executor, exception);
				}
			}else{
				LogUtil.error(exception, exception.getMessage());
			}
		}
	}
}
