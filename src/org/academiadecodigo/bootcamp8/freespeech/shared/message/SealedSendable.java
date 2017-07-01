package org.academiadecodigo.bootcamp8.freespeech.shared.message;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public interface SealedSendable extends Serializable {

    /**
     * The SealedSendable defines objects that can be sealed and serializable
     * <p>
     * Return the type of message to be carried
     *
     * @return the type
     * @see MessageType
     */
    MessageType getType();

    /**
     * Unsealed the content and returns it
     *
     * @param key the key to decrypt the content
     * @return the decrypt content with type Sendable
     * @throws ClassNotFoundException Nonexistent class
     * @throws NoSuchAlgorithmException Invalid algorithm
     * @throws InvalidKeyException Invalid key
     * @throws IOException Input / output exception
     */
    Sendable getContent(Key key) throws ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException, IOException;

}
