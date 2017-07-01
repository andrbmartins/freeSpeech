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

public interface SealedSendable extends Serializable{

    MessageType getType();

    Sendable getContent(Key key) throws ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException, IOException;

}
