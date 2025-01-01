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

package org.dromara.hutool.core.io.file;

import org.dromara.hutool.core.io.IORuntimeException;
import org.dromara.hutool.core.io.file.visitor.DelVisitor;
import org.dromara.hutool.core.lang.Assert;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * 文件删除封装
 *
 * @author Looly
 * @since 6.0.0
 */
public class PathDeleter {

	/**
	 * 创建文件或目录移动器
	 *
	 * @param src 源文件或目录
	 * @return {@code PathMover}
	 */
	public static PathDeleter of(final Path src) {
		return new PathDeleter(src);
	}

	private final Path path;

	/**
	 * 构造
	 *
	 * @param path 文件或目录，不能为{@code null}且必须存在
	 */
	public PathDeleter(final Path path) {
		this.path = Assert.notNull(path, "Path must be not null !");
	}

	/**
	 * 删除文件或者文件夹，不追踪软链<br>
	 * 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
	 * 某个文件删除失败会终止删除操作
	 *
	 * @throws IORuntimeException IO异常
	 */
	public void del() throws IORuntimeException {
		final Path path = this.path;
		if (Files.notExists(path)) {
			return;
		}

		if (PathUtil.isDirectory(path)) {
			_del(path);
		} else {
			delFile(path);
		}
	}

	/**
	 * 清空目录
	 */
	public void clean() {
		try (final Stream<Path> list = Files.list(this.path)){
			list.forEach(PathUtil::del);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 删除目录
	 *
	 * @param path 目录路径
	 */
	private static void _del(final Path path) {
		try {
			Files.walkFileTree(path, DelVisitor.INSTANCE);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 删除文件或空目录，不追踪软链
	 *
	 * @param path 文件对象
	 * @throws IORuntimeException IO异常
	 * @since 5.7.7
	 */
	private static void delFile(final Path path) throws IORuntimeException {
		try {
			Files.delete(path);
		} catch (final IOException e) {
			if (e instanceof AccessDeniedException) {
				// 可能遇到只读文件，无法删除.使用 file 方法删除
				if (path.toFile().delete()) {
					return;
				}
			}
			throw new IORuntimeException(e);
		}
	}
}
