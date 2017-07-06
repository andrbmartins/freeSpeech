package org.academiadecodigo.bootcamp8.freespeech.client.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class Hash {

    private static final String ALGORITHM = "SHA-512";
    private static final int HASH_SIZE = 32;

    /**
     * Returns a String resulting from the hash computation of the specified element.
     * @param string - the specified element.
     * @return the hashed string.
     */
    public static String getHash(String string) {

        String hashText = "";

        try {
            MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
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
