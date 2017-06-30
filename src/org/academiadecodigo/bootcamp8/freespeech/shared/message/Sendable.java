package org.academiadecodigo.bootcamp8.freespeech.shared.message;

import java.io.Serializable;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public interface Sendable<T> extends Serializable {

    //TODO write comments and

    MessageType getType();


    //TODO <T> ?
    T getContent();

    Sendable<T> updateMessage(MessageType type, T content);

}
