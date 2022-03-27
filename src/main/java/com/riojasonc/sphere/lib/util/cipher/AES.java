package com.riojasonc.sphere.lib.util.cipher;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AES {
    public static final String BASICKEY = "RioJasonCTsk5410";
    public static final String BASICKEY0 = "SphereTYsoku5410";
    //算法
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    public static String decrypt(String encrypt, String key) {
        try {
            return aesDecrypt(encrypt, key);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String encrypt(String content, String key) {
        try {
            return aesEncrypt(content, key);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String aesEncrypt(String content, String encryptKey) throws Exception {
        return java.util.Base64.getEncoder().encodeToString(aesEncryptToBytes(content, encryptKey));
    }

    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
        return encryptStr.isEmpty() ? null : aesDecryptByBytes(java.util.Base64.getDecoder().decode(encryptStr), decryptKey);
    }

    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));

        return cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
    }

    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);

        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }

    public static String keyComplete(String key) {
        if(key.length() >= 16) {
            key = key.substring(0, 16);
        }
        else {
            int delta = 16 - key.length();
            while(delta > 0) {
                key = key + key.charAt(delta);
                delta--;
            }
        }
        return key;
    }
}