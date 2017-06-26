package org.academiadecodigo.bootcamp8.shared.message;

import org.academiadecodigo.bootcamp8.shared.Values;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class Message<T> implements Sendable<T> {

    private static final long serialVersionUID = Values.UID_MESSAGE;

    private final Type type;
    private final T content;

    public Message(Type type, T content) {
        this.content = content;
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public T getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", content=" + content +
                '}';
    }

    public enum Type {
        DATA,
        LOGIN,
        REGISTER,
        COMMAND
    }

}