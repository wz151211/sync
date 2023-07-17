package com.ping.syncsearch.utils;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @Author: W.Z
 * @Date: 2022/8/20 21:31
 */
public class TripleDES {


    public static String decrypt(String key, String text, String iv) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "TripleDES");

            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            byte[] encryptBytes = text.getBytes(StandardCharsets.UTF_8);
            Cipher decryptCipher = Cipher.getInstance("TripleDES/CBC/PKCS5Padding");
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
            byte[] decryptedBytes = decryptCipher.doFinal(Base64.getDecoder().decode(encryptBytes));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String key, String text, String iv) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "TripleDES");

            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            byte[] encryptBytes = text.getBytes(StandardCharsets.UTF_8);
            Cipher encryptCipher = Cipher.getInstance("TripleDES/CBC/PKCS5Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
            byte[] encryptedBytes = encryptCipher.doFinal(encryptBytes);
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
