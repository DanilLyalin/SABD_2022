package main.java.obfuscation;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Obfuscator {
    private static final String PASSWORD = "SABD2022SABD2022";

    static String obfuscate(String value) {
        byte[] encrypted = new byte[0];

        //шифрование данных c помощью AES
        try {
            Key aesKey = new SecretKeySpec(PASSWORD.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            encrypted = cipher.doFinal(value.getBytes());
        } catch(Exception e) {
            e.printStackTrace();
        }
        //Перевод в base64
        String result =  Base64.getEncoder().withoutPadding().encodeToString(encrypted);
        return result;
    }

    static String deobfuscate(String value) {
        String initialValue = "";

        //декодирование base64
        byte[] encrypted = Base64.getDecoder().decode(value);

        //расшифровка AES
        try {
            Key aesKey = new SecretKeySpec(PASSWORD.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            initialValue = new String(cipher.doFinal(encrypted));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException  | IllegalBlockSizeException
                 | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return initialValue;
    }
}
