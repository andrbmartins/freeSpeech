package org.academiadecodigo.bootcamp8.freespeach.shared.message;

import org.academiadecodigo.bootcamp8.freespeach.shared.Values;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class Message<T> implements Sendable<T> {

    private static final long serialVersionUID = Values.UID_MESSAGE;

    private final MessageType type;
    private final T content;

    public Message(MessageType type, T content) {
        this.content = content;
        this.type = type;
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public T getContent() {
        return content;
    }

    @Override
    public Sendable<T> updateMessage(MessageType type, T content) {
        return new Message<>(type,content);
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", content=" + content +
                '}';
    }

}