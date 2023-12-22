package com.chensoul.oauth2.common.util;

import lombok.SneakyThrows;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * TODO Comment
 *
 * @author <a href="mailto:chensoul.eth@gmail.com">chensoul</a>
 * @since TODO
 */
public class RSAUtil {
    public static final String ALGORITHM_KEY = "RSA";

    private RSAUtil() {
    }

    /**
     * @param privateKeyStr
     * @return
     */
    @SneakyThrows
    public static PrivateKey getPrivateKeyFromString(String privateKeyStr) {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr.replaceAll("\n", ""));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_KEY);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * @param publicKeyStr
     * @return
     */
    @SneakyThrows
    public static PublicKey getPublicKeyFromString(String publicKeyStr) {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr.replaceAll("\n", ""));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_KEY);
        return keyFactory.generatePublic(keySpec);
    }
}
