package org.academiadecodigo.bootcamp8.freespeach.shared.message;

import java.io.Serializable;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public interface Sendable<T> extends Serializable {

    //TODO write comments and

    MessageType getType();

    T getContent();

    Sendable<T> updateMessage(MessageType type, T content);

}
