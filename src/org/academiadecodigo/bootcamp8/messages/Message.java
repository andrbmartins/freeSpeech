package org.academiadecodigo.bootcamp8.messages;

import org.academiadecodigo.bootcamp8.client.utils.Values;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class Message implements Sendable {

    private static final long serialVersionUID = Values.UID_MESSAGE;

    private String sender;
    private String message;
    private String destination;
    private String password;
    private Type type;

    public Message(String sender, String message, String destination, Type type) {
        this.sender = sender;
        this.message = message;
        this.destination = destination;
        this.type = type;
    }

    public Message(String sender, String password) {
        this.sender = sender;
        this.password = password;
    }

    public String getSender() {
        return sender;
    }

    public String getDestination() {
        return destination;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return this.message;
    }

    private enum Type {
        TEXT,
        LOGIN,
        CMD
    }
}