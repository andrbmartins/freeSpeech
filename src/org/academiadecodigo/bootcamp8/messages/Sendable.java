package org.academiadecodigo.bootcamp8.messages;

import java.io.Serializable;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public interface Sendable<T> extends Serializable {

    //TODO write comments and

    Message.Type getType();

    T getContent();

}
