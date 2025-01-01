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

package org.dromara.hutool.core.array;

import org.dromara.hutool.core.collection.ListUtil;
import org.dromara.hutool.core.collection.set.SetUtil;
import org.dromara.hutool.core.util.CharsetUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ArrayUtil} 数组工具单元测试
 *
 * @author Looly
 */
public class ArrayUtilTest {

	@SuppressWarnings({"DataFlowIssue", "ConstantValue"})
	@Test
	public void isEmptyTest() {
		final int[] a = {};
		Assertions.assertTrue(ArrayUtil.isEmpty(a));
		Assertions.assertTrue(ArrayUtil.isEmpty((Object) a));
		final int[] b = null;
		Assertions.assertTrue(ArrayUtil.isEmpty(b));
		final Object c = null;
		Assertions.assertTrue(ArrayUtil.isEmpty(c));

		Object d = new Object[]{"1", "2", 3, 4D};
		boolean isEmpty = ArrayUtil.isEmpty(d);
		Assertions.assertFalse(isEmpty);
		d = new Object[0];
		isEmpty = ArrayUtil.isEmpty(d);
		Assertions.assertTrue(isEmpty);
		d = null;
		isEmpty = ArrayUtil.isEmpty(d);
		Assertions.assertTrue(isEmpty);

		// Object数组
		final Object[] e = new Object[]{"1", "2", 3, 4D};
		final boolean empty = ArrayUtil.isEmpty(e);
		Assertions.assertFalse(empty);

		// 当这个对象并非数组对象且非`null`时，返回`false`，即当用户传入非数组对象，理解为单个元素的数组。
		final Object nonArrayObj = "a" ;
		Assertions.assertFalse(ArrayUtil.isEmpty(nonArrayObj));
	}

	@Test
	public void isNotEmptyTest() {
		final int[] a = {1, 2};
		Assertions.assertTrue(ArrayUtil.isNotEmpty(a));

		final String[] b = {"a", "b", "c"};
		Assertions.assertTrue(ArrayUtil.isNotEmpty(b));

		final Object c = new Object[]{"1", "2", 3, 4D};
		Assertions.assertTrue(ArrayUtil.isNotEmpty(c));
	}

	@Test
	public void newArrayTest() {
		final String[] newArray = ArrayUtil.newArray(String.class, 3);
		assertEquals(3, newArray.length);

		final Object[] newArray2 = ArrayUtil.newArray(3);
		assertEquals(3, newArray2.length);
	}

	@Test
	public void cloneTest() {
		final Integer[] b = {1, 2, 3};
		final Integer[] cloneB = ArrayUtil.clone(b);
		assertArrayEquals(b, cloneB);

		final int[] a = {1, 2, 3};
		final int[] clone = ArrayUtil.clone(a);
		assertArrayEquals(a, clone);

		final int[] clone1 = a.clone();
		assertArrayEquals(a, clone1);
	}

	@Test
	public void filterEditTest() {
		final Integer[] a = {1, 2, 3, 4, 5, 6};
		final Integer[] filter = ArrayUtil.edit(a, t -> (t % 2 == 0) ? t : null);
		assertArrayEquals(filter, new Integer[]{2, 4, 6});
	}

	@Test
	public void filterTestForFilter() {
		final Integer[] a = {1, 2, 3, 4, 5, 6};
		final Integer[] filter = ArrayUtil.filter(a, t -> t % 2 == 0);
		assertArrayEquals(filter, new Integer[]{2, 4, 6});
	}

	@Test
	public void editTest() {
		final Integer[] a = {1, 2, 3, 4, 5, 6};
		final Integer[] filter = ArrayUtil.edit(a, t -> (t % 2 == 0) ? t * 10 : t);
		assertArrayEquals(filter, new Integer[]{1, 20, 3, 40, 5, 60});
	}

	@Test
	public void indexOfTest() {
		final Integer[] a = {1, 2, 3, 4, 5, 6};
		final int index = ArrayUtil.indexOf(a, 3);
		assertEquals(2, index);

		final long[] b = {1, 2, 3, 4, 5, 6};
		final int index2 = ArrayUtil.indexOf(b, 3);
		assertEquals(2, index2);
	}

	@Test
	public void lastIndexOfTest() {
		final Integer[] a = {1, 2, 3, 4, 3, 6};
		final int index = ArrayUtil.lastIndexOf(a, 3);
		assertEquals(4, index);

		final long[] b = {1, 2, 3, 4, 3, 6};
		final int index2 = ArrayUtil.lastIndexOf(b, 3);
		assertEquals(4, index2);
	}

	@Test
	public void containsTest() {
		final Integer[] a = {1, 2, 3, 4, 3, 6};
		final boolean contains = ArrayUtil.contains(a, 3);
		Assertions.assertTrue(contains);

		final long[] b = {1, 2, 3, 4, 3, 6};
		final boolean contains2 = ArrayUtil.contains(b, 3);
		Assertions.assertTrue(contains2);
	}

	@Test
	public void containsAnyTest() {
		final Integer[] a = {1, 2, 3, 4, 3, 6};
		boolean contains = ArrayUtil.containsAny(a, 4, 10, 40);
		Assertions.assertTrue(contains);

		contains = ArrayUtil.containsAny(a, 10, 40);
		Assertions.assertFalse(contains);
	}

