package com.example.securesearch.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    private static final String KEY = "1234567890123456"; // 16-char key

    public static String encrypt(String data){
        try{
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(),"AES");
            cipher.init(Cipher.ENCRYPT_MODE,keySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        }catch(Exception e){ throw new RuntimeException(e);}
    }

    public static String decrypt(String data){
        try{
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(),"AES");
            cipher.init(Cipher.DECRYPT_MODE,keySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
        }catch(Exception e){ throw new RuntimeException(e);}
    }
}