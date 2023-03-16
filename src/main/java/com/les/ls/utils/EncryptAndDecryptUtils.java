package com.les.ls.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.stream.Collectors;

/**
 * 常用算法加密解密工具类
 */
public final class EncryptAndDecryptUtils {

    /**
     * 签名算法
     */
    public static final String SHA1_WITH_RSA = "SHA1WithRSA";
    public static final String SHA256_WITH_RSA = "SHA256withRSA";
    public static final String SHA384_WITH_RSA = "SHA384withRSA";
    public static final String SHA512_WITH_RSA = "SHA512withRSA";
    public static final String MD2_WITH_RSA = "MD2withRSA";
    public static final String MD5_WITH_RSA = "MD5withRSA";
    public static final String NONE_WITH_RSA = "NONEwithRSA";

    public static final String RSA = "RSA";

    /**
     * 秘钥文件名
     */
    private static final String PV_KEY_NAME = "privateKey.keystore";
    private static final String PU_KEY_NAME = "publicKey.keystore";

    public static void main(String[] args) throws Exception {
        generatorKeyPair(RSA, 1024, TerminalUtils.getWorkDir());
        String privateKey = loadKeyByFile(TerminalUtils.getWorkDir() + File.separator + PV_KEY_NAME);
        String publicKey = loadKeyByFile(TerminalUtils.getWorkDir() + File.separator + PU_KEY_NAME);
        String content = "周末出去吃烧烤！";
        System.out.println("content->[" + content + "]");
        String signResult = sign(RSA, SHA512_WITH_RSA, content, privateKey, StandardCharsets.UTF_8);
        System.out.println("signResult->[" + signResult + "]");
        Boolean checkResult = doCheck(RSA, SHA512_WITH_RSA, content, signResult, publicKey, StandardCharsets.UTF_8);
        System.out.println("checkResult->[" + checkResult + "]");
    }

    /**
     * URL编码 (符合FRC1738规范)
     *
     * @param input 待编码的字符串
     * @return 编码后的字符串
     */
    public static String FRC1738EncodeUrl(String input) throws Exception {
        return URLEncoder.encode(input, StandardCharsets.UTF_8.name()).replace("+", "%20").replace("*", "%2A");
    }

    /**
     * 生成随机字符串（大小写字母、数字）
     *
     * @param length 长度
     */
    public static String randomStr(int length) {
        return RandomStringUtils.random(length, CHARS);
    }

    /**
     * SHA1加密，转大写
     *
     * @param content 加密内容
     */
    public static String encryptSHA1(String content) {
        return DigestUtils.sha1Hex(content).toUpperCase();
    }

    /**
     * 签名
     *
     * @param keyAlgorithm  秘钥算法
     * @param signAlgorithm 签名算法
     * @param content       待签名数据
     * @param privateKey    商户私钥
     * @param encode        待签名数据编码
     * @return 签名值
     */
    public static String sign(String keyAlgorithm, String signAlgorithm, String content,
                              String privateKey, Charset encode) throws Exception {
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
        PrivateKey priKey = keyFactory.generatePrivate(priPKCS8);
        java.security.Signature signature = java.security.Signature.getInstance(signAlgorithm);
        signature.initSign(priKey);
        signature.update(content.getBytes(encode));
        return Base64.encodeBase64String(signature.sign());
    }


    /**
     * 签名检查
     *
     * @param keyAlgorithm  秘钥算法
     * @param signAlgorithm 签名算法
     * @param content       待签名数据
     * @param sign          签名值
     * @param publicKey     公钥
     * @param encode        待签名数据字符集编码
     */
    public static Boolean doCheck(String keyAlgorithm, String signAlgorithm, String content,
                                  String sign, String publicKey, Charset encode) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
        byte[] encodedKey = Base64.decodeBase64(publicKey);
        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
        java.security.Signature signature = java.security.Signature.getInstance(signAlgorithm);
        signature.initVerify(pubKey);
        signature.update(content.getBytes(encode));
        return signature.verify(Base64.decodeBase64(sign));
    }


    /**
     * 从文件中输入流中加载公私钥内容
     *
     * @param fileName 公私钥文件地址及文件名
     * @throws Exception 加载公私钥时产生的异常
     */
    public static String loadKeyByFile(String fileName) throws Exception {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            return bufferedReader.lines().collect(Collectors.joining());
        }
    }

    /**
     * 从字符串中加载公钥
     *
     * @param algorithm 算法名称
     * @param keyStr    公钥数据字符串
     * @throws Exception 加载秘钥时产生的异常
     */
    public static PublicKey loadPublicKeyByStr(String algorithm, String keyStr) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(keyStr));
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 从字符串中加载私钥
     *
     * @param algorithm 算法名称
     * @param keyStr    私钥数据字符串
     * @throws Exception 加载秘钥时产生的异常
     */
    public static PrivateKey loadPrivateKeyByStr(String algorithm, String keyStr) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(keyStr));
        return keyFactory.generatePrivate(keySpec);
    }


    /**
     * 私钥加密过程
     *
     * @param algorithm     算法名称
     * @param privateKey    私钥
     * @param plainTextData 明文数据
     * @throws Exception 加密过程中的异常信息
     */
    public static byte[] encrypt(String algorithm, PrivateKey privateKey, byte[] plainTextData)
            throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(plainTextData);
    }


    /**
     * 随机生成密钥对
     *
     * @param algorithm 算法名称
     * @param length    秘钥长度
     * @param filePath  文件路径
     */
    public static void generatorKeyPair(String algorithm, Integer length, String filePath) throws Exception {
        //KeyPairGenerator类用于生成公钥和私钥对
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(algorithm);
        // 初始化密钥对生成器，密钥长度 512/1024/2048/...
        keyPairGen.initialize(length, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        PrivateKey privateKey = keyPair.getPrivate();
        // 得到公钥
        PublicKey publicKey = keyPair.getPublic();
        // 得到公钥字符串
        String publicKeyString = Base64.encodeBase64String(publicKey.getEncoded());
        // 得到私钥字符串
        String privateKeyString = Base64.encodeBase64String(privateKey.getEncoded());
        FileUtils.writeByteArrayToFile(new File(filePath + File.separator + PV_KEY_NAME), privateKeyString.getBytes());
        FileUtils.writeByteArrayToFile(new File(filePath + File.separator + PU_KEY_NAME), publicKeyString.getBytes());
    }

    private static final char[] CHARS = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    };
}
