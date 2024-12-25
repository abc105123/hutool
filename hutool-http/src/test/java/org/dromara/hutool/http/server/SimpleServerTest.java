/*
 * Copyright (c) 2024 Hutool Team and hutool.cn
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

package org.dromara.hutool.http.server;

import org.dromara.hutool.core.collection.ListUtil;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.http.HttpUtil;
import org.dromara.hutool.http.meta.ContentType;
import org.dromara.hutool.http.meta.HeaderName;
import org.dromara.hutool.http.multipart.UploadFile;
import org.dromara.hutool.json.JSONUtil;

import java.net.HttpCookie;

public class SimpleServerTest {

	public static void main(final String[] args) {
		HttpUtil.createServer(8888)
			.addFilter(((req, res, chain) -> {
				Console.log("Filter: " + req.getPath());
				chain.doFilter(req.getExchange());
			}))
			// 设置默认根目录，classpath/html
			.setRoot(FileUtil.file("html"))
			// get数据测试，返回请求的PATH
			.addAction("/get", (request, response) ->
				response.write(request.getPath(), ContentType.TEXT_PLAIN.toString())
			)
			// 返回JSON数据测试
			.addAction("/restTest", (request, response) -> {
				final String res = JSONUtil.ofObj()
					.putValue("id", 1)
					.putValue("method", request.getMethod())
					.putValue("request", request.getBody())
					.toStringPretty();
				response.write(res, ContentType.JSON.toString());
			})
			// 获取表单数据测试
			// http://localhost:8888/formTest?a=1&a=2&b=3
			.addAction("/formTest", (request, response) -> {
					Console.log(request.getMethod());
					response.write(request.getParams().toString(), ContentType.TEXT_PLAIN.toString());
				}
			)

			// 文件上传测试
			// http://localhost:8888/formForUpload.html
			.addAction("/file", (request, response) -> {
					Console.log("Upload file...");
					Console.log(request.getParams());
					final UploadFile[] files = request.getMultipart().getFiles("file");
					// 传入目录，默认读取HTTP头中的文件名然后创建文件
					for (final UploadFile file : files) {
						file.write("d:/test/");
						Console.log("Write file: d:/test/" + file.getFileName());
					}
					response.write(request.getMultipart().getParamMap().toString(), ContentType.TEXT_PLAIN.toString());
				}
			)
			// 测试输出响应内容是否能正常返回Content-Length头信息
			.addAction("test/zeroStr", (req, res) -> {
				res.write("0");
				Console.log("Write 0 OK");
			}).addAction("/getCookie", ((request, response) -> {
				response.setHeader(HeaderName.SET_COOKIE.toString(),
					ListUtil.of(
						new HttpCookie("cc", "123").toString(),
						new HttpCookie("cc", "abc").toString()));
				response.write("Cookie ok");
			}))
			.start();
	}
}
