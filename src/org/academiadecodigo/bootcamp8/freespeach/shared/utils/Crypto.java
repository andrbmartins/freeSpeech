package org.academiadecodigo.bootcamp8.freespeach.shared.utils;

import javax.crypto.*;
import java.io.IOException;
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

            keyPair = getKeyPair();
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

    public static KeyPair getKeyPair() {

        KeyPairGenerator keyPairGenerator;
        KeyPair keyPair = null;

        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ENCRYPTION_ALGORITHM);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return keyPair;

    }

    public static Object decrypt(SealedObject sealedObject, Key key) {

        Object object = null;

        try {
            object = sealedObject.getObject(key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return object;

    }

}