	@Test
	public void containsAllTest() {
		final Integer[] a = {1, 2, 3, 4, 3, 6};
		boolean contains = ArrayUtil.containsAll(a, 4, 2, 6);
		// 提供的可变参数中元素顺序不需要一致
		Assertions.assertTrue(contains);

		contains = ArrayUtil.containsAll(a, 1, 2, 3, 5);
		Assertions.assertFalse(contains);
	}

	@Test
	void containsIgnoreCaseTest() {
		final String[] keys = {"a", "B", "c"};
		final boolean b = ArrayUtil.containsIgnoreCase(keys, "b");
		Assertions.assertTrue(b);
	}

	@Test
	public void testContainsIgnoreCaseWithEmptyArray() {
		final CharSequence[] array = new CharSequence[0];
		final CharSequence value = "test" ;
		final boolean result = ArrayUtil.containsIgnoreCase(array, value);
		Assertions.assertFalse(result, "Expected the result to be false for an empty array.");
	}

	@Test
	public void testContainsIgnoreCaseWithNullValue() {
		final CharSequence[] array = {"Hello", "World"};
		final CharSequence value = null;
		final boolean result = ArrayUtil.containsIgnoreCase(array, value);
		Assertions.assertFalse(result, "Expected the result to be false when the value is null.");
	}

	@Test
	public void testContainsIgnoreCaseWithExistingValue() {
		final CharSequence[] array = {"Hello", "World"};
		final CharSequence value = "world" ;
		final boolean result = ArrayUtil.containsIgnoreCase(array, value);
		Assertions.assertTrue(result, "Expected the result to be true when the value exists in the array.");
	}

	@Test
	public void testContainsIgnoreCaseWithNonExistingValue() {
		final CharSequence[] array = {"Hello", "World"};
		final CharSequence value = "Java" ;
		final boolean result = ArrayUtil.containsIgnoreCase(array, value);
		Assertions.assertFalse(result, "Expected the result to be false when the value does not exist in the array.");
	}

	@Test
	public void testContainsIgnoreCaseWithCaseSensitiveValue() {
		final CharSequence[] array = {"Hello", "World"};
		final CharSequence value = "HELLO" ;
		final boolean result = ArrayUtil.containsIgnoreCase(array, value);
		Assertions.assertTrue(result, "Expected the result to be true when the value exists in the array with different case sensitivity.");
	}

	@Test
	public void zipTest() {
		final String[] keys = {"a", "b", "c"};
		final Integer[] values = {1, 2, 3};
		final Map<String, Integer> map = ArrayUtil.zip(keys, values, true);
		assertEquals(Objects.requireNonNull(map).toString(), "{a=1, b=2, c=3}");
	}

	@Test
	void mapTest() {
		final int[] a = {1, 2, 3};
		final Integer[] map = ArrayUtil.map(a, Integer.class, t -> (int) t * 2);
		assertEquals("[2, 4, 6]", ArrayUtil.toString(map));
	}

	@Test
	public void mapToArrayTest() {
		final String[] keys = {"a", "b", "c"};
		final Integer[] integers = ArrayUtil.mapToArray(keys, String::length, Integer[]::new);
		assertArrayEquals(new Integer[]{1, 1, 1}, integers);
	}

	@Test
	public void mapToListTest() {
		final String[] keys = {"a", "b", "c"};
		final List<Integer> integers = ArrayUtil.mapToList(keys, String::length);
		assertEquals(ListUtil.of(1, 1, 1), integers);
	}

	@Test
	public void mapToSetTest() {
		final String[] keys = {"a", "b", "c"};
		final Set<Integer> integers = ArrayUtil.mapToSet(keys, String::length);
		assertEquals(SetUtil.of(1), integers);
	}

	@Test
	public void castTest() {
		final Object[] values = {"1", "2", "3"};
		final String[] cast = (String[]) ArrayUtil.cast(String.class, values);
		assertEquals(values[0], cast[0]);
		assertEquals(values[1], cast[1]);
		assertEquals(values[2], cast[2]);
	}

	@Test
	public void maxTest() {
		final int max = ArrayUtil.max(1, 2, 13, 4, 5);
		assertEquals(13, max);

		final long maxLong = ArrayUtil.max(1L, 2L, 13L, 4L, 5L);
		assertEquals(13, maxLong);

		final double maxDouble = ArrayUtil.max(1D, 2.4D, 13.0D, 4.55D, 5D);
		assertEquals(13.0, maxDouble, 0);

		final BigDecimal one = new BigDecimal("1.00");
		final BigDecimal two = new BigDecimal("2.0");
		final BigDecimal three = new BigDecimal("3");
		final BigDecimal[] bigDecimals = {two, one, three};

		final BigDecimal minAccuracy = ArrayUtil.min(bigDecimals, Comparator.comparingInt(BigDecimal::scale));
		assertEquals(minAccuracy, three);

		final BigDecimal maxAccuracy = ArrayUtil.max(bigDecimals, Comparator.comparingInt(BigDecimal::scale));
		assertEquals(maxAccuracy, one);
	}

	@Test
	public void minTest() {
		final int min = ArrayUtil.min(1, 2, 13, 4, 5);
		assertEquals(1, min);

		final long minLong = ArrayUtil.min(1L, 2L, 13L, 4L, 5L);
		assertEquals(1, minLong);

		final double minDouble = ArrayUtil.min(1D, 2.4D, 13.0D, 4.55D, 5D);
		assertEquals(1.0, minDouble, 0);
	}

