package org.academiadecodigo.bootcamp8.freespeech.shared.message;

import java.io.Serializable;
import java.security.Key;

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
     */
    <T> Sendable<T> getContent(Key key);

}
