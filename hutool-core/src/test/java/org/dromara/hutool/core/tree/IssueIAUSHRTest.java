/*
 * Copyright (c) 2025 Hutool Team and hutool.cn
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

package org.dromara.hutool.core.tree;

import lombok.Data;
import org.dromara.hutool.core.tree.parser.NodeParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IssueIAUSHRTest {

	private final NodeParser<TestDept, Long> nodeParser = (dept, tree) ->
		tree.setId(dept.getDeptId())
			.setParentId(dept.getParentId())
			.setName(dept.getDeptName());

	private final TreeNodeConfig treeNodeConfig = new TreeNodeConfig()
		.setIdKey("deptId")
		.setNameKey("deptName")
		.setParentIdKey("parentId");

	/**
	 * 理论上，你所有节点的ID都不应该重复，那你要构建一个列表形式的树结构，指定的这个rootId应该是A节点的parentId，而非A的id
	 */
	@Test
	void buildTest() {
		final List<TestDept> list = mockData();

		final List<MapTree<Long>> build = TreeUtil.build(list, null, treeNodeConfig, nodeParser);
		Assertions.assertEquals(1, build.size());
		assertEquals("A", build.get(0).getName());
		assertEquals(2, build.get(0).getChildren().size());
		assertEquals("B", build.get(0).getChildren().get(0).getName());
		assertEquals("C", build.get(0).getChildren().get(1).getName());
	}

	/**
	 * 构建单节点时，如果指定的节点存在，则这个节点作为根节点
	 */
	@Test
	void buildSingleTest() {
		final List<TestDept> list = mockData();

		final MapTree<Long> longTree = TreeUtil.buildSingle(list, 1L, treeNodeConfig, nodeParser);

		assertEquals("A", longTree.getName());
		assertEquals(2, longTree.getChildren().size());
		assertEquals("B", longTree.getChildren().get(0).getName());
		assertEquals("C", longTree.getChildren().get(1).getName());
	}

	private List<TestDept> mockData() {
		final List<TestDept> list = new ArrayList<>();
		TestDept sysDept = new TestDept();
		sysDept.setDeptId(1L);
		sysDept.setDeptName("A");
		sysDept.setParentId(null);
		list.add(sysDept);

		sysDept = new TestDept();
		sysDept.setDeptId(2L);
		sysDept.setDeptName("B");
		sysDept.setParentId(1L);
		list.add(sysDept);

		sysDept = new TestDept();
		sysDept.setDeptId(3L);
		sysDept.setDeptName("C");
		sysDept.setParentId(1L);
		list.add(sysDept);

		return list;
	}

	@Data
	static class TestDept {
		private Long deptId;
		private String deptName;
		private Long parentId;
	}
}