	@Test
	public void appendTest() {
		final String[] a = {"1", "2", "3", "4"};
		final String[] b = {"a", "b", "c"};

		final String[] result = ArrayUtil.append(a, b);
		assertArrayEquals(new String[]{"1", "2", "3", "4", "a", "b", "c"}, result);
	}

	@Test
	public void appendTest2() {
		final String[] a = {"1", "2", "3", "4"};

		final String[] result = ArrayUtil.append(a, "a", "b", "c");
		assertArrayEquals(new String[]{"1", "2", "3", "4", "a", "b", "c"}, result);
	}

	@Test
	public void insertTest() {
		final String[] a = {"1", "2", "3", "4"};
		final String[] b = {"a", "b", "c"};

		// 在-1的位置插入，相当于在3的位置插入
		String[] result = ArrayUtil.insert(a, -1, b);
		assertArrayEquals(new String[]{"1", "2", "3", "a", "b", "c", "4"}, result);

		// 在第0个位置插入，即在数组前追加
		result = ArrayUtil.insert(a, 0, b);
		assertArrayEquals(new String[]{"a", "b", "c", "1", "2", "3", "4"}, result);

		// 在第2个位置插入，即"3"之前
		result = ArrayUtil.insert(a, 2, b);
		assertArrayEquals(new String[]{"1", "2", "a", "b", "c", "3", "4"}, result);

		// 在第4个位置插入，即"4"之后，相当于追加
		result = ArrayUtil.insert(a, 4, b);
		assertArrayEquals(new String[]{"1", "2", "3", "4", "a", "b", "c"}, result);

		// 在第5个位置插入，由于数组长度为4，因此补充null
		result = ArrayUtil.insert(a, 5, b);
		assertArrayEquals(new String[]{"1", "2", "3", "4", null, "a", "b", "c"}, result);
	}

	@Test
	public void joinTest() {
		final String[] array = {"aa", "bb", "cc", "dd"};
		final String join = ArrayUtil.join(array, ",", "[", "]");
		assertEquals("[aa],[bb],[cc],[dd]", join);

		final Object array2 = new String[]{"aa", "bb", "cc", "dd"};
		final String join2 = ArrayUtil.join(array2, ",");
		assertEquals("aa,bb,cc,dd", join2);
	}

	@Test
	public void testJoinWithNullElement() {
		final String[] array = {"Java", null, "Python"};
		final String result = ArrayUtil.join(array, ", ", value -> value == null ? "null" : value);
		assertEquals("Java, null, Python", result);
	}

	@Test
	public void testJoinWithEmptyArray() {
		final String[] array = {};
		final String result = ArrayUtil.join(array, ", ", String::toUpperCase);
		assertEquals("", result);
	}

	@Test
	public void testJoinWithoutEditor() {
		final Integer[] array = {1, 2, 3};
		final String result = ArrayUtil.join(array, ", ");
		assertEquals("1, 2, 3", result);
	}

	@Test
	public void testJoinWithEditor() {
		final String[] array = {"java", "scala", "kotlin"};
		final String result = ArrayUtil.join(array, " -> ", String::toUpperCase);
		assertEquals("JAVA -> SCALA -> KOTLIN", result);
	}

	@Test
	public void testJoinWithNullConjunction() {
		final String[] array = {"one", "two", "three"};
		final String result = ArrayUtil.join(array, null, value -> value + "!");
		assertEquals("one!two!three!", result);
	}

	@Test
	public void getArrayTypeTest() {
		Class<?> arrayType = ArrayUtil.getArrayType(int.class);
		Assertions.assertSame(int[].class, arrayType);

		arrayType = ArrayUtil.getArrayType(String.class);
		Assertions.assertSame(String[].class, arrayType);
	}

	@Test
	public void distinctTest() {
		final String[] array = {"aa", "bb", "cc", "dd", "bb", "dd"};
		final String[] distinct = ArrayUtil.distinct(array);
		assertArrayEquals(new String[]{"aa", "bb", "cc", "dd"}, distinct);
	}

	@Test
	public void distinctByFunctionTest() {
		final String[] array = {"aa", "Aa", "BB", "bb"};

		// 覆盖模式下，保留最后加入的两个元素
		String[] distinct = ArrayUtil.distinct(array, String::toLowerCase, true);
		assertArrayEquals(new String[]{"Aa", "bb"}, distinct);

		// 忽略模式下，保留最早加入的两个元素
		distinct = ArrayUtil.distinct(array, String::toLowerCase, false);
		assertArrayEquals(new String[]{"aa", "BB"}, distinct);
	}

