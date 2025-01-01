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

import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.tree.parser.DefaultNodeParser;
import org.dromara.hutool.core.tree.parser.NodeParser;
import org.dromara.hutool.core.util.ObjUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * 树工具类
 *
 * @author liangbaikai
 */
public class TreeUtil {

	// region ----- build

	/**
	 * 构建单root节点树
	 *
	 * @param list 源数据集合
	 * @return {@link MapTree}
	 * @since 5.7.2
	 */
	public static MapTree<Integer> buildSingle(final Iterable<TreeNode<Integer>> list) {
		return buildSingle(list, 0);
	}

	/**
	 * 树构建
	 *
	 * @param list 源数据集合
	 * @return List
	 */
	public static List<MapTree<Integer>> build(final Iterable<TreeNode<Integer>> list) {
		return build(list, 0);
	}

	/**
	 * 构建单root节点树<br>
	 * 它会生成一个以指定ID为ID的空的节点，然后逐级增加子节点。
	 *
	 * @param <E>      ID类型
	 * @param list     源数据集合
	 * @param parentId 最顶层父id值 一般为 0 之类
	 * @return {@link MapTree}
	 * @since 5.7.2
	 */
	public static <E> MapTree<E> buildSingle(final Iterable<TreeNode<E>> list, final E parentId) {
		return buildSingle(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, new DefaultNodeParser<>());
	}

	/**
	 * 树构建
	 *
	 * @param <E>      ID类型
	 * @param list     源数据集合
	 * @param parentId 最顶层父id值 一般为 0 之类
	 * @return List
	 */
	public static <E> List<MapTree<E>> build(final Iterable<TreeNode<E>> list, final E parentId) {
		return build(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, new DefaultNodeParser<>());
	}

	/**
	 * 构建单root节点树<br>
	 * 它会将指定Id的节点作为根节点，如果这个节点不存在，则创建一个空节点，然后逐级增加子节点。
	 *
	 * @param <T>        转换的实体 为数据源里的对象类型
	 * @param <E>        ID类型
	 * @param list       源数据集合
	 * @param parentId   最顶层父id值 一般为 0 之类
	 * @param nodeParser 转换器
	 * @return {@link MapTree}
	 * @since 5.7.2
	 */
	public static <T, E> MapTree<E> buildSingle(final Iterable<T> list, final E parentId, final NodeParser<T, E> nodeParser) {
		return buildSingle(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, nodeParser);
	}

	/**
	 * 树构建<br>
	 * 你所有节点的ID都不应该重复，那你要构建一个列表形式的树结构，指定的这个rootId应该是首层节点的parentId，而非某个节点的id
	 *
	 * @param <T>        转换的实体 为数据源里的对象类型
	 * @param <E>        ID类型
	 * @param list       源数据集合
	 * @param parentId   最顶层父id值 一般为 0 之类
	 * @param nodeParser 转换器
	 * @return List
	 */
	public static <T, E> List<MapTree<E>> build(final Iterable<T> list, final E parentId, final NodeParser<T, E> nodeParser) {
		return build(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, nodeParser);
	}

	/**
	 * 树构建<br>
	 * 你所有节点的ID都不应该重复，那你要构建一个列表形式的树结构，指定的这个rootId应该是首层节点的parentId，而非某个节点的id
	 *
	 * @param <T>            转换的实体 为数据源里的对象类型
	 * @param <E>            ID类型
	 * @param list           源数据集合
	 * @param rootId         最顶层父id值 一般为 0 之类
	 * @param treeNodeConfig 配置
	 * @param nodeParser     转换器
	 * @return List
	 */
	public static <T, E> List<MapTree<E>> build(final Iterable<T> list, final E rootId, final TreeNodeConfig treeNodeConfig, final NodeParser<T, E> nodeParser) {
		return buildSingle(list, rootId, treeNodeConfig, nodeParser).getChildren();
	}

	/**
	 * 构建单root节点树<br>
	 * 它会生成一个以指定ID为ID的空的节点，然后逐级增加子节点。
	 *
	 * @param <T>            转换的实体 为数据源里的对象类型
	 * @param <E>            ID类型
	 * @param list           源数据集合
	 * @param rootId         最顶层父id值 一般为 0 之类
	 * @param treeNodeConfig 配置
	 * @param nodeParser     转换器
	 * @return {@link MapTree}
	 * @since 5.7.2
	 */
	public static <T, E> MapTree<E> buildSingle(final Iterable<T> list, final E rootId, final TreeNodeConfig treeNodeConfig, final NodeParser<T, E> nodeParser) {
		return TreeBuilder.of(rootId, treeNodeConfig)
			.append(list, nodeParser).build();
	}

	/**
	 * 树构建，按照权重排序
	 *
	 * @param <E>    ID类型
	 * @param map    源数据Map
	 * @param rootId 最顶层父id值 一般为 0 之类
	 * @return List
	 * @since 5.6.7
	 */
	public static <E> List<MapTree<E>> build(final Map<E, MapTree<E>> map, final E rootId) {
		return buildSingle(map, rootId).getChildren();
	}

