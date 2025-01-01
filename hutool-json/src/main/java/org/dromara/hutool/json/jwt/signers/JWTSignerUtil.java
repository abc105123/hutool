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

package org.dromara.hutool.json.jwt.signers;

import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.regex.ReUtil;

import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.regex.Pattern;

/**
 * JWT签名器工具类
 *
 * @author Looly
 * @since 5.7.0
 */
public class JWTSignerUtil {

	private static final Pattern ES_ALGORITHM_PATTERN = Pattern.compile("es\\d{3}", Pattern.CASE_INSENSITIVE);

	/**
	 * 无签名
	 *
	 * @return 无签名的签名器
	 */
	public static JWTSigner none() {
		return NoneJWTSigner.NONE;
	}

	//------------------------------------------------------------------------- HSxxx

	/**
	 * HS256(HmacSHA256)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner hs256(final byte[] key) {
		return createSigner("HS256", key);
	}

	/**
	 * HS384(HmacSHA384)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner hs384(final byte[] key) {
		return createSigner("HS384", key);
	}

	/**
	 * HS512(HmacSHA512)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner hs512(final byte[] key) {
		return createSigner("HS512", key);
	}

	//------------------------------------------------------------------------- RSxxx

	/**
	 * RS256(SHA256withRSA)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner rs256(final Key key) {
		return createSigner("RS256", key);
	}

	/**
	 * RS384(SHA384withRSA)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner rs384(final Key key) {
		return createSigner("RS384", key);
	}

	/**
	 * RS512(SHA512withRSA)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner rs512(final Key key) {
		return createSigner("RS512", key);
	}

	//------------------------------------------------------------------------- ESxxx

	/**
	 * ES256(SHA256withECDSA)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner es256(final Key key) {
		return createSigner("ES256", key);
	}

	/**
	 * ES384(SHA383withECDSA)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner es384(final Key key) {
		return createSigner("ES384", key);
	}

	/**
	 * ES512(SHA512withECDSA)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner es512(final Key key) {
		return createSigner("ES512", key);
	}

	/**
	 * HMD5(HmacMD5)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner hmd5(final Key key) {
		return createSigner("HMD5",key);
	}

	/**
	 * HSHA1(HmacSHA1)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner hsha1(final Key key) {
		return createSigner("HSHA1",key);
	}

	/**
	 * SM4CMAC(SM4CMAC)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner sm4cmac(final Key key) {
		return createSigner("SM4CMAC",key);
	}

	/**
	 * RMD2(MD2withRSA)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner rmd2(final Key key) {
		return createSigner("RMD2",key);
	}

	/**
	 * RMD5(MD5withRSA)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner rmd5(final Key key) {
		return createSigner("RMD5",key);
	}

	/**
	 * RSHA1(SHA1withRSA)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner rsha1(final Key key) {
		return createSigner("RSHA1",key);
	}

	/**
	 * DNONE(NONEwithDSA)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner dnone(final Key key) {
		return createSigner("DNONE",key);
	}

	/**
	 * DSHA1(SHA1withDSA)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner dsha1(final Key key) {
		return createSigner("DSHA1",key);
	}

	/**
	 * ENONE(NONEwithECDSA)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner enone(final Key key) {
		return createSigner("ENONE",key);
	}

	/**
	 * ESHA1(SHA1withECDSA)签名器
	 *
	 * @param key 密钥
	 * @return 签名器
	 */
	public static JWTSigner esha1(final Key key) {
		return createSigner("ESHA1",key);
	}

	/**
	 * 创建签名器
	 *
	 * @param algorithmId 算法ID，见{@link AlgorithmUtil}
	 * @param key         密钥
	 * @return 签名器
	 */
	public static JWTSigner createSigner(final String algorithmId, final byte[] key) {
		Assert.notNull(key, "Signer key must be not null!");

		if (null == algorithmId || NoneJWTSigner.ID_NONE.equals(algorithmId)) {
			return none();
		}
		return new HMacJWTSigner(AlgorithmUtil.getAlgorithm(algorithmId), key);
	}

	/**
	 * 创建签名器
	 *
	 * @param algorithmId 算法ID，见{@link AlgorithmUtil}
	 * @param keyPair     密钥对
	 * @return 签名器
	 */
	public static JWTSigner createSigner(final String algorithmId, final KeyPair keyPair) {
		Assert.notNull(keyPair, "Signer key pair must be not null!");

		if (null == algorithmId || NoneJWTSigner.ID_NONE.equals(algorithmId)) {
			return none();
		}

		final String algorithm = AlgorithmUtil.getAlgorithm(algorithmId);
		// issue3205@Github
		if(ReUtil.isMatch(ES_ALGORITHM_PATTERN, algorithmId)){
			return new EllipticCurveJWTSigner(algorithm, keyPair);
		}

		return new AsymmetricJWTSigner(algorithm, keyPair);
	}

	/**
	 * 创建签名器
	 *
	 * @param algorithmId 算法ID，见{@link AlgorithmUtil}
	 * @param key         密钥
	 * @return 签名器
	 */
	public static JWTSigner createSigner(final String algorithmId, final Key key) {
		Assert.notNull(key, "Signer key must be not null!");

		if (null == algorithmId || NoneJWTSigner.ID_NONE.equals(algorithmId)) {
			return NoneJWTSigner.NONE;
		}

		final String algorithm = AlgorithmUtil.getAlgorithm(algorithmId);
		if (key instanceof PrivateKey || key instanceof PublicKey) {
			// issue3205@Github
			if(ReUtil.isMatch(ES_ALGORITHM_PATTERN, algorithmId)){
				return new EllipticCurveJWTSigner(algorithm, key);
			}

			return new AsymmetricJWTSigner(algorithm, key);
		}
		return new HMacJWTSigner(algorithm, key);
	}
}
