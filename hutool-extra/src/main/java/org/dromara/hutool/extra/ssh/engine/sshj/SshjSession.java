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

package org.dromara.hutool.extra.ssh.engine.sshj;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Parameters;
import net.schmizz.sshj.connection.channel.forwarded.RemotePortForwarder;
import net.schmizz.sshj.connection.channel.forwarded.SocketForwardingConnectListener;
import org.dromara.hutool.core.io.IORuntimeException;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.map.MapUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.util.CharsetUtil;
import org.dromara.hutool.extra.ssh.Connector;
import org.dromara.hutool.extra.ssh.Session;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 基于SSHJ（https://github.com/hierynomus/sshj）的Session封装
 *
 * @author Looly
 */
public class SshjSession implements Session {

	private final SSHClient ssh;
	private final net.schmizz.sshj.connection.channel.direct.Session raw;

	private Map<String, ServerSocket> localPortForwarderMap;

	/**
	 * 构造
	 *
	 * @param connector {@link Connector}，保存连接和验证信息等
	 */
	public SshjSession(final Connector connector) {
		this(SshjUtil.openClient(connector));
	}

	/**
	 * 构造
	 *
	 * @param ssh {@link SSHClient}
	 */
	public SshjSession(final SSHClient ssh) {
		this.ssh = ssh;
		try {
			this.raw = ssh.startSession();
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
	}

	@Override
	public net.schmizz.sshj.connection.channel.direct.Session getRaw() {
		return raw;
	}

	@Override
	public boolean isConnected() {
		return null != this.raw && (null == this.ssh || this.ssh.isConnected());
	}

	@Override
	public void close() throws IOException {
		IoUtil.closeQuietly(this.raw);
		IoUtil.closeQuietly(this.ssh);
	}

	/**
	 * 打开SFTP会话
	 *
	 * @param charset 编码
	 * @return {@link SshjSftp}
	 */
	public SshjSftp openSftp(final Charset charset) {
		return new SshjSftp(this.ssh, charset);
	}

	@Override
	public void bindLocalPort(final InetSocketAddress localAddress, final InetSocketAddress remoteAddress) throws IORuntimeException {
		final Parameters params = new Parameters(
			localAddress.getHostName(), localAddress.getPort(),
			remoteAddress.getHostName(), remoteAddress.getPort());
		final ServerSocket ss;
		try {
			ss = new ServerSocket();
			ss.setReuseAddress(true);
			ss.bind(localAddress);
			ssh.newLocalPortForwarder(params, ss).listen();
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}

		if (null == this.localPortForwarderMap) {
			this.localPortForwarderMap = new HashMap<>();
		}

		//加入记录
		this.localPortForwarderMap.put(localAddress.toString(), ss);
	}

	@Override
	public void unBindLocalPort(final InetSocketAddress localAddress) throws IORuntimeException {
		if (MapUtil.isEmpty(this.localPortForwarderMap)) {
			return;
		}

		IoUtil.closeQuietly(this.localPortForwarderMap.remove(localAddress.toString()));
	}

	@Override
	public void bindRemotePort(final InetSocketAddress remoteAddress, final InetSocketAddress localAddress) throws IORuntimeException {
		try {
			this.ssh.getRemotePortForwarder().bind(
				new RemotePortForwarder.Forward(remoteAddress.getHostName(), remoteAddress.getPort()),
				new SocketForwardingConnectListener(localAddress)
			);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}
	}

	@Override
	public void unBindRemotePort(final InetSocketAddress remoteAddress) {
		final String hostName = remoteAddress.getHostName();
		final int port = remoteAddress.getPort();

		final RemotePortForwarder remotePortForwarder = this.ssh.getRemotePortForwarder();
		final Set<RemotePortForwarder.Forward> activeForwards = remotePortForwarder.getActiveForwards();
		for (final RemotePortForwarder.Forward activeForward : activeForwards) {
			if (port == activeForward.getPort()) {
				final String activeAddress = activeForward.getAddress();
				if(StrUtil.isNotBlank(activeAddress) && !StrUtil.equalsIgnoreCase(hostName, activeAddress)){
					// 对于用于已经定义的host，做对比，否则跳过
					continue;
				}

				try {
					remotePortForwarder.cancel(activeForward);
				} catch (final IOException e) {
					throw new IORuntimeException(e);
				}
				return;
			}
		}
	}

	/**
	 * 执行Shell命令（使用EXEC方式）
	 * <p>
	 * 此方法单次发送一个命令到服务端，不读取环境变量，不会产生阻塞。
	 * </p>
	 *
	 * @param cmd       命令
	 * @param charset   发送和读取内容的编码
	 * @param errStream 错误信息输出到的位置
	 * @return 执行返回结果
	 */
	public String exec(final String cmd, Charset charset, final OutputStream errStream) {
		if (null == charset) {
			charset = CharsetUtil.UTF_8;
		}

		final net.schmizz.sshj.connection.channel.direct.Session.Command command;

		// 发送命令
		try {
			command = this.raw.exec(cmd);
			//command.join();
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}

		// 错误输出
		if (null != errStream) {
			IoUtil.copy(command.getErrorStream(), errStream);
		}

		// 结果输出
		return IoUtil.read(command.getInputStream(), charset);
	}

	/**
	 * 执行Shell命令
	 * <p>
	 * 此方法单次发送一个命令到服务端，自动读取环境变量，可能产生阻塞。
	 * </p>
	 *
	 * @param cmd       命令
	 * @param charset   发送和读取内容的编码
	 * @param errStream 错误信息输出到的位置
	 * @return 执行返回结果
	 */
	public String execByShell(final String cmd, Charset charset, final OutputStream errStream) {
		if (null == charset) {
			charset = CharsetUtil.UTF_8;
		}

		final net.schmizz.sshj.connection.channel.direct.Session.Shell shell;
		try {
			shell = this.raw.startShell();
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		}

		// 发送命令
		IoUtil.writeStrs(shell.getOutputStream(), charset, true, cmd);

		// 错误输出
		if (null != errStream) {
			IoUtil.copy(shell.getErrorStream(), errStream);
		}

		// 结果输出
		return IoUtil.read(shell.getInputStream(), charset);
	}
}
