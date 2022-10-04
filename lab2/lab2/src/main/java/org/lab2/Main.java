package org.lab2;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class Main {

    static String certificatePath = "res/public.cer";
    static String privateKeyPath = "res/private.p12";
    static char[] p12Password = "password".toCharArray();
    static char[] keyPassword = "password".toCharArray();

    public static void main(String[] args) throws NoSuchAlgorithmException, CertificateException, NoSuchProviderException, KeyStoreException, IOException, UnrecoverableKeyException, OperatorCreationException, CMSException {
        //Установка Unlimited Strength Jurisdiction Policy
        Security.setProperty("crypto.policy", "unlimited");
        int maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength("AES");
        System.out.println("Max Key Size for AES : " + maxKeySize);

        //Подготовка сертификата и закрытого ключа
        Security.addProvider(new BouncyCastleProvider());
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");

        X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(new FileInputStream(certificatePath));

        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(new FileInputStream(privateKeyPath), p12Password);
        PrivateKey privateKey = (PrivateKey) keystore.getKey("baeldung", keyPassword);

        //Шифрование и дешифрование
        String secretMessage = "This is a Lab 2 test message!";
        System.out.println("Original Message : " + secretMessage);

        byte[] stringToEncrypt = secretMessage.getBytes();
        byte[] encryptedData = Encryptor.encryptData(stringToEncrypt, certificate);
        System.out.println("Encrypted Message : ");
        System.out.println(new String(encryptedData));

        byte[] rawData = Encryptor.decryptData(encryptedData, privateKey);
        String decryptedMessage = new String(rawData);
        System.out.println("Decrypted Message : " + decryptedMessage);

        //Верификация
        byte[] signedData = Encryptor.signData(rawData, certificate, privateKey);
        Boolean check = Encryptor.verifSignData(signedData);
        System.out.print("Final verification check : ");
        if (check)
            System.out.println("Passed");
        else
            System.out.println("Failed!");
    }
}