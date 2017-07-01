package org.academiadecodigo.bootcamp8.freespeech.shared.utils;

import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.SealedMessage;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.SealedSendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;

import javax.crypto.*;
import java.io.IOException;
import java.io.Serializable;
import java.security.*;

/**
 * @author by André Martins <Code Cadet>
 *         freeSpeach (25/06/2017)
 *         <Academia de Código_>
 */
public class Crypto {

    private Key symmetricKey;
    private Key foreignPublicKey;
    private KeyPair nativeKeyPair;

    /**
     * Constructor
     */
    public Crypto() {
        generateKeyPair();
    }

    /**
     * Generate the key pair using an RSA algorithm
     */
    private void generateKeyPair() {

        try {

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(4096);
            nativeKeyPair = keyGen.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Invalid encryption algorithm or invalid provider :: " + e.getMessage());
        }

    }

    /**
     * Generate the symmetricKey using an generated key
     */
    public void generateSymmetricKey() {

        try {

            symmetricKey = KeyGenerator.getInstance("Blowfish").generateKey();


        } catch (NoSuchAlgorithmException e) {
            System.err.println("Invalid encryption algorithm :: " + e.getMessage());
        }

    }

    /**
     * This method is responsible for encrypt the object
     *
     * @param object to encrypt
     * @param key    key to encrypt
     * @return an encrypt (sealed) object
     */
    public SealedSendable encryptObject(MessageType type, Serializable object, Key key) {

        SealedSendable sealed = null;

        try {

            Cipher cipher = getCipher(key);
            sealed = new SealedMessage(type, object, cipher);

        } catch (IOException e) {
            System.err.println("Failure on encapsulate object :: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.err.println("Object is too big to encrypt :: " + e.getMessage());
        }

        return sealed;

    }

    /**
     * This method is responsible for decrypt the object
     *
     * @param sealedObject the object to decrypt
     * @param key          the symmetricKey to decrypt
     * @return an object
     */
    public Sendable decryptObject(SealedSendable sealedObject, Key key) {

        Sendable object = null;

        try {

            object = sealedObject.getContent(key);

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
     *
     * @param sealedObject the object to decrypt
     * @return an object
     */
    public Object decryptObject(SealedSendable sealedObject) {

        return decryptObject(sealedObject, symmetricKey);

    }

    public Object decryptObjectWithPrivate(SealedSendable sealedSendable) {

        return decryptObject(sealedSendable, nativeKeyPair.getPrivate());

    }

    /**
     * createCipher method is responsible for creating Cipher object for encryption
     *
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

    public void setSymmetricKey(Key symmetricKey) {
        this.symmetricKey = symmetricKey;
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
