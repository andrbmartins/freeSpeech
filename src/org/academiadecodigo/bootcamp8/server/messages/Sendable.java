package org.academiadecodigo.bootcamp8.server.messages;

import java.io.Serializable;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public interface Sendable extends Serializable{

    //TODO write comments and

    Type getType();

    <T> T getContent();
}
