package org.academiadecodigo.bootcamp8.freespeech.shared.message;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
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

public class SealedMessage extends SealedObject implements SealedSendable {

    private MessageType type;

    public SealedMessage(MessageType type, Serializable serializable, Cipher cipher)
            throws IOException, IllegalBlockSizeException {
        super(serializable, cipher);
        this.type = type;
    }

    /**
     * @see SealedSendable#getType()
     */
    @Override
    public MessageType getType() {
        return type;
    }

    /**
     * @see SealedSendable#getContent(Key)
     */
    @Override
    public <T> Sendable<T> getContent(Key key) {

        Sendable<T> sendable = null;

        try {
            sendable = (Sendable<T>) getObject(key);
        } catch (IOException e) {
            //TODO log
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return sendable;
    }
}
