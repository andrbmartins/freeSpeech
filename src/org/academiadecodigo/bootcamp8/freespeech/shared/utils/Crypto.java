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

    private KeyPair keyPair;
    private Key symKey;
    private Key foreignKey;

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
            keyPair = keyGen.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Invalid encryption algorithm or invalid provider :: " + e.getMessage());
        }

    }

    /**
     * Generate the symKey using an generated key
     */
    public void generateSymKey() {

        try {

            symKey = KeyGenerator.getInstance("Blowfish").generateKey();


        } catch (NoSuchAlgorithmException e) {
            System.err.println("Invalid encryption algorithm :: " + e.getMessage());
        }

    }

    /**
     * This method is responsible for encrypt an object
     *
     * @param object to encrypt
     * @param key    key to encrypt
     * @return an encrypt (sealed) object
     */
    public SealedSendable encrypt(MessageType type, Serializable object, Key key) {

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
     * @param sealed the object to decrypt
     * @param key    the symKey to decrypt
     * @return an object
     */
    public Object decrypt(SealedSendable sealed, Key key) {

        Object object = null;

        try {

            object = sealed.getContent(key);

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
     * TODO
     *
     * @param sealedSendable
     * @return
     */
    public Object decryptWithPrivate(SealedSendable sealedSendable) {

        return decrypt(sealedSendable, keyPair.getPrivate());

    }

    /**
     * TODO - write comment
     *
     * @param sealed
     * @param key
     * @return
     */
    public Sendable decryptSendable(SealedSendable sealed, Key key) {

        Object object = decrypt(sealed, key);

        return (Sendable) object;

    }

    /**
     * TODO - write comment
     *
     * @param sealed
     * @return
     */
    public Sendable decryptSendable(SealedSendable sealed) {

        return decryptSendable(sealed, symKey);

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

    public void setSymKey(Key symKey) {
        this.symKey = symKey;
    }

    public void setForeignKey(Key foreignKey) {

        if (this.foreignKey == null) {
            this.foreignKey = foreignKey;
        }
    }

    public Key getSymKey() {
        return symKey;
    }

    public Key getForeignKey() {
        return foreignKey;
    }

    public Key getPublicKey() {
        return keyPair.getPublic();
    }

}
