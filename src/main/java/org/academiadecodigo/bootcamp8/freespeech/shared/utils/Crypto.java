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
     * Construct the object
     * Initialize the keyPair property
     */
    public Crypto() {
        generateKeyPair();
    }

    /**
     * Generate the key pair using an RSA algorithm
     * Initializes the keyPair property
     */
    private void generateKeyPair() {

        try {

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(4096);
            keyPair = keyGen.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Invalid encryption algorithm or invalid provider. " + e.getMessage());
        }

    }

    /**
     * Generate an symmetric key given an algorithm
     */
    public void generateSymKey() {

        try {

            symKey = KeyGenerator.getInstance("DES").generateKey();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Invalid encryption algorithm. " + e.getMessage());
        }

    }

    /**
     * Creates a sealed object with a type, content
     * Encryption cipher is generated given the key
     *
     * @param type    flag for the sealed object
     * @param content to save on the sealed object
     * @param key     key to generate cipher
     * @return SealedSendable
     */
    public SealedSendable encrypt(MessageType type, Serializable content, Key key) {

        SealedSendable sealed = null;

        try {

            Cipher cipher = getCipher(key);
            sealed = new SealedMessage(type, content, cipher);

        } catch (IOException e) {
            System.err.println("Failure on encapsulate content. " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.err.println("Object is too big to encrypt. " + e.getMessage());
        }

        return sealed;

    }

    /**
     * @param type    flag for the sealed object
     * @param content to save on the sealed object
     * @return SealedSendable
     * @see Crypto#encrypt(MessageType, Serializable, Key)
     */
    public SealedSendable encrypt(MessageType type, Serializable content) {
        return encrypt(type, content, symKey);
    }

    /**
     * Creates the Cipher object for encryption
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
            System.err.println("Failure on cipher creation : " + e.getMessage());
        }

        return cipher;

    }

    public void setSymKey(Key symKey) {
        this.symKey = symKey;
    }

    public void setForeignKey(Key foreignKey) {
        this.foreignKey = foreignKey;
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

    public Key getPrivateKey() {
        return keyPair.getPrivate();
    }

}
