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

import org.dromara.hutool.core.collection.ListUtil;
import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.core.util.CharsetUtil;
import org.dromara.hutool.core.util.SystemUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link FileUtil} 单元测试类
 *
 * @author Looly
 */
public class FileUtilTest {

	@Test
	void fileTest1() {
		final File file = FileUtil.file("d:/aaa", "bbb");
		Assertions.assertNotNull(file);
	}

	@Test
	public void fileTest2() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			// 构建目录中出现非子目录抛出异常
			FileUtil.file("d:/aaa/bbb", "../ccc");
		});
	}

	@Test
	public void getAbsolutePathTest() {
		final String absolutePath = FileUtil.getAbsolutePath("LICENSE-junit.txt");
		Assertions.assertNotNull(absolutePath);
		final String absolutePath2 = FileUtil.getAbsolutePath(absolutePath);
		Assertions.assertNotNull(absolutePath2);
		Assertions.assertEquals(absolutePath, absolutePath2);

		String path = FileUtil.getAbsolutePath("中文.xml");
		assertTrue(path.contains("中文.xml"));

		path = FileUtil.getAbsolutePath("d:");
		Assertions.assertEquals("d:", path);
	}

	@Test
	@Disabled
	public void touchTest() {
		FileUtil.touch("d:\\tea\\a.jpg");
	}

	@Test
	@Disabled
	public void renameTest() {
		FileUtil.rename(FileUtil.file("d:/test/3.jpg"), "2.jpg", false);
	}

	@Test
	@Disabled
	public void renameTest2() {
		FileUtil.move(FileUtil.file("d:/test/a"), FileUtil.file("d:/test/b"), false);
	}

	@Test
	@Disabled
	public void renameToSubTest() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			// 移动到子目录，报错
			FileUtil.move(FileUtil.file("d:/test/a"), FileUtil.file("d:/test/a/c"), false);
		});
	}

	@Test
	@Disabled
	public void renameSameTest() {
		// 目标和源相同，不处理
		FileUtil.move(FileUtil.file("d:/test/a"), FileUtil.file("d:/test/a"), false);
	}

	@Test
	public void copyTest() {
		final File srcFile = FileUtil.file("hutool.jpg");
		final File destFile = FileUtil.file("hutool.copy.jpg");

		FileUtil.copy(srcFile, destFile, true);

		assertTrue(destFile.exists());
		Assertions.assertEquals(srcFile.length(), destFile.length());
	}

	@Test
	@Disabled
	public void copySameTest() {
		final File srcFile = FileUtil.file("d:/test/a");
		final File destFile = FileUtil.file("d:/test/");

		// 拷贝到当前目录，不做处理
		FileUtil.copy(srcFile, destFile, true);
	}

	@Test
	@Disabled
	public void copyDirTest() {
		final File srcFile = FileUtil.file("D:\\test");
		final File destFile = FileUtil.file("E:\\");

		FileUtil.copy(srcFile, destFile, true);
	}

	@Test
	@Disabled
	public void moveDirTest() {
		final File srcFile = FileUtil.file("E:\\test2");
		final File destFile = FileUtil.file("D:\\");

		FileUtil.move(srcFile, destFile, true);
	}

	@Test
	public void equalsTest() {
		// 源文件和目标文件都不存在
		final File srcFile = FileUtil.file("d:/hutool.jpg");
		final File destFile = FileUtil.file("d:/hutool.jpg");

		final boolean equals = FileUtil.equals(srcFile, destFile);
		assertTrue(equals);

		// 源文件存在，目标文件不存在
		final File srcFile1 = FileUtil.file("hutool.jpg");
		final File destFile1 = FileUtil.file("d:/hutool.jpg");

		final boolean notEquals = FileUtil.equals(srcFile1, destFile1);
		Assertions.assertFalse(notEquals);
	}

	@Test
	@Disabled
	public void convertLineSeparatorTest() {
		FileUtil.convertLineSeparator(FileUtil.file("d:/aaa.txt"), CharsetUtil.UTF_8, LineSeparator.WINDOWS);
	}

	@Test
	public void normalizeHomePathTest() {
		final String home = SystemUtil.getUserHomePath().replace('\\', '/');
		Assertions.assertEquals(home + "/bar/", FileUtil.normalize("~/foo/../bar/"));
	}

	@Test
	public void normalizeHomePathTest2() {
		final String home = SystemUtil.getUserHomePath().replace('\\', '/');
		// 多个~应该只替换开头的
		Assertions.assertEquals(home + "/~bar/", FileUtil.normalize("~/foo/../~bar/"));
	}

	@Test
	public void normalizeClassPathTest() {
		Assertions.assertEquals("", FileUtil.normalize("classpath:"));
	}

	@Test
	public void normalizeClassPathTest2() {
		Assertions.assertEquals("../a/b.csv", FileUtil.normalize("../a/b.csv"));
		Assertions.assertEquals("../../../a/b.csv", FileUtil.normalize("../../../a/b.csv"));
	}

	@Test
	public void doubleNormalizeTest() {
		final String normalize = FileUtil.normalize("/aa/b:/c");
		final String normalize2 = FileUtil.normalize(normalize);
		Assertions.assertEquals("/aa/b:/c", normalize);
		Assertions.assertEquals(normalize, normalize2);
	}

	@Test
	public void subPathTest2() {
		String subPath = FileUtil.subPath("d:/aaa/bbb/", "d:/aaa/bbb/ccc/");
		Assertions.assertEquals("ccc/", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb/ccc/");
		Assertions.assertEquals("ccc/", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb/ccc/test.txt");
		Assertions.assertEquals("ccc/test.txt", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb/", "d:/aaa/bbb/ccc");
		Assertions.assertEquals("ccc", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb/ccc");
		Assertions.assertEquals("ccc", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb");
		Assertions.assertEquals("", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb/", "d:/aaa/bbb");
		Assertions.assertEquals("", subPath);
	}

	@Test
	@EnabledForJreRange(max = JRE.JAVA_8)
	public void listFileNamesTest() {
		// JDK9+中，由于模块化问题，获取的classoath路径非项目下，而是junit下的。
		List<String> names = FileUtil.listFileNames("classpath:");
		assertTrue(names.contains("hutool.jpg"));

		names = FileUtil.listFileNames("");
		assertTrue(names.contains("hutool.jpg"));

		names = FileUtil.listFileNames(".");
		assertTrue(names.contains("hutool.jpg"));
	}

	@Test
	@Disabled
	public void listFileNamesInJarTest() {
		final List<String> names = FileUtil.listFileNames("d:/test/hutool-core-5.1.0.jar!/cn/hutool/core/util ");
		for (final String name : names) {
			Console.log(name);
		}
	}

	@Test
	@Disabled
	public void listFileNamesTest2() {
		final List<String> names = FileUtil.listFileNames("D:\\m2_repo\\commons-cli\\commons-cli\\1.0\\commons-cli-1.0.jar!org/mina/commons/cli/");
		for (final String string : names) {
			Console.log(string);
		}
	}

	@Test
	@Disabled
	public void loopFilesTest() {
		final List<File> files = FileUtil.loopFiles("d:/");
		for (final File file : files) {
			Console.log(file.getPath());
		}
	}

	@Test
	@Disabled
	public void loopFileTest() {
		final List<File> files = FileUtil.loopFiles("D:\\m2_repo\\cglib\\cglib\\3.3.0\\cglib-3.3.0.jar");
		Console.log(files);
	}

	@Test
	@Disabled
	public void loopFilesTest2() {
		FileUtil.loopFiles("").forEach(Console::log);
	}

	@Test
	@Disabled
	public void loopFilesWithDepthTest() {
		final List<File> files = FileUtil.loopFiles(FileUtil.file("d:/m2_repo"), 2, null);
		for (final File file : files) {
			Console.log(file.getPath());
		}
	}

	@Test
	public void getParentTest() {
		// 只在Windows下测试
		if (FileUtil.isWindows()) {
			File parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 0);
			Assertions.assertEquals(FileUtil.file("d:\\aaa\\bbb\\cc\\ddd"), parent);

			parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 1);
			Assertions.assertEquals(FileUtil.file("d:\\aaa\\bbb\\cc"), parent);

			parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 2);
			Assertions.assertEquals(FileUtil.file("d:\\aaa\\bbb"), parent);

			parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 4);
			Assertions.assertEquals(FileUtil.file("d:\\"), parent);

			parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 5);
			Assertions.assertNull(parent);

			parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 10);
			Assertions.assertNull(parent);
		}
	}

	@Test
	public void lastIndexOfSeparatorTest() {
		final String dir = "d:\\aaa\\bbb\\cc\\ddd";
		final int index = FileUtil.lastIndexOfSeparator(dir);
		Assertions.assertEquals(13, index);

		final String file = "ddd.jpg";
		final int index2 = FileUtil.lastIndexOfSeparator(file);
		Assertions.assertEquals(-1, index2);
	}

	@Test
	public void getNameTest() {
		String path = "d:\\aaa\\bbb\\cc\\ddd\\";
		String name = FileNameUtil.getName(path);
		Assertions.assertEquals("ddd", name);

		path = "d:\\aaa\\bbb\\cc\\ddd.jpg";
		name = FileNameUtil.getName(path);
		Assertions.assertEquals("ddd.jpg", name);
	}

	@Test
	public void mainNameTest() {
		String path = "d:\\aaa\\bbb\\cc\\ddd\\";
		String mainName = FileNameUtil.mainName(path);
		Assertions.assertEquals("ddd", mainName);

		path = "d:\\aaa\\bbb\\cc\\ddd";
		mainName = FileNameUtil.mainName(path);
		Assertions.assertEquals("ddd", mainName);

		path = "d:\\aaa\\bbb\\cc\\ddd.jpg";
		mainName = FileNameUtil.mainName(path);
		Assertions.assertEquals("ddd", mainName);
	}

	@Test
	public void extNameTest() {
		String path = FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\ddd\\" : "~/Desktop/hutool/ddd/";
		String mainName = FileNameUtil.extName(path);
		Assertions.assertEquals("", mainName);

		path = FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\ddd" : "~/Desktop/hutool/ddd";
		mainName = FileNameUtil.extName(path);
		Assertions.assertEquals("", mainName);

		path = FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\ddd.jpg" : "~/Desktop/hutool/ddd.jpg";
		mainName = FileNameUtil.extName(path);
		Assertions.assertEquals("jpg", mainName);

		path = FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\fff.xlsx" : "~/Desktop/hutool/fff.xlsx";
		mainName = FileNameUtil.extName(path);
		Assertions.assertEquals("xlsx", mainName);

		path = FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\fff.tar.gz" : "~/Desktop/hutool/fff.tar.gz";
		mainName = FileNameUtil.extName(path);
		Assertions.assertEquals("tar.gz", mainName);

		path = FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\fff.tar.Z" : "~/Desktop/hutool/fff.tar.Z";
		mainName = FileNameUtil.extName(path);
		Assertions.assertEquals("tar.Z", mainName);

		path = FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\fff.tar.bz2" : "~/Desktop/hutool/fff.tar.bz2";
		mainName = FileNameUtil.extName(path);
		Assertions.assertEquals("tar.bz2", mainName);

		path = FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\fff.tar.xz" : "~/Desktop/hutool/fff.tar.xz";
		mainName = FileNameUtil.extName(path);
		Assertions.assertEquals("tar.xz", mainName);
	}

	@Test
	@EnabledForJreRange(max = JRE.JAVA_8)
	public void getWebRootTest() {
		// JDK9+环境中，由于模块问题，junit获取的classpath路径和实际不同
		final File webRoot = FileUtil.getWebRoot();
		Assertions.assertNotNull(webRoot);
		Assertions.assertEquals("hutool-core", webRoot.getName());
	}

	@Test
	public void getMimeTypeTest() {
		String mimeType = FileUtil.getMimeType("test2Write.jpg");
		Assertions.assertEquals("image/jpeg", mimeType);

		mimeType = FileUtil.getMimeType("test2Write.html");
		Assertions.assertEquals("text/html", mimeType);

		mimeType = FileUtil.getMimeType("main.css");
		Assertions.assertEquals("text/css", mimeType);

		mimeType = FileUtil.getMimeType("test.js");
		// 在 jdk 11+ 会获取到 text/javascript,而非 自定义的 application/x-javascript
		final List<String> list = ListUtil.of("text/javascript", "application/x-javascript");
		assertTrue(list.contains(mimeType));

		// office03
		mimeType = FileUtil.getMimeType("test.doc");
		Assertions.assertEquals("application/msword", mimeType);
		mimeType = FileUtil.getMimeType("test.xls");
		Assertions.assertEquals("application/vnd.ms-excel", mimeType);
		mimeType = FileUtil.getMimeType("test.ppt");
		Assertions.assertEquals("application/vnd.ms-powerpoint", mimeType);

		// office07+
		mimeType = FileUtil.getMimeType("test.docx");
		Assertions.assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", mimeType);
		mimeType = FileUtil.getMimeType("test.xlsx");
		Assertions.assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", mimeType);
		mimeType = FileUtil.getMimeType("test.pptx");
		Assertions.assertEquals("application/vnd.openxmlformats-officedocument.presentationml.presentation", mimeType);

		// pr#2617@Github
		mimeType = FileUtil.getMimeType("test.wgt");
		Assertions.assertEquals("application/widget", mimeType);

		// issue#3092
		mimeType = FileUtil.getMimeType("https://xxx.oss-cn-hangzhou.aliyuncs.com/xxx.webp");
		Assertions.assertEquals("image/webp", mimeType);
	}

	@Test
	public void isSubTest() {
		final File file = new File("d:/test");
		final File file2 = new File("d:/test2/aaa");
		Assertions.assertFalse(FileUtil.isSub(file, file2));
	}

	@Test
	public void isSubRelativeTest() {
		final File file = new File("..");
		final File file2 = new File(".");
		assertTrue(FileUtil.isSub(file, file2));
	}

	@Test
	@Disabled
	public void appendLinesTest() {
		final List<String> list = ListUtil.of("a", "b", "c");
		FileUtil.appendLines(list, FileUtil.file("d:/test/appendLines.txt"), CharsetUtil.UTF_8);
	}

	@Test
	public void createTempFileTest() {
		final File nullDirTempFile = FileUtil.createTempFile();
		assertTrue(nullDirTempFile.exists());

		final File suffixDirTempFile = FileUtil.createTempFile(".xlsx", true);
		Assertions.assertEquals("xlsx", FileNameUtil.getSuffix(suffixDirTempFile));

		final File prefixDirTempFile = FileUtil.createTempFile("prefix", ".xlsx", true);
		Console.log(prefixDirTempFile);
		assertTrue(FileNameUtil.getPrefix(prefixDirTempFile).startsWith("prefix"));
	}

	@Test
	public void getTotalLinesTest() {
		// 此文件最后一行有换行符，则最后的空行算作一行
		final int totalLines = FileUtil.getTotalLines(FileUtil.file("test_lines.csv"));
		Assertions.assertEquals(8, totalLines);
	}

	@Test
	public void getTotalLinesCrTest() {
		// 此文件最后一行有换行符，则最后的空行算作一行
		final int totalLines = FileUtil.getTotalLines(FileUtil.file("test_lines_cr.csv"));
		assertEquals(8, totalLines);
	}

	@Test
	public void getTotalLinesCrlfTest() {
		// 此文件最后一行有换行符，则最后的空行算作一行
		final int totalLines = FileUtil.getTotalLines(FileUtil.file("test_lines_crlf.csv"));
		assertEquals(8, totalLines);
	}

	@Test
	public void issue3591Test() {
		// 此文件最后一行末尾无换行符
		final int totalLines = FileUtil.getTotalLines(FileUtil.file("1_psi_index_0.txt"));
		assertEquals(11, totalLines);
	}

	@Test
	public void isAbsolutePathTest() {
		String path = "d:/test\\aaa.txt";
		assertTrue(FileUtil.isAbsolutePath(path));

		path = "test\\aaa.txt";
		Assertions.assertFalse(FileUtil.isAbsolutePath(path));
	}

	@Test
	public void smbPathTest() {
		final String smbPath = "\\\\192.168.1.1\\share\\rc-source";
		final String parseSmbPath = FileUtil.getAbsolutePath(smbPath);
		assertEquals(smbPath, parseSmbPath);
		assertTrue(FileUtil.isAbsolutePath(smbPath));
		assertTrue(Paths.get(smbPath).isAbsolute());
	}

	@Test
	@Disabled
	void readBytesTest() {
		final byte[] bytes = FileUtil.readBytes("test.properties");
		Assertions.assertEquals(125, bytes.length);
	}

	@Test
	void checkSlipTest() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			FileUtil.checkSlip(FileUtil.file("test/a"), FileUtil.file("test/../a"));
		});
	}
}
