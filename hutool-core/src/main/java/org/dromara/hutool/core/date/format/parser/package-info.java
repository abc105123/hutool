/*
 * Copyright (c) 2013-2024 Hutool Team and hutool.cn
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

/**
 * 提供日期解析相关封装，主要包括：
 * <pre>
 *                            DateParser
 *             /                  |                   \
 *     FastDateParser     RegisterDateParser     RegexDateParser
 *   （根据日期格式解析）   （根据注册的模式匹配解析） （通过预定义正则解析）
 * </pre>
 *
 * @author looly
 */
package org.dromara.hutool.core.date.format.parser;