	/**
	 * 单点树构建，按照权重排序<br>
	 * 它会生成一个以指定ID为ID的空的节点，然后逐级增加子节点。
	 *
	 * @param <E>    ID类型
	 * @param map    源数据Map
	 * @param rootId 根节点id值 一般为 0 之类
	 * @return {@link MapTree}
	 * @since 5.7.2
	 */
	public static <E> MapTree<E> buildSingle(final Map<E, MapTree<E>> map, final E rootId) {
		final MapTree<E> tree = CollUtil.getFirstNoneNull(map.values());
		if (null != tree) {
			final TreeNodeConfig config = tree.getConfig();
			return TreeBuilder.of(rootId, config)
				.append(map)
				.build();
		}

		return createEmptyNode(rootId);
	}
	// endregion

	/**
	 * 获取ID对应的节点，如果有多个ID相同的节点，只返回第一个。<br>
	 * 此方法只查找此节点及子节点，采用递归深度优先遍历。
	 *
	 * @param <T>  ID类型
	 * @param node 节点
	 * @param id   ID
	 * @return 节点
	 * @since 5.2.4
	 */
	public static <T> MapTree<T> getNode(final MapTree<T> node, final T id) {
		if (ObjUtil.equals(id, node.getId())) {
			return node;
		}

		final List<MapTree<T>> children = node.getChildren();
		if (null == children) {
			return null;
		}

		// 查找子节点
		MapTree<T> childNode;
		for (final MapTree<T> child : children) {
			childNode = child.getNode(id);
			if (null != childNode) {
				return childNode;
			}
		}

		// 未找到节点
		return null;
	}

	/**
	 * 获取所有父节点名称列表
	 *
	 * <p>
	 * 比如有个人在研发1部，他上面有研发部，接着上面有技术中心<br>
	 * 返回结果就是：[研发一部, 研发中心, 技术中心]
	 *
	 * @param <T>                节点ID类型
	 * @param node               节点
	 * @param includeCurrentNode 是否包含当前节点的名称
	 * @return 所有父节点名称列表，node为null返回空List
	 * @since 5.2.4
	 */
	public static <T> List<CharSequence> getParentsName(final MapTree<T> node, final boolean includeCurrentNode) {
		return getParents(node, includeCurrentNode, MapTree::getName);
	}

	/**
	 * 获取所有父节点ID列表
	 *
	 * <p>
	 * 比如有个人在研发1部，他上面有研发部，接着上面有技术中心<br>
	 * 返回结果就是：[研发部, 技术中心]
	 *
	 * @param <T>                节点ID类型
	 * @param node               节点
	 * @param includeCurrentNode 是否包含当前节点的名称
	 * @return 所有父节点ID列表，node为null返回空List
	 * @since 5.8.22
	 */
	public static <T> List<T> getParentsId(final MapTree<T> node, final boolean includeCurrentNode) {
		return getParents(node, includeCurrentNode, MapTree::getId);
	}

	/**
	 * 获取所有父节点指定函数结果列表
	 *
	 * @param <T>                节点ID类型
	 * @param <E>                字段值类型
	 * @param node               节点
	 * @param includeCurrentNode 是否包含当前节点的名称
	 * @param fieldFunc          获取父节点名称的函数
	 * @return 所有父节点字段值列表，node为null返回空List
	 * @since 6.0.0
	 */
	public static <T, E> List<E> getParents(final MapTree<T> node, final boolean includeCurrentNode, final Function<MapTree<T>, E> fieldFunc) {
		final List<E> result = new ArrayList<>();
		if (null == node) {
			return result;
		}

		if (includeCurrentNode) {
			result.add(fieldFunc.apply(node));
		}

		MapTree<T> parent = node.getParent();
		E fieldValue;
		while (null != parent) {
			fieldValue = fieldFunc.apply(parent);
			parent = parent.getParent();
			if (null != fieldValue || null != parent) {
				// issue#I795IN，根节点的null不加入
				result.add(fieldValue);
			}
		}
		return result;
	}

	/**
	 * 获取所有父节点ID列表
	 *
	 * <p>
	 * 比如有个人在研发1部，他上面有研发部，接着上面有技术中心<br>
	 * parent = parent.getParent();) {
	 * if(null != id || null != parent){
	 * // issue#I795IN，根节点的null不加入
	 * result.add(fieldFunc.apply(parent));
	 * }
	 * }
	 * return result;
	 * }
	 * <p>
	 * /**
	 * 创建空Tree的节点
	 *
	 * @param id  节点ID
	 * @param <E> 节点ID类型
	 * @return {@link MapTree}
	 * @since 5.7.2
	 */
	public static <E> MapTree<E> createEmptyNode(final E id) {
		return new MapTree<E>().setId(id);
	}

	/**
	 * 深度优先,遍历树,将树换为数组
	 *
	 * @param root       树的根节点
	 * @param broadFirst 是否广度优先遍历
	 * @param <E>        节点ID类型
	 * @return 树所有节点列表
	 */
	public static <E> List<MapTree<E>> toList(final MapTree<E> root, final boolean broadFirst) {
		if (Objects.isNull(root)) {
			return null;
		}
		final List<MapTree<E>> list = new ArrayList<>();
		root.walk(list::add, broadFirst);

		return list;
	}
}
