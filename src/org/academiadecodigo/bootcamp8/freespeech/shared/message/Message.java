package org.academiadecodigo.bootcamp8.freespeech.shared.message;

import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

import java.io.Serializable;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class Message<T> implements Sendable<T> {

    private static final long serialVersionUID = Values.UID_MESSAGE;

    private final T content;

    public Message(T content) {
        this.content = content;
    }


    @Override
    public <T> T getContent(Class<T> type) {

        return content == null ? null : type.cast(content);

    }

    @Override
    public Sendable<T> updateContent(T content) {
        return new Message<>(content);
    }

    @Override
    public String toString() {
        return "Message{" +
                "content=" + content +
                '}';
    }
}