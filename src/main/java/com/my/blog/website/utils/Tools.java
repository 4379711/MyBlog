package com.my.blog.website.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random;

/**
 * 工具类
 * @author liuyalong
 */
public class Tools {
    private static final Random RANDOM = new Random();

    public static int rand(int min, int max) {
        return RANDOM.nextInt(max) % (max - min + 1) + min;
    }

    public static String enAes(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(encryptedBytes);
    }

    public static String deAes(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] cipherTextBytes = decoder.decode(data);
        byte[] decValue = cipher.doFinal(cipherTextBytes);
        return new String(decValue);
    }

    /**
     * 判断字符串是否为数字和有正确的值
     */
    public static boolean isNumber(String str) {
        return null != str && 0 != str.trim().length() && str.matches("\\d*");
    }
}
