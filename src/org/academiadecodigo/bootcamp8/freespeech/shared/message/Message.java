package org.academiadecodigo.bootcamp8.freespeech.shared.message;

import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class Message<T> implements Sendable<T> {

    private static final long serialVersionUID = Values.SERIAL_VERSION_UID;
    private final T content;

    public Message(T content) {
        this.content = content;
    }

    /**
     * @see Sendable#getContent()
     */
    @Override
    public T getContent() {
        return content;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "Message{" +
                "content=" + content +
                '}';
    }
}