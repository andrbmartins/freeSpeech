package org.academiadecodigo.bootcamp8.freespeach.shared.utils;

import org.academiadecodigo.bootcamp8.freespeach.tests.SealedTest;

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

    private Crypto() {
        // generate secret key using DES algorithm same for encryption and decryption
        try {
            key = KeyGenerator.getInstance("DES").generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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
     * getCipherObject method is responsible for creating Cipher object for encryption and decryption
     * @param mode of cipher
     * @return the initialized cipher
     */
    public Cipher getCipherObject(int mode) {

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
            sealed = new SealedObject(object, getCipherObject(Cipher.ENCRYPT_MODE));
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
     * @return an object
     */
    public Object decryptObject(SealedObject sealed) {

        Object object = null;

        try {

            object = sealed.getObject(getCipherObject(Cipher.DECRYPT_MODE));

        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return object;

    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

}
