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

package org.dromara.hutool.core.tree;

import lombok.Data;
import org.dromara.hutool.core.collection.ListUtil;
import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.core.tree.parser.DefaultNodeParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用树测试
 *
 * @author liangbaikai
 */
public class TreeTest {
	// 模拟数据
	static List<TreeNode<String>> nodeList = ListUtil.of();

	static {
		// 模拟数据
		nodeList.add(new TreeNode<>("1", "0", "系统管理", 5));
		nodeList.add(new TreeNode<>("111", "11", "用户添加", 0));
		nodeList.add(new TreeNode<>("11", "1", "用户管理", 222222));

		nodeList.add(new TreeNode<>("2", "0", "店铺管理", 1));
		nodeList.add(new TreeNode<>("21", "2", "商品管理", 44));
		nodeList.add(new TreeNode<>("221", "2", "商品管理2", 2));
	}


	@Test
	public void sampleTreeTest() {
		final List<MapTree<String>> treeList = TreeUtil.build(nodeList, "0");
		for (final MapTree<String> tree : treeList) {
			Assertions.assertNotNull(tree);
			Assertions.assertEquals("0", tree.getParentId());
//			Console.log(tree);
		}

		// 测试通过子节点查找父节点
		final MapTree<String> rootNode0 = treeList.get(0);
		final MapTree<String> parent = rootNode0.getChildren().get(0).getParent();
		Assertions.assertEquals(rootNode0, parent);
	}

	@Test
	public void treeTest() {

		// 配置
		final TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
		// 自定义属性名 都要默认值的
		treeNodeConfig.setWeightKey("order");
		treeNodeConfig.setIdKey("rid");
		treeNodeConfig.setDeep(2);

		// 转换器
		final List<MapTree<String>> treeNodes = TreeUtil.build(nodeList, "0", treeNodeConfig,
			(treeNode, tree) -> {
				tree.setId(treeNode.getId());
				tree.setParentId(treeNode.getParentId());
				tree.setWeight(treeNode.getWeight());
				tree.setName(treeNode.getName());
				// 扩展属性 ...
				tree.putExtra("extraField", 666);
				tree.putExtra("other", new Object());
			});

		Assertions.assertEquals(treeNodes.size(), 2);
	}

	@Test
	public void walkTest() {
		final List<String> ids = new ArrayList<>();
		final MapTree<String> tree = TreeUtil.buildSingle(nodeList, "0");
		tree.walk((tr) -> ids.add(tr.getId()));

		Assertions.assertEquals(7, ids.size());
	}

	@Test
	public void walkBroadFirstTest() {
		final List<String> ids = new ArrayList<>();
		final MapTree<String> tree = TreeUtil.buildSingle(nodeList, "0");
		Console.log(tree);
		tree.walk((tr) -> ids.add(tr.getId()), true);

		Assertions.assertEquals(7, ids.size());
	}

	@Test
	public void cloneTreeTest() {
		final MapTree<String> tree = TreeUtil.buildSingle(nodeList, "0");
		final MapTree<String> cloneTree = tree.cloneTree();

		final List<String> ids = new ArrayList<>();
		cloneTree.walk((tr) -> ids.add(tr.getId()));

		Assertions.assertEquals(7, ids.size());
	}

	@Test
	public void filterTest() {
		// 经过过滤，丢掉"用户添加"节点
		final MapTree<String> tree = TreeUtil.buildSingle(nodeList, "0");
		tree.filter((t) -> {
			final CharSequence name = t.getName();
			return null != name && name.toString().contains("店铺");
		});

		final List<String> ids = new ArrayList<>();
		tree.walk((tr) -> ids.add(tr.getId()));
		Assertions.assertEquals(4, ids.size());
	}

	@Test
	public void filterNewTest() {
		final MapTree<String> tree = TreeUtil.buildSingle(nodeList, "0");

		// 经过过滤，生成新的树
		final MapTree<String> newTree = tree.filterNew((t) -> {
			final CharSequence name = t.getName();
			return null != name && name.toString().contains("店铺");
		});

		final List<String> ids = new ArrayList<>();
		newTree.walk((tr) -> ids.add(tr.getId()));
		Assertions.assertEquals(4, ids.size());

		final List<String> ids2 = new ArrayList<>();
		tree.walk((tr) -> ids2.add(tr.getId()));
		Assertions.assertEquals(7, ids2.size());
	}

	/**
	 * https://gitee.com/dromara/hutool/pulls/1248/
	 */
	@Test
	public void lambdaConfigTest() {
		// 配置自定义属性名 为null则取默认值
		final LambdaTreeNodeConfig<CustomTreeNode, String> treeNodeConfig = new LambdaTreeNodeConfig<>();
		treeNodeConfig.setChildrenKeyFun(CustomTreeNode::getChildrenNodes);
		treeNodeConfig.setIdKeyFun(CustomTreeNode::getNodeId);
		treeNodeConfig.setNameKeyFun(CustomTreeNode::getLabel);
		treeNodeConfig.setParentIdKeyFun(CustomTreeNode::getParentNodeId);
		treeNodeConfig.setWeightKeyFun(CustomTreeNode::getSortNo);
		// 最大递归深度
		treeNodeConfig.setDeep(3);

		final List<MapTree<String>> treeNodes = TreeUtil.build(nodeList, "0", treeNodeConfig, new DefaultNodeParser<>());
		Assertions.assertEquals(treeNodes.size(), 2);
		final MapTree<String> treeNode1 = treeNodes.get(1);
		Assertions.assertNotNull(treeNode1);
		Assertions.assertNotNull(treeNode1.getConfig());
		Assertions.assertEquals(treeNode1.getChildren().size(), 1);
	}

	/**
	 * 自定义工程树节点对象
	 *
	 * @author Earlman
	 */
	@Data
	static class CustomTreeNode {
		// 主键ID
		private String nodeId;
		// 节点名称
		private String label;
		// 父级id
		private String parentNodeId;
		// 排序字段
		private Integer sortNo;
		// 子节点
		private List<CustomTreeNode> childrenNodes;
	}
}
