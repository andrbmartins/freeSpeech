package org.academiadecodigo.bootcamp8.message;

import java.io.Serializable;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public interface Sendable extends Serializable{

    //TODO write comments and

    Message.Type getType();
    <T> T getContent();
}
