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

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public Sendable getContent(Key key) throws ClassNotFoundException, NoSuchAlgorithmException,
            InvalidKeyException, IOException {

        return (Sendable) getObject(key);

    }
}
