package org.academiadecodigo.bootcamp8.messages;

import org.academiadecodigo.bootcamp8.client.utils.Values;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class Message<T> implements Sendable {

    private static final long serialVersionUID = Values.UID_MESSAGE;

    private Type type;
    private T content;

    public Message(Type type, T content) {
        this.content = content;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public T getContent() {
        return null;
    }

    public enum Type {
        DATA,
        LOGIN,
        COMMAND
    }
}