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

package org.dromara.hutool.extra.ssh.engine.jsch;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import org.dromara.hutool.core.io.IORuntimeException;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.util.ByteUtil;
import org.dromara.hutool.core.util.CharsetUtil;
import org.dromara.hutool.extra.ssh.Connector;
import org.dromara.hutool.extra.ssh.Session;
import org.dromara.hutool.extra.ssh.SshException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Jsch Session封装
 */
public class JschSession implements Session {

	private final com.jcraft.jsch.Session raw;
	private final long timeout;

	/**
	 * 构造
	 *
	 * @param connector {@link Connector}，保存连接和验证信息等
	 */
	public JschSession(final Connector connector) {
		this(JschUtil.openSession(connector), connector.getTimeout());
	}

	/**
	 * 构造
	 *
	 * @param raw     {@link com.jcraft.jsch.Session}
	 * @param timeout 连接超时时常，0表示不限制
	 */
	public JschSession(final com.jcraft.jsch.Session raw, final long timeout) {
		this.raw = raw;
		this.timeout = timeout;
	}

	@Override
	public com.jcraft.jsch.Session getRaw() {
		return this.raw;
	}

	@Override
	public boolean isConnected() {
		return null != this.raw && this.raw.isConnected();
	}

	@Override
	public void close() throws IOException {
		JschUtil.close(this.raw);
	}

	@Override
	public void bindLocalPort(final InetSocketAddress localAddress, final InetSocketAddress remoteAddress) throws SshException {
		try {
			this.raw.setPortForwardingL(localAddress.getHostName(), localAddress.getPort(), remoteAddress.getHostName(), remoteAddress.getPort());
		} catch (final JSchException e) {
			throw new SshException(e, "From [{}] mapping to [{}] error！", localAddress, remoteAddress);
		}
	}

	@Override
	public void unBindLocalPort(final InetSocketAddress localAddress) {
		try {
			this.raw.delPortForwardingL(localAddress.getHostName(), localAddress.getPort());
		} catch (final JSchException e) {
			throw new SshException(e);
		}
	}

	@Override
	public void bindRemotePort(final InetSocketAddress remoteAddress, final InetSocketAddress localAddress) throws SshException {
		try {
			this.raw.setPortForwardingR(remoteAddress.getHostName(), remoteAddress.getPort(),
				localAddress.getHostName(), localAddress.getPort());
		} catch (final JSchException e) {
			throw new SshException(e, "From [{}] mapping to [{}] error！", remoteAddress, localAddress);
		}
	}

	@Override
	public void unBindRemotePort(final InetSocketAddress remoteAddress) {
		try {
			this.raw.delPortForwardingR(remoteAddress.getHostName(), remoteAddress.getPort());
		} catch (final JSchException e) {
			throw new SshException(e);
		}
	}

	/**
	 * 创建Channel连接
	 *
	 * @param channelType 通道类型，可以是shell或sftp等，见{@link ChannelType}
	 * @return {@link Channel}
	 */
	public Channel createChannel(final ChannelType channelType) {
		return JschUtil.createChannel(this.raw, channelType, this.timeout);
	}

	/**
	 * 打开Shell连接
	 *
	 * @return {@link ChannelShell}
	 */
	public ChannelShell openShell() {
		return (ChannelShell) openChannel(ChannelType.SHELL);
	}

	/**
	 * 打开Channel连接
	 *
	 * @param channelType 通道类型，可以是shell或sftp等，见{@link ChannelType}
	 * @return {@link Channel}
	 */
	public Channel openChannel(final ChannelType channelType) {
		return JschUtil.openChannel(this.raw, channelType, this.timeout);
	}

	/**
	 * 打开SFTP会话
	 *
	 * @param charset 编码
	 * @return {@link JschSftp}
	 */
	public JschSftp openSftp(final Charset charset) {
		return new JschSftp(this.raw, charset, this.timeout);
	}

	/**
	 * 执行Shell命令
	 *
	 * @param cmd     命令
	 * @param charset 发送和读取内容的编码
	 * @return {@link ChannelExec}
	 */
	public String exec(final String cmd, final Charset charset) {
		return exec(cmd, charset, System.err);
	}

	/**
	 * 执行Shell命令（使用EXEC方式）
	 * <p>
	 * 此方法单次发送一个命令到服务端，不读取环境变量，执行结束后自动关闭channel，不会产生阻塞。
	 * </p>
	 *
	 * @param cmd       命令
	 * @param charset   发送和读取内容的编码
	 * @param errStream 错误信息输出到的位置
	 * @return 执行结果内容
	 * @since 4.3.1
	 */
	public String exec(final String cmd, Charset charset, final OutputStream errStream) {
		if (null == charset) {
			charset = CharsetUtil.UTF_8;
		}
		final ChannelExec channel = (ChannelExec) createChannel(ChannelType.EXEC);
		channel.setCommand(ByteUtil.toBytes(cmd, charset));
		channel.setInputStream(null);

		channel.setErrStream(errStream);
		InputStream in = null;
		try {
			channel.connect();
			in = channel.getInputStream();
			return IoUtil.read(in, charset);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		} catch (final JSchException e) {
			throw new SshException(e);
		} finally {
			IoUtil.closeQuietly(in);
			if (channel.isConnected()) {
				channel.disconnect();
			}
		}
	}

	/**
	 * 执行Shell命令
	 * <p>
	 * 此方法单次发送一个命令到服务端，自动读取环境变量，执行结束后自动关闭channel，不会产生阻塞。
	 * </p>
	 *
	 * @param cmd     命令
	 * @param charset 发送和读取内容的编码
	 * @return {@link ChannelExec}
	 * @since 5.2.5
	 */
	public String execByShell(final String cmd, final Charset charset) {
		final ChannelShell shell = openShell();
		// 开始连接
		shell.setPty(true);
		OutputStream out = null;
		InputStream in = null;
		try {
			out = shell.getOutputStream();
			in = shell.getInputStream();

			out.write(ByteUtil.toBytes(cmd, charset));
			out.flush();

			return IoUtil.read(in, charset);
		} catch (final IOException e) {
			throw new IORuntimeException(e);
		} finally {
			IoUtil.closeQuietly(out);
			IoUtil.closeQuietly(in);
			if (shell.isConnected()) {
				shell.disconnect();
			}
		}
	}
}
