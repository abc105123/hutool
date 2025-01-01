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

import org.dromara.hutool.core.exception.HutoolException;
import org.dromara.hutool.core.io.IORuntimeException;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片元信息工具类<br>
 * 借助metadata-extractor完成图片元信息的读取，如旋转角度等问题
 *
 * @author wdz
 * @since 6.0.0
 */
public class ImgMetaUtil {

	/**
	 * 获取图片文件旋转角度
	 *
	 * @param file 上传图片
	 * @return 旋转角度
	 * @throws IORuntimeException IO异常
	 */
	public static int getOrientation(final File file) throws IORuntimeException {
		final Metadata metadata;
		try {
			metadata = ImageMetadataReader.readMetadata(file);
		} catch (final ImageProcessingException e) {
			throw new HutoolException(e);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
		return getOrientation(metadata);
	}

	/**
	 * 获取图片旋转角度
	 *
	 * @param in 图片流
	 * @return 旋转角度
	 * @throws IORuntimeException IO异常
	 */
	public static int getOrientation(final InputStream in) throws IORuntimeException {
		final Metadata metadata;
		try {
			metadata = ImageMetadataReader.readMetadata(in);
		} catch (final ImageProcessingException e) {
			throw new HutoolException(e);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return getOrientation(metadata);
	}

	/**
	 * 获取旋转角度
	 * @param metadata {@link Metadata}
	 * @return 旋转角度，可能为90,180,270
	 */
	private static int getOrientation(final Metadata metadata) {
		for (final Directory directory : metadata.getDirectories()) {
			for (final Tag tag : directory.getTags()) {
				if ("Orientation".equals(tag.getTagName())) {
					final String orientation = tag.getDescription();
					if (orientation.contains("90")) {
						return 90;
					} else if (orientation.contains("180")) {
						return 180;
					} else if (orientation.contains("270")) {
						return 270;
					}
				}
			}
		}
		return 0;
	}
}
