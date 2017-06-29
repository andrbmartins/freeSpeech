package org.academiadecodigo.bootcamp8.freespeach.shared.utils;

import javax.crypto.*;
import java.io.IOException;
import java.io.Serializable;
import java.security.*;

/**
 * @author by André Martins <Code Cadet>
 *         freeSpeach (25/06/2017)
 *         <Academia de Código_>
 */
public final class Crypto {

    private static Crypto instance;

    private Key key;
    private Cipher cipher;

    private Crypto() {

        key = createKey();
        cipher = createCipher(Cipher.ENCRYPT_MODE);

    }

    /**
     * Singleton method
     * @return an instance of crypto
     */
    public static Crypto getInstance() {

        synchronized (Crypto.class) {

            if (instance == null) {
                instance = new Crypto();
            }

        }

        return instance;

    }

    /**
     * Generate secret key using an algorithm
     * @return the secret key
     */
    private Key createKey() {


        Key key = null;

        try {

            /*KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(1024, random);*/

            key = KeyGenerator.getInstance("DES").generateKey();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return key;

    }

    /**
     * createCipher method is responsible for creating Cipher object for encryption and decryption
     * @param mode of cipher
     * @return the initialized cipher
     */
    private Cipher createCipher(int mode) {

        Cipher cipher = null;

        try {

            cipher = Cipher.getInstance("DES");
            cipher.init(mode, key);

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return cipher;

    }

    /**
     * This method is responsible for encrypt the object
     * @param object to encrypt
     * @return an encrypt (sealed) object
     */
    public SealedObject encryptObject(Serializable object) {

        SealedObject sealed = null;

        try {

            sealed = new SealedObject(object, cipher);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return sealed;

    }

    /**
     * This method is responsible for decrypt the object
     * @param sealed the object to decrypt
     * @param key the key to decrypt
     * @return an object
     */
    public Object decryptObject(SealedObject sealed, Key key) {

        Object object = null;

        try {

            object = sealed.getObject(key);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return object;

    }

    public Key getKey() {
        return key;
    }

}
