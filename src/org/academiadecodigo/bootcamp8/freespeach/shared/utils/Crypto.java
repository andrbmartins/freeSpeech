package org.academiadecodigo.bootcamp8.freespeach.shared.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.*;

/**
 * @author by André Martins <Code Cadet>
 *         freeSpeach (25/06/2017)
 *         <Academia de Código_>
 */
public final class Crypto {

    private static final String ENCRYPTION_ALGORITHM = "RSA";

    private KeyPair keyPair;
    private Cipher cipher;

    public Crypto(int mode) {

        try {

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ENCRYPTION_ALGORITHM);
            keyPair = keyPairGenerator.generateKeyPair();
            cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(mode, keyPair.getPrivate());

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    public Crypto(int mode, Key key) {

        try {

            cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(mode, key);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    public Cipher getCipher() {
        return cipher;
    }

    public Key getPublicKey() {
        return keyPair.getPublic();
    }

}
