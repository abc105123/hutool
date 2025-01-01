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

package org.dromara.hutool.core.date;

import org.dromara.hutool.core.text.StrUtil;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 时长格式化器，用于格式化输出两个日期相差的时长<br>
 * 根据{@link Level}不同，调用{@link #format()}方法后返回类似于：
 * <ul>
 *    <li>XX小时XX分XX秒</li>
 *    <li>XX天XX小时</li>
 *    <li>XX月XX天XX小时</li>
 * </ul>
 *
 * @author Looly
 */
public class BetweenFormatter implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 时长毫秒数
	 */
	private long betweenMs;
	/**
	 * 格式化级别
	 */
	private Level level;
	/**
	 * 格式化级别的最大个数
	 */
	private final int levelMaxCount;
	/**
	 * 是否为简化模式，此标记用于自定义是否输出各个位数中间为0的部分<br>
	 * 如为{@code true}，输出 1小时3秒，为{@code false}输出 1小时0分3秒
	 */
	private boolean simpleMode = true;
	/**
	 * 格式化器
	 */
	private Function<Level, String> levelFormatter = Level::getName;
	/**
	 * 分隔符
	 */
	private String separator = StrUtil.EMPTY;

	/**
	 * 创建 BetweenFormatter
	 *
	 * @param betweenMs 日期间隔
	 * @param level     级别，按照天、小时、分、秒、毫秒分为5个等级，根据传入等级，格式化到相应级别
	 * @return BetweenFormatter
	 */
	public static BetweenFormatter of(final long betweenMs, final Level level) {
		return of(betweenMs, level, 0);
	}

	/**
	 * 创建 BetweenFormatter
	 *
	 * @param betweenMs     日期间隔
	 * @param level         级别，按照天、小时、分、秒、毫秒分为5个等级，根据传入等级，格式化到相应级别
	 * @param levelMaxCount 格式化级别的最大个数，假如级别个数为1，但是级别到秒，那只显示一个级别
	 * @return BetweenFormatter
	 */
	public static BetweenFormatter of(final long betweenMs, final Level level, final int levelMaxCount) {
		return new BetweenFormatter(betweenMs, level, levelMaxCount);
	}

	/**
	 * 构造
	 *
	 * @param betweenMs     日期间隔
	 * @param level         级别，按照天、小时、分、秒、毫秒分为5个等级，根据传入等级，格式化到相应级别
	 * @param levelMaxCount 格式化级别的最大个数，假如级别个数为1，但是级别到秒，那只显示一个级别
	 */
	public BetweenFormatter(final long betweenMs, final Level level, final int levelMaxCount) {
		this.betweenMs = betweenMs;
		this.level = level;
		this.levelMaxCount = levelMaxCount;
	}

	/**
	 * 格式化日期间隔输出<br>
	 *
	 * @return 格式化后的字符串
	 */
	public String format() {
		final StringBuilder sb = new StringBuilder();
		if (betweenMs > 0) {
			final long day = betweenMs / DateUnit.DAY.getMillis();
			final long hour = betweenMs / DateUnit.HOUR.getMillis() - day * 24;
			final long minute = betweenMs / DateUnit.MINUTE.getMillis() - day * 24 * 60 - hour * 60;

			final long BetweenOfSecond = ((day * 24 + hour) * 60 + minute) * 60;
			final long second = betweenMs / DateUnit.SECOND.getMillis() - BetweenOfSecond;
			final long millisecond = betweenMs - (BetweenOfSecond + second) * 1000;

			final int level = this.level.ordinal();
			int levelCount = 0;

			// 天
			if (isLevelCountValid(levelCount) && day > 0) {
				sb.append(day).append(levelFormatter.apply(Level.DAY)).append(separator);
				levelCount++;
			}

			// 时
			if (isLevelCountValid(levelCount) && level >= Level.HOUR.ordinal()) {
				if (hour > 0 || (!this.simpleMode && StrUtil.isNotEmpty(sb))) {
					sb.append(hour).append(levelFormatter.apply(Level.HOUR)).append(separator);
					levelCount++;
				}
			}

			// 分
			if (isLevelCountValid(levelCount) && level >= Level.MINUTE.ordinal()) {
				if (minute > 0 || (!this.simpleMode && StrUtil.isNotEmpty(sb))) {
					sb.append(minute).append(levelFormatter.apply(Level.MINUTE)).append(separator);
					levelCount++;
				}
			}

			// 秒
			if (isLevelCountValid(levelCount) && level >= Level.SECOND.ordinal()) {
				if (second > 0 || (!this.simpleMode && StrUtil.isNotEmpty(sb))) {
					sb.append(second).append(levelFormatter.apply(Level.SECOND)).append(separator);
					levelCount++;
				}
			}

			// 毫秒
			if (isLevelCountValid(levelCount) && millisecond > 0 && level >= Level.MILLISECOND.ordinal()) {
				sb.append(millisecond).append(levelFormatter.apply(Level.MILLISECOND)).append(separator);
				// levelCount++;
			}
		}

		if (StrUtil.isEmpty(sb)) {
			sb.append(0).append(levelFormatter.apply(this.level));
		} else if(StrUtil.isNotEmpty(separator)) {
			sb.delete(sb.length() - separator.length(), sb.length());
		}
		// 自定义实现最后可能存在空格
		return sb.toString().trim();
	}

	/**
	 * 获得 时长毫秒数
	 *
	 * @return 时长毫秒数
	 */
	public long getBetweenMs() {
		return betweenMs;
	}

	/**
	 * 设置 时长毫秒数
	 *
	 * @param betweenMs 时长毫秒数
	 * @return this
	 */
	public BetweenFormatter setBetweenMs(final long betweenMs) {
		this.betweenMs = betweenMs;
		return this;
	}

	/**
	 * 获得 格式化级别
	 *
	 * @return 格式化级别
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * 设置格式化级别
	 *
	 * @param level 格式化级别
	 * @return this
	 */
	public BetweenFormatter setLevel(final Level level) {
		this.level = level;
		return this;
	}

	/**
	 * 是否为简化模式，此标记用于自定义是否输出各个位数中间为0的部分<br>
	 * 如为{@code true}，输出 1小时3秒，为{@code false}输出 1小时0分3秒
	 *
	 * @param simpleMode 是否简化模式
	 * @return this
	 */
	public BetweenFormatter setSimpleMode(final boolean simpleMode) {
		this.simpleMode = simpleMode;
		return this;
	}

	/**
	 * 设置级别格式化器
	 *
	 * @param levelFormatter 级别格式化器
	 * @return this
	 */
	public BetweenFormatter setLevelFormatter(final Function<Level, String> levelFormatter) {
		this.levelFormatter = levelFormatter;
		return this;
	}

	/**
	 * 设置分隔符
	 *
	 * @param separator 分割符
	 * @return this
	 */
	public BetweenFormatter setSeparator(final String separator) {
		this.separator = StrUtil.toStringOrEmpty(separator);
		return this;
	}

	/**
	 * 格式化等级枚举
	 *
	 * @author Looly
	 */
	public enum Level {

		/**
		 * 天
		 */
		DAY("天"),
		/**
		 * 小时
		 */
		HOUR("小时"),
		/**
		 * 分钟
		 */
		MINUTE("分"),
		/**
		 * 秒
		 */
		SECOND("秒"),
		/**
		 * 毫秒
		 */
		MILLISECOND("毫秒");

		/**
		 * 级别名称
		 */
		private final String name;

		/**
		 * 构造
		 *
		 * @param name 级别名称
		 */
		Level(final String name) {
			this.name = name;
		}

		/**
		 * 获取级别名称
		 *
		 * @return 级别名称
		 */
		public String getName() {
			return this.name;
		}
	}

	@Override
	public String toString() {
		return format();
	}

	/**
	 * 等级数量是否有效<br>
	 * 有效的定义是：levelMaxCount大于0（被设置），当前等级数量没有超过这个最大值
	 *
	 * @param levelCount 登记数量
	 * @return 是否有效
	 */
	private boolean isLevelCountValid(final int levelCount) {
		return this.levelMaxCount <= 0 || levelCount < this.levelMaxCount;
	}
}
