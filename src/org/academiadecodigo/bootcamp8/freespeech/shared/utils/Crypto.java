package org.academiadecodigo.bootcamp8.freespeech.shared.utils;

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

    private Key symmetricKey;
    private Key foreignPublicKey;
    private KeyPair nativeKeyPair;

    /**
     * Private constructor to prevent direct object initialization
     */
    private Crypto() {
        init();
    }

    /**
     * Singleton method
     * @return an instance of crypto
     */
    public static Crypto getInstance() {

        if (instance == null) {

            synchronized (Crypto.class) {

                if (instance == null) {
                    instance = new Crypto();
                }

            }

        }

        return instance;

    }

    private void init() {

        try {

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(3072);
            nativeKeyPair = keyGen.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Invalid encryption algorithm or invalid provider :: " + e.getMessage());
        }

    }

    /**
     * This method is responsible for encrypt the object
     * @param object to encrypt
     * @param key key to encrypt
     * @return an encrypt (sealed) object
     */
    public SealedObject encryptObject(Serializable object, Key key) {

        SealedObject sealed = null;
        Cipher cipher = getCipher(key);

        try {

            sealed = new SealedObject(object, cipher);

        } catch (IOException e) {
            System.err.println("Failure on encapsulate object :: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.err.println("Object is too big to encrypt :: " + e.getMessage());
        }

        return sealed;

    }

    /**
     * This method is responsible for decrypt the object
     * @param sealedObject the object to decrypt
     * @param key the symmetricKey to decrypt
     * @return an object
     */
    public Object decryptObject(SealedObject sealedObject, Key key) {

        Object object = null;

        try {

            object = sealedObject.getObject(key);

        } catch (IOException e) {
            System.err.println("Failure on de-encapsulate object :: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid cast due to unrecognized object :: " + e.getMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            System.err.println("Failure on decrypt operation :: " + e.getMessage());
        }

        return object;

    }

    /**
     * This method is responsible for decrypt the object using the private key
     * @param sealedObject the object to decrypt
     * @return an object
     */
    public Object decryptObject(SealedObject sealedObject) {

        return decryptObject(sealedObject, nativeKeyPair.getPrivate());

    }

    /**
     * createCipher method is responsible for creating Cipher object for encryption
     * @param key to cipher
     * @return the initialized cipher
     */
    private Cipher getCipher(Key key) {

        Cipher cipher = null;

        try {

            cipher = Cipher.getInstance(key.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, key);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            System.err.println("Failure on cipher creation :: " + e.getMessage());
        }

        return cipher;

    }

    /**
     * Set the symmetricKey using an generated key
     */
    public void setSymmetricKey() {

        try {

            symmetricKey = KeyGenerator.getInstance("Blowfish").generateKey();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Invalid encryption algorithm :: " + e.getMessage());
        }

    }

    /**
     * Set the symmetricKey
     * @param key the pretended symmetric key
     */
    public void setSymmetricKey(Key key) {

        symmetricKey = key;

    }

    public void setForeignPublicKey(Key foreignPublicKey) {
        this.foreignPublicKey = foreignPublicKey;
    }

    public Key getSymmetricKey() {
        return symmetricKey;
    }

    public Key getForeignPublicKey() {
        return foreignPublicKey;
    }

    public Key getNativePublicKey() {
        return nativeKeyPair.getPublic();
    }

}
