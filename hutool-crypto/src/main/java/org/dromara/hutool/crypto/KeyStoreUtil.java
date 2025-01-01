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

package org.dromara.hutool.crypto;

import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.io.file.FileNameUtil;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.crypto.provider.GlobalProviderFactory;

import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;

/**
 * {@link KeyStore} 相关工具类
 *
 * @author Looly
 * @since 6.0.0
 */
public class KeyStoreUtil {

	/**
	 * Java密钥库(Java Key Store，JKS)KEY_STORE，Java 平台特有的密钥库格式<br>
	 * JKS 密钥库可以用 Java 的 keytool 工具进行管理。
	 */
	public static final String TYPE_JKS = "JKS";
	/**
	 * JCEKS（Java Cryptography Extension Key Store）
	 */
	public static final String TYPE_JCEKS = "jceks";
	/**
	 * PKCS12是公钥加密标准，它规定了可包含所有私钥、公钥和证书。其以二进制格式存储，也称为 PFX 文件
	 */
	public static final String TYPE_PKCS12 = "pkcs12";

	/**
	 * 读取密钥库(Java Key Store，JKS) KeyStore文件<br>
	 * KeyStore文件用于数字证书的密钥对保存<br>
	 * see: <a href="http://snowolf.iteye.com/blog/391931">...</a>
	 *
	 * @param keyFile  证书文件
	 * @param password 密码
	 * @return {@link KeyStore}
	 * @since 5.0.0
	 */
	public static KeyStore readJKSKeyStore(final File keyFile, final char[] password) {
		return readKeyStore(TYPE_JKS, keyFile, password);
	}

	/**
	 * 读取密钥库(Java Key Store，JKS) KeyStore文件<br>
	 * KeyStore文件用于数字证书的密钥对保存<br>
	 * see: <a href="http://snowolf.iteye.com/blog/391931">...</a>
	 *
	 * @param in       {@link InputStream} 如果想从文件读取.keystore文件，使用 {@link FileUtil#getInputStream(File)} 读取
	 * @param password 密码
	 * @return {@link KeyStore}
	 */
	public static KeyStore readJKSKeyStore(final InputStream in, final char[] password) {
		return readKeyStore(TYPE_JKS, in, password);
	}

	/**
	 * 读取PKCS12 KeyStore文件<br>
	 * KeyStore文件用于数字证书的密钥对保存
	 *
	 * @param keyFile  证书文件
	 * @param password 密码
	 * @return {@link KeyStore}
	 * @since 5.0.0
	 */
	public static KeyStore readPKCS12KeyStore(final File keyFile, final char[] password) {
		return readKeyStore(TYPE_PKCS12, keyFile, password);
	}

	/**
	 * 读取PKCS12 KeyStore文件<br>
	 * KeyStore文件用于数字证书的密钥对保存
	 *
	 * @param in       {@link InputStream} 如果想从文件读取.keystore文件，使用 {@link FileUtil#getInputStream(java.io.File)} 读取
	 * @param password 密码
	 * @return {@link KeyStore}
	 * @since 5.0.0
	 */
	public static KeyStore readPKCS12KeyStore(final InputStream in, final char[] password) {
		return readKeyStore(TYPE_PKCS12, in, password);
	}

	/**
	 * 读取KeyStore文件<br>
	 * KeyStore文件用于数字证书的密钥对保存<br>
	 * 证书类型根据扩展名自动判断，规则如下：
	 * <pre>{@code
	 *     .jks .keystore -> JKS
	 *      .p12 .pfx等其它 -> PKCS12
	 * }</pre>
	 *
	 * @param keyFile  证书文件
	 * @param password 密码，null表示无密码
	 * @return {@link KeyStore}
	 * @since 6.0.0
	 */
	public static KeyStore readKeyStore(final File keyFile, final char[] password) {
		final String suffix = FileNameUtil.getSuffix(keyFile);
		final String type;
		if(StrUtil.equalsIgnoreCase(suffix, "jks") || StrUtil.equalsIgnoreCase(suffix, "keystore")){
			type = TYPE_JKS;
		}else{
			type = TYPE_PKCS12;
		}
		return readKeyStore(type, keyFile, password);
	}

	/**
	 * 读取KeyStore文件<br>
	 * KeyStore文件用于数字证书的密钥对保存<br>
	 * see: <a href="http://snowolf.iteye.com/blog/391931">...</a>
	 *
	 * @param type     类型
	 * @param keyFile  证书文件
	 * @param password 密码，null表示无密码
	 * @return {@link KeyStore}
	 * @since 5.0.0
	 */
	public static KeyStore readKeyStore(final String type, final File keyFile, final char[] password) {
		InputStream in = null;
		try {
			in = FileUtil.getInputStream(keyFile);
			return readKeyStore(type, in, password);
		} finally {
			IoUtil.closeQuietly(in);
		}
	}

	/**
	 * 读取KeyStore文件<br>
	 * KeyStore文件用于数字证书的密钥对保存<br>
	 * see: <a href="http://snowolf.iteye.com/blog/391931">...</a>
	 *
	 * @param type     类型
	 * @param in       {@link InputStream} 如果想从文件读取.keystore文件，使用 {@link FileUtil#getInputStream(File)} 读取
	 * @param password 密码，null表示无密码
	 * @return {@link KeyStore}
	 */
	public static KeyStore readKeyStore(final String type, final InputStream in, final char[] password) {
		final KeyStore keyStore = getKeyStore(type);
		try {
			keyStore.load(in, password);
		} catch (final Exception e) {
			throw new CryptoException(e);
		}
		return keyStore;
	}

	/**
	 * 获取{@link KeyStore}对象
	 *
	 * @param type 类型
	 * @return {@link KeyStore}
	 */
	public static KeyStore getKeyStore(final String type) {
		final Provider provider = GlobalProviderFactory.getProvider();
		try {
			return null == provider ? KeyStore.getInstance(type) : KeyStore.getInstance(type, provider);
		} catch (final KeyStoreException e) {
			throw new CryptoException(e);
		}
	}
}
