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

package org.dromara.hutool.swing.img;

import java.awt.Image;

/**
 * 图片缩略算法类型
 *
 * @author Looly
 * @since 4.5.8
 */
public enum ScaleType {

	/**
	 * 默认
	 */
	DEFAULT(Image.SCALE_DEFAULT),
	/**
	 * 快速
	 */
	FAST(Image.SCALE_FAST),
	/**
	 * 平滑
	 */
	SMOOTH(Image.SCALE_SMOOTH),
	/**
	 * 使用 ReplicateScaleFilter 类中包含的图像缩放算法
	 */
	REPLICATE(Image.SCALE_REPLICATE),
	/**
	 * Area Averaging算法
	 */
	AREA_AVERAGING(Image.SCALE_AREA_AVERAGING);

	private final int value;
	/**
	 * 构造
	 *
	 * @param value 缩放方式
	 * @see Image#SCALE_DEFAULT
	 * @see Image#SCALE_FAST
	 * @see Image#SCALE_SMOOTH
	 * @see Image#SCALE_REPLICATE
	 * @see Image#SCALE_AREA_AVERAGING
	 */
	ScaleType(final int value) {
		this.value = value;
	}

	/**
	 * 获取值
	 *
	 * @return 值
	 */
	public int getValue() {
		return this.value;
	}
}
