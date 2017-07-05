package org.academiadecodigo.bootcamp8.freespeech.shared.message;

import java.io.Serializable;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public interface Sendable<T> extends Serializable {

    /**
     * Interface Sendable is a wrapper for all the objects to be sealed
     * <p>
     * Get the content of a Sendable
     *
     * @return the content with type T
     */
    T getContent();

}