	@Test
	public void toStingTest() {
		final int[] a = {1, 3, 56, 6, 7};
		assertEquals("[1, 3, 56, 6, 7]", ArrayUtil.toString(a));
		final long[] b = {1, 3, 56, 6, 7};
		assertEquals("[1, 3, 56, 6, 7]", ArrayUtil.toString(b));
		final short[] c = {1, 3, 56, 6, 7};
		assertEquals("[1, 3, 56, 6, 7]", ArrayUtil.toString(c));
		final double[] d = {1, 3, 56, 6, 7};
		assertEquals("[1.0, 3.0, 56.0, 6.0, 7.0]", ArrayUtil.toString(d));
		final byte[] e = {1, 3, 56, 6, 7};
		assertEquals("[1, 3, 56, 6, 7]", ArrayUtil.toString(e));
		final boolean[] f = {true, false, true, true, true};
		assertEquals("[true, false, true, true, true]", ArrayUtil.toString(f));
		final float[] g = {1, 3, 56, 6, 7};
		assertEquals("[1.0, 3.0, 56.0, 6.0, 7.0]", ArrayUtil.toString(g));
		final char[] h = {'a', 'b', '你', '好', '1'};
		assertEquals("[a, b, 你, 好, 1]", ArrayUtil.toString(h));

		final String[] array = {"aa", "bb", "cc", "dd", "bb", "dd"};
		assertEquals("[aa, bb, cc, dd, bb, dd]", ArrayUtil.toString(array));
	}

	@Test
	public void toArrayTest() {
		final List<String> list = ListUtil.of("A", "B", "C", "D");
		final String[] array = ArrayUtil.ofArray(list, String.class);
		assertEquals("A", array[0]);
		assertEquals("B", array[1]);
		assertEquals("C", array[2]);
		assertEquals("D", array[3]);
	}

