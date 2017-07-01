package org.academiadecodigo.bootcamp8.freespeech.shared.message;

import java.io.Serializable;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public interface Sendable<T> extends Serializable {

    //TODO write comments and


    <T> T getContent(Class<T> type);


}
