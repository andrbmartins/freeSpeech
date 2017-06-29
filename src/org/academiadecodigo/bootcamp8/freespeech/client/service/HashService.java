package org.academiadecodigo.bootcamp8.freespeech.client.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */


//TODO documentation - implement on password

public class HashService {

    public static final String ALGORITHM = "SHA-1";
    public static final int HASH_SIZE = 32;
    private static MessageDigest messageDigest = null;

    public static String getHash(String string) {

        String hashText = "";

        try {
            messageDigest = MessageDigest.getInstance(ALGORITHM);
            messageDigest.reset();
            messageDigest.update(string.getBytes());
            byte[] data = messageDigest.digest();

            BigInteger hashValue = new BigInteger(1, data);
            hashText = hashValue.toString(HASH_SIZE);

            while (hashText.length() < HASH_SIZE) {
                hashText = "0" + hashText;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashText;
    }
}
