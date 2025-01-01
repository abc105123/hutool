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

package org.dromara.hutool.json.xml;

import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.text.CharUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.text.escape.EscapeUtil;
import org.dromara.hutool.json.JSON;
import org.dromara.hutool.json.JSONArray;
import org.dromara.hutool.json.JSONException;
import org.dromara.hutool.json.JSONObject;

/**
 * JSON转XML字符串工具
 *
 * @author Looly
 * @since 5.7.11
 */
public class JSONXMLSerializer {
	/**
	 * 转换JSONObject为XML
	 * Convert a JSONObject into a well-formed, element-normal XML string.
	 *
	 * @param json A JSONObject.
	 * @return A string.
	 * @throws JSONException Thrown if there is an error parsing the string
	 */
	public static String toXml(final JSON json) throws JSONException {
		return toXml(json, null);
	}

	/**
	 * 转换JSONObject为XML
	 *
	 * @param json  JSON对象或数组
	 * @param tagName 可选标签名称，名称为空时忽略标签
	 * @return A string.
	 * @throws JSONException JSON解析异常
	 */
	public static String toXml(final JSON json, final String tagName) throws JSONException {
		return toXml(json, tagName, "content");
	}

	/**
	 * 转换JSONObject为XML
	 *
	 * @param json      JSON对象或数组
	 * @param tagName     可选标签名称，名称为空时忽略标签
	 * @param contentKeys 标识为内容的key,遇到此key直接解析内容而不增加对应名称标签
	 * @return A string.
	 * @throws JSONException JSON解析异常
	 */
	public static String toXml(final JSON json, final String tagName, final String... contentKeys) throws JSONException {
		if (null == json) {
			return null;
		}

		final StringBuilder sb = new StringBuilder();
		if (json instanceof JSONObject) {

			// Emit <tagName>
			appendTag(sb, tagName, false);

			// Loop thru the keys.
			json.asJSONObject().forEach((key, value) -> {
				// Emit content in body
				if (ArrayUtil.contains(contentKeys, key)) {
					if (value instanceof JSONArray) {
						int i = 0;
						for (final JSON val : (JSONArray) value) {
							if (i > 0) {
								sb.append(CharUtil.LF);
							}
							sb.append(EscapeUtil.escapeXml(val.toBean(String.class)));
							i++;
						}
					} else {
						sb.append(EscapeUtil.escapeXml(value.toBean(String.class)));
					}

					// Emit an array of similar keys

				} else if (StrUtil.isEmptyIfStr(value)) {
					sb.append(wrapWithTag(null, key));
				} else if (value instanceof JSONArray) {
					for (final JSON val : (JSONArray) value) {
						if (val instanceof JSONArray) {
							sb.append(wrapWithTag(toXml(val, null, contentKeys), key));
						} else {
							sb.append(toXml(val, key, contentKeys));
						}
					}
				} else {
					sb.append(toXml(value, key, contentKeys));
				}
			});

			// Emit the </tagname> close tag
			appendTag(sb, tagName, true);
			return sb.toString();
		}

		if (json instanceof JSONArray) {
			for (final JSON val : (JSONArray) json) {
				// XML does not have good support for arrays. If an array
				// appears in a place where XML is lacking, synthesize an
				// <array> element.
				sb.append(toXml(val, tagName == null ? "array" : tagName, contentKeys));
			}
			return sb.toString();
		}

		return wrapWithTag(EscapeUtil.escapeXml(json.toBean(String.class)), tagName);
	}

	/**
	 * 追加标签
	 *
	 * @param sb       XML内容
	 * @param tagName  标签名
	 * @param isEndTag 是否结束标签
	 * @since 5.7.11
	 */
	private static void appendTag(final StringBuilder sb, final String tagName, final boolean isEndTag) {
		if (StrUtil.isNotBlank(tagName)) {
			sb.append('<');
			if (isEndTag) {
				sb.append('/');
			}
			sb.append(tagName).append('>');
		}
	}

	/**
	 * 将内容使用标签包装为XML
	 *
	 * @param tagName 标签名
	 * @param content 内容
	 * @return 包装后的XML
	 * @since 5.7.11
	 */
	private static String wrapWithTag(final String content, final String tagName) {
		if (StrUtil.isBlank(tagName)) {
			return StrUtil.wrap(content, "\"");
		}

		if (StrUtil.isEmpty(content)) {
			return "<" + tagName + "/>";
		} else {
			return "<" + tagName + ">" + content + "</" + tagName + ">";
		}
	}
}