	@Test
	public void addAllTest() {
		final int[] ints = ArrayUtil.addAll(new int[]{1, 2, 3}, new int[]{4, 5, 6});
		assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6}, ints);
	}

	@Test
	public void isAllNotNullTest() {
		final String[] allNotNull = {"aa", "bb", "cc", "dd", "bb", "dd"};
		Assertions.assertTrue(ArrayUtil.isAllNotNull(allNotNull));
		final String[] hasNull = {"aa", "bb", "cc", null, "bb", "dd"};
		Assertions.assertFalse(ArrayUtil.isAllNotNull(hasNull));
	}

	@Test
	void firstNonNullTest() {
		final String[] a = {null, null, "cc", null, "bb", "dd"};
		final String s = ArrayUtil.firstNonNull(a);
		assertEquals("cc", s);
	}

	@Test
	public void indexOfSubTest() {
		final Integer[] a = {0x12, 0x34, 0x56, 0x78, 0x9A};
		final Integer[] b = {0x56, 0x78};
		final Integer[] c = {0x12, 0x56};
		final Integer[] d = {0x78, 0x9A};
		final Integer[] e = {0x78, 0x9A, 0x10};

		int i = ArrayUtil.indexOfSub(a, b);
		assertEquals(2, i);

		i = ArrayUtil.indexOfSub(a, c);
		assertEquals(-1, i);

		i = ArrayUtil.indexOfSub(a, d);
		assertEquals(3, i);

		i = ArrayUtil.indexOfSub(a, e);
		assertEquals(-1, i);

		i = ArrayUtil.indexOfSub(a, null);
		assertEquals(-1, i);

		i = ArrayUtil.indexOfSub(null, null);
		assertEquals(-1, i);

		i = ArrayUtil.indexOfSub(null, b);
		assertEquals(-1, i);
	}

	@Test
	public void indexOfSubTest2() {
		final Integer[] a = {0x12, 0x56, 0x34, 0x56, 0x78, 0x9A};
		final Integer[] b = {0x56, 0x78};
		final int i = ArrayUtil.indexOfSub(a, b);
		assertEquals(3, i);
	}

	@Test
	public void lastIndexOfSubTest() {
		final Integer[] a = {0x12, 0x34, 0x56, 0x78, 0x9A};
		final Integer[] b = {0x56, 0x78};
		final Integer[] c = {0x12, 0x56};
		final Integer[] d = {0x78, 0x9A};
		final Integer[] e = {0x78, 0x9A, 0x10};

		int i = ArrayUtil.lastIndexOfSub(a, b);
		assertEquals(2, i);

		i = ArrayUtil.lastIndexOfSub(a, c);
		assertEquals(-1, i);

		i = ArrayUtil.lastIndexOfSub(a, d);
		assertEquals(3, i);

		i = ArrayUtil.lastIndexOfSub(a, e);
		assertEquals(-1, i);

		i = ArrayUtil.lastIndexOfSub(a, null);
		assertEquals(-1, i);

		i = ArrayUtil.lastIndexOfSub(null, null);
		assertEquals(-1, i);

		i = ArrayUtil.lastIndexOfSub(null, b);
		assertEquals(-1, i);
	}

	@Test
	public void lastIndexOfSubTest2() {
		final Integer[] a = {0x12, 0x56, 0x78, 0x56, 0x21, 0x9A};
		final Integer[] b = {0x56, 0x78};
		final int i = ArrayUtil.lastIndexOfSub(a, b);
		assertEquals(1, i);
	}

	@Test
	public void reverseTest() {
		final int[] a = {1, 2, 3, 4};
		final int[] reverse = ArrayUtil.reverse(a);
		assertArrayEquals(new int[]{4, 3, 2, 1}, reverse);
	}

	@Test
	public void reverseTest2() {
		final Object[] a = {"1", '2', "3", 4};
		final Object[] reverse = ArrayUtil.reverse(a);
		assertArrayEquals(new Object[]{4, "3", '2', "1"}, reverse);
	}

	@Test
	void testRemoveWithValidIndex() {
		final String[] array = {"a", "b", "c", "d"};
		final String[] result = ArrayUtil.remove(array, 1);
		assertArrayEquals(new String[]{"a", "c", "d"}, result);
	}

	@Test
	void testRemoveEleFromObjectArray() {
		final Integer[] array = {1, 2, 3, 2, 4};
		final Integer[] expected = {1, 3, 2, 4};
		Assertions.assertArrayEquals(expected, ArrayUtil.removeEle(array, 2));
	}

	@Test
	public void removeEmptyTest() {
		final String[] a = {"a", "b", "", null, " ", "c"};
		final String[] resultA = {"a", "b", " ", "c"};
		assertArrayEquals(ArrayUtil.removeEmpty(a), resultA);
	}

	@Test
	public void removeBlankTest() {
		final String[] a = {"a", "b", "", null, " ", "c"};
		final String[] resultA = {"a", "b", "c"};
		assertArrayEquals(ArrayUtil.removeBlank(a), resultA);
	}

	@Test
	public void nullToEmptyTest() {
		final String[] a = {"a", "b", "", null, " ", "c"};
		final String[] resultA = {"a", "b", "", "", " ", "c"};
		assertArrayEquals(ArrayUtil.nullToEmpty(a), resultA);
	}

	@Test
	public void wrapTest() {
		final Object a = new int[]{1, 2, 3, 4};
		final Object[] wrapA = ArrayUtil.wrap(a);
		for (final Object o : wrapA) {
			Assertions.assertInstanceOf(Integer.class, o);
		}
	}

	@Test
	public void wrapIntTest() {
		final int[] a = new int[]{1, 2, 3, 4};
		final Integer[] wrapA = ArrayUtil.wrap(a);
		for (final Integer o : wrapA) {
			Assertions.assertInstanceOf(Integer.class, o);
		}
	}

	@Test
	public void unWrapIntTest() {
		final Integer[] a = new Integer[]{1, 2, 3, 4};
		final int[] wrapA = ArrayUtil.unWrap(a);
		final Class<?> componentType = wrapA.getClass().getComponentType();
		assertEquals(int.class, componentType);
	}

	@Test
	public void splitTest() {
		final byte[] array = new byte[1024];
		final byte[][] arrayAfterSplit = ArrayUtil.split(array, 500);
		assertEquals(3, arrayAfterSplit.length);
		assertEquals(24, arrayAfterSplit[2].length);

		final byte[] arr = {1, 2, 3, 4, 5, 6, 7};
		assertArrayEquals(new byte[][]{{1, 2, 3, 4, 5, 6, 7}}, ArrayUtil.split(arr, 8));
		assertArrayEquals(new byte[][]{{1, 2, 3, 4, 5, 6, 7}}, ArrayUtil.split(arr, 7));
		assertArrayEquals(new byte[][]{{1, 2, 3, 4}, {5, 6, 7}}, ArrayUtil.split(arr, 4));
		assertArrayEquals(new byte[][]{{1, 2, 3}, {4, 5, 6}, {7}}, ArrayUtil.split(arr, 3));
		assertArrayEquals(new byte[][]{{1, 2}, {3, 4}, {5, 6}, {7}}, ArrayUtil.split(arr, 2));
		assertArrayEquals(new byte[][]{{1}, {2}, {3}, {4}, {5}, {6}, {7}}, ArrayUtil.split(arr, 1));
	}

	@Test
	public void getTest() {
		final String[] a = {"a", "b", "c"};
		final Object o = ArrayUtil.get(a, -1);
		assertEquals("c", o);
	}

	@Test
	public void getByPredicateTest() {
		final String[] a = {"a", "b", "c"};
		final Object o = ArrayUtil.get(a, "b"::equals);
		assertEquals("b", o);
	}

	@Test
	public void replaceTest() {
		final String[] a = {"1", "2", "3", "4"};
		final String[] b = {"a", "b", "c"};

		// 在小于0的位置，相当于在a前插入b，返回b+a，新数组
		String[] result = ArrayUtil.replace(a, -1, b);
		assertArrayEquals(new String[]{"a", "b", "c", "1", "2", "3", "4"}, result);

		// 在第0个位置开始替换，返回a
		result = ArrayUtil.replace(ArrayUtil.clone(a), 0, b);
		assertArrayEquals(new String[]{"a", "b", "c", "4"}, result);

		// 在第1个位置替换，即"2"开始
		result = ArrayUtil.replace(ArrayUtil.clone(a), 1, b);
		assertArrayEquals(new String[]{"1", "a", "b", "c"}, result);

		// 在第2个位置插入，即"3"之后
		result = ArrayUtil.replace(ArrayUtil.clone(a), 2, b);
		assertArrayEquals(new String[]{"1", "2", "a", "b", "c"}, result);

		// 在第3个位置插入，即"4"之后
		result = ArrayUtil.replace(ArrayUtil.clone(a), 3, b);
		assertArrayEquals(new String[]{"1", "2", "3", "a", "b", "c"}, result);

		// 在第4个位置插入，数组长度为4，在索引4出替换即两个数组相加
		result = ArrayUtil.replace(ArrayUtil.clone(a), 4, b);
		assertArrayEquals(new String[]{"1", "2", "3", "4", "a", "b", "c"}, result);

		// 在大于3个位置插入，数组长度为4，即两个数组相加
		result = ArrayUtil.replace(ArrayUtil.clone(a), 5, b);
		assertArrayEquals(new String[]{"1", "2", "3", "4", "a", "b", "c"}, result);

		final String[] e = null;
		final String[] f = {"a", "b", "c"};

		// e为null 返回 f
		result = ArrayUtil.replace(e, -1, f);
		assertArrayEquals(f, result);

		final String[] g = {"a", "b", "c"};
		final String[] h = null;

		// h为null 返回 g
		result = ArrayUtil.replace(g, 0, h);
		assertArrayEquals(g, result);
	}

	@Test
	public void replaceTest2() {
		int[] a = new int[0];
		a = ArrayUtil.replace(a, 0, new int[]{1});
		assertEquals(1, a.length);
	}

	@Test
	public void setOrAppendTest() {
		final String[] arr = new String[0];
		final String[] newArr = ArrayUtil.setOrAppend(arr, 0, "Good");// ClassCastException
		assertArrayEquals(new String[]{"Good"}, newArr);

		// 非空数组替换第一个元素
		int[] arr2 = new int[]{1};
		int[] o = ArrayUtil.setOrAppend(arr2, 0, 2);
		assertArrayEquals(new int[]{2}, o);

		// 空数组追加
		arr2 = new int[0];
		o = ArrayUtil.setOrAppend(arr2, 0, 2);
		assertArrayEquals(new int[]{2}, o);
	}

	@Test
	void setOrPaddingTest() {
		final String[] arr = new String[0];
		final String[] newArr = ArrayUtil.setOrPadding(arr, 2, "Good");
		assertArrayEquals(new String[]{null, null, "Good"}, newArr);
	}

	@Test
	void setOrPaddingTest2() {
		final String[] arr = new String[0];
		final String[] newArr = ArrayUtil.setOrPadding(arr, 2, "Good");
		assertArrayEquals(new String[]{null, null, "Good"}, newArr);
	}

	@Test
	void setOrPaddingTest3() {
		final String[] arr = new String[0];
		final String[] newArr = ArrayUtil.setOrPadding(arr, 2, "Good", "pad");
		assertArrayEquals(new String[]{"pad", "pad", "Good"}, newArr);
	}

	@Test
	public void getAnyTest() {
		final String[] a = {"a", "b", "c", "d", "e"};
		final Object o = ArrayUtil.getAny(a, 3, 4);
		final String[] resultO = (String[]) o;
		final String[] c = {"d", "e"};
		Assertions.assertTrue(ArrayUtil.containsAll(c, resultO[0], resultO[1]));
	}

	@Test
	void hasNullTest() {
		final String[] a = {"e", null};
		Assertions.assertTrue(ArrayUtil.hasNull(a));
	}

	@Test
	public void hasNonNullTest() {
		String[] a = {null, "e"};
		Assertions.assertTrue(ArrayUtil.hasNonNull(a));

		a = new String[]{null, null};
		Assertions.assertFalse(ArrayUtil.hasNonNull(a));

		a = new String[]{"", null};
		Assertions.assertTrue(ArrayUtil.hasNonNull(a));

		a = new String[]{null};
		Assertions.assertFalse(ArrayUtil.hasNonNull(a));

		a = new String[]{};
		Assertions.assertFalse(ArrayUtil.hasNonNull(a));

		a = null;
		Assertions.assertFalse(ArrayUtil.hasNonNull(a));
	}

	@Test
	public void isAllNullTest() {
		String[] a = {null, "e"};
		Assertions.assertFalse(ArrayUtil.isAllNull(a));

		a = new String[]{null, null};
		Assertions.assertTrue(ArrayUtil.isAllNull(a));

		a = new String[]{"", null};
		Assertions.assertFalse(ArrayUtil.isAllNull(a));

		a = new String[]{null};
		Assertions.assertTrue(ArrayUtil.isAllNull(a));

		a = new String[]{};
		Assertions.assertTrue(ArrayUtil.isAllNull(a));

		a = null;
		Assertions.assertTrue(ArrayUtil.isAllNull(a));
	}

	@Test
	public void insertPrimitiveTest() {
		// https://gitee.com/dromara/hutool/pulls/874

		final boolean[] booleans = new boolean[10];
		final byte[] bytes = new byte[10];
		final char[] chars = new char[10];
		final short[] shorts = new short[10];
		final int[] ints = new int[10];
		final long[] longs = new long[10];
		final float[] floats = new float[10];
		final double[] doubles = new double[10];

		final boolean[] insert1 = ArrayUtil.insert(booleans, 0, 0, 1, 2);
		Assertions.assertNotNull(insert1);
		final byte[] insert2 = ArrayUtil.insert(bytes, 0, 1, 2, 3);
		Assertions.assertNotNull(insert2);
		final char[] insert3 = ArrayUtil.insert(chars, 0, 1, 2, 3);
		Assertions.assertNotNull(insert3);
		final short[] insert4 = ArrayUtil.insert(shorts, 0, 1, 2, 3);
		Assertions.assertNotNull(insert4);
		final int[] insert5 = ArrayUtil.insert(ints, 0, 1, 2, 3);
		Assertions.assertNotNull(insert5);
		final long[] insert6 = ArrayUtil.insert(longs, 0, 1, 2, 3);
		Assertions.assertNotNull(insert6);
		final float[] insert7 = ArrayUtil.insert(floats, 0, 1, 2, 3);
		Assertions.assertNotNull(insert7);
		final double[] insert8 = ArrayUtil.insert(doubles, 0, 1, 2, 3);
		Assertions.assertNotNull(insert8);
	}

	@Test
	public void subTest() {
		final int[] arr = {1, 2, 3, 4, 5};
		final int[] empty = new int[0];
		assertArrayEquals(empty, ArrayUtil.sub(arr, 2, 2));
		assertArrayEquals(empty, ArrayUtil.sub(arr, 5, 5));
		assertArrayEquals(empty, ArrayUtil.sub(arr, 5, 7));
		assertArrayEquals(arr, ArrayUtil.sub(arr, 0, 5));
		assertArrayEquals(arr, ArrayUtil.sub(arr, 5, 0));
		assertArrayEquals(arr, ArrayUtil.sub(arr, 0, 7));
		assertArrayEquals(new int[]{1}, ArrayUtil.sub(arr, 0, 1));
		assertArrayEquals(new int[]{5}, ArrayUtil.sub(arr, 4, 5));
		assertArrayEquals(new int[]{2, 3, 4}, ArrayUtil.sub(arr, 1, 4));
		assertArrayEquals(new int[]{2, 3, 4}, ArrayUtil.sub(arr, 4, 1));
		assertArrayEquals(new int[]{2, 3, 4}, ArrayUtil.sub(arr, 1, -1));
		assertArrayEquals(new int[]{2, 3, 4}, ArrayUtil.sub(arr, -1, 1));
		assertArrayEquals(new int[]{2, 3, 4}, ArrayUtil.sub(arr, -1, 1));
		assertArrayEquals(new int[]{2, 3, 4}, ArrayUtil.sub(arr, -4, -1));
	}

	@Test
	public void isSortedTest() {
		final Integer[] a = {1, 1, 2, 2, 2, 3, 3};
		Assertions.assertTrue(ArrayUtil.isSorted(a));
		Assertions.assertTrue(ArrayUtil.isSorted(a, Integer::compareTo));
		Assertions.assertFalse(ArrayUtil.isSorted(a, null));

		final Integer[] b = {1, 1, 1, 1, 1, 1};
		Assertions.assertTrue(ArrayUtil.isSorted(b));
		Assertions.assertTrue(ArrayUtil.isSorted(b, Integer::compareTo));
		Assertions.assertFalse(ArrayUtil.isSorted(a, null));

		final Integer[] c = {3, 3, 2, 2, 2, 1, 1};
		Assertions.assertTrue(ArrayUtil.isSorted(c));
		Assertions.assertTrue(ArrayUtil.isSorted(c, Integer::compareTo));
		Assertions.assertFalse(ArrayUtil.isSorted(a, null));

		Assertions.assertFalse(ArrayUtil.isSorted(null));
		Assertions.assertFalse(ArrayUtil.isSorted(null, Integer::compareTo));
		Assertions.assertFalse(ArrayUtil.isSorted(null, null));

		final Integer[] d = {};
		Assertions.assertFalse(ArrayUtil.isSorted(d));
		Assertions.assertFalse(ArrayUtil.isSorted(d, Integer::compareTo));
		Assertions.assertFalse(ArrayUtil.isSorted(d, null));

		final Integer[] e = {1};
		Assertions.assertTrue(ArrayUtil.isSorted(e));
		Assertions.assertTrue(ArrayUtil.isSorted(e, Integer::compareTo));
		Assertions.assertFalse(ArrayUtil.isSorted(e, null));

		final Integer[] f = {1, 2};
		Assertions.assertTrue(ArrayUtil.isSorted(f));
		Assertions.assertTrue(ArrayUtil.isSorted(f, Integer::compareTo));
		Assertions.assertFalse(ArrayUtil.isSorted(f, null));
	}

	@Test
	public void hasSameElementTest() {
		final Integer[] a = {1, 1};
		Assertions.assertTrue(ArrayUtil.hasSameElement(a));

		final String[] b = {"a", "b", "c"};
		Assertions.assertFalse(ArrayUtil.hasSameElement(b));

		final Object[] c = new Object[]{"1", "2", 2, 4D};
		Assertions.assertFalse(ArrayUtil.hasSameElement(c));

		final Object[] d = new Object[]{"1", "2", "2", 4D};
		Assertions.assertTrue(ArrayUtil.hasSameElement(d));

		final Object[] e = new Object[]{"1", 2, 2, 4D};
		Assertions.assertTrue(ArrayUtil.hasSameElement(e));

	}

	@Test
	public void startWithTest() {
		boolean b = ArrayUtil.startWith(new String[]{}, new String[]{});
		Assertions.assertTrue(b);

		b = ArrayUtil.startWith(new String[]{"1", "2", "3"}, new String[]{"1"});
		Assertions.assertTrue(b);

		b = ArrayUtil.startWith(new String[]{"1"}, new String[]{"1"});
		Assertions.assertTrue(b);

		b = ArrayUtil.startWith((String[]) null, null);
		Assertions.assertTrue(b);
	}

	@SuppressWarnings({"RedundantArrayCreation", "ConfusingArgumentToVarargsMethod"})
	@Test
	public void startWithTest2() {
		boolean b = ArrayUtil.startWith(new int[]{}, new int[]{});
		Assertions.assertTrue(b);

		b = ArrayUtil.startWith(new int[]{1, 2, 3}, new int[]{1});
		Assertions.assertTrue(b);

		b = ArrayUtil.startWith(new int[]{1}, new int[]{1});
		Assertions.assertTrue(b);

		b = ArrayUtil.startWith((int[]) null, null);
		Assertions.assertTrue(b);
	}

	@Test
	public void equalsTest() {
		final boolean b = ArrayUtil.equals(new int[]{1, 2, 3}, new int[]{1, 2, 3});
		Assertions.assertTrue(b);
	}

	@Test
	public void copyOfRangeTest() {
		final String a = "aIDAT" ;
		final byte[] bytes1 = Arrays.copyOfRange(a.getBytes(CharsetUtil.UTF_8), 1, 1 + 4);

		assertEquals(new String(bytes1),
			new String(a.getBytes(CharsetUtil.UTF_8), 1, 4));
	}

	@Test
	void copyTest() {
		final String[] dest = new String[3];
		ArrayUtil.copy(new String[]{"a", "b"}, dest);
		assertArrayEquals(new String[]{"a", "b", null}, dest);
	}

	@Test
	void copyTest2() {
		final String[] dest = new String[3];
		ArrayUtil.copy(new String[]{"a", "b"}, dest, 1);
		assertArrayEquals(new String[]{"a", null, null}, dest);
	}

	@Test
	public void regionMatchesTest() {
		final byte[] a = new byte[]{1, 2, 3, 4, 5};
		final byte[] b = new byte[]{2, 3, 4};

		Assertions.assertTrue(ArrayUtil.regionMatches(a, 1, b, 0, 1));
		Assertions.assertTrue(ArrayUtil.regionMatches(a, 1, b, 0, 2));
		Assertions.assertTrue(ArrayUtil.regionMatches(a, 1, b, 0, 3));
		Assertions.assertTrue(ArrayUtil.isSubEquals(a, 1, b));

		Assertions.assertFalse(ArrayUtil.regionMatches(a, 2, b, 0, 2));
		Assertions.assertFalse(ArrayUtil.regionMatches(a, 3, b, 0, 2));
	}

	@Test
	public void hasEmptyVarargsTest() {
		Assertions.assertFalse(ArrayUtil.hasEmptyVarargs(1, 2, 3, 4, 5));
		Assertions.assertTrue(ArrayUtil.hasEmptyVarargs("", " ", "	"));
		Assertions.assertTrue(ArrayUtil.hasEmptyVarargs("", "apple", "pear"));
	}

	@Test
	void hasEmptyTest() {
		final String[] a = {"", "a"};
		Assertions.assertTrue(ArrayUtil.hasEmpty(a));

		Object[] b = {"a", new ArrayList<>()};
		Assertions.assertTrue(ArrayUtil.hasEmpty(b));

		b = new Object[]{"a", new HashMap<>()};
		Assertions.assertTrue(ArrayUtil.hasEmpty(b));
	}

	@Test
	public void isAllEmptyTest() {
		Assertions.assertFalse(ArrayUtil.isAllEmptyVarargs("apple", "pear", "", "orange"));
	}

	@Test
	void emptyCountTest() {
		final Object[] b = {"a", new ArrayList<>(), new HashMap<>(), new int[0]};
		final int emptyCount = ArrayUtil.emptyCount(b);
		assertEquals(3, emptyCount);
	}

	@Test
	void hasBlankTest() {
		final String[] a = {"  ", "aa"};
		Assertions.assertTrue(ArrayUtil.hasBlank(a));
	}

	@Test
	void isAllBlankTest() {
		final String[] a = {"  ", " ", ""};
		Assertions.assertTrue(ArrayUtil.isAllBlank(a));
	}

	@Test
	void firstMatchShouldReturnFirstMatch() {
		final Integer[] array = {5, 10, 15, 20, 25};
		final Integer result = ArrayUtil.firstMatch(value -> value > 15, array);
		assertEquals(20, result);
	}

	@Test
	void testMatchIndexWithMatchingFirstElement() {
		final Integer[] array = {1, 2, 3, 4, 5};
		final int index = ArrayUtil.matchIndex(value -> value == 3, array);
		assertEquals(2, index);
	}

	@Test
	void testofArrayWithNonEmptyIterable() {
		// Given
		final List<String> list = Arrays.asList("a", "b", "c");
		// When
		final String[] result = ArrayUtil.ofArray(list, String.class);
		// Then
		assertArrayEquals(list.toArray(new String[0]), result, "The array should match the list contents.");
	}

	@Test
	public void testResizeWithSmallerSize() {
		// Setup
		final Integer[] originalArray = {1, 2, 3, 4, 5};
		// Execute
		final Integer[] resizedArray = ArrayUtil.resize(originalArray, 3);
		// Assert
		assertEquals(3, resizedArray.length);
		assertArrayEquals(new Integer[]{1, 2, 3}, resizedArray);
	}

	@Test
	public void testResizeWithLargerSize() {
		// Setup
		final Integer[] originalArray = {1, 2, 3};
		// Execute
		final Integer[] resizedArray = ArrayUtil.resize(originalArray, 5);
		// Assert
		assertEquals(5, resizedArray.length);
		assertArrayEquals(new Integer[]{1, 2, 3, null, null}, resizedArray);
	}

	@Test
	void testShuffleNotSameAsOriginal() {
		final Integer[] initialArray = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
		final Integer[] shuffledArray = ArrayUtil.shuffle(initialArray.clone());

		assertNotEquals(Arrays.toString(initialArray), Arrays.toString(shuffledArray));
	}
}
