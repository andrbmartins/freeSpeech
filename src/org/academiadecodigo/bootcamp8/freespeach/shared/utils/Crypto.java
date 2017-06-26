package org.academiadecodigo.bootcamp8.freespeach.shared.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author by André Martins <Code Cadet>
 *         freeSpeach (25/06/2017)
 *         <Academia de Código_>
 */
public final class Crypto {

    private SecretKey secretKey;
    private Cipher cipher;

    public Crypto(int mode) {

        try {

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            secretKey = keyGen.generateKey();
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    public Crypto(int mode, SecretKey secretKey) {

        try {

            this.secretKey = secretKey;
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    public Cipher getCipher() {
        return cipher;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

}
