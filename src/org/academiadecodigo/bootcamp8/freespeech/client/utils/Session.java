package org.academiadecodigo.bootcamp8.freespeech.client.utils;

import java.net.Socket;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */


//TODO documentation

public class Session {

    private String username;
    private Socket userSocket;

    private static Session instance = null;

    public static Session getInstance() {
        if (instance == null) {
            synchronized (Navigation.class) {
                if (instance == null) {
                   instance = new Session();
                }
            }
        }
        return instance;
    }

    public void setUsername(String username) {
        if (this.username == null) {
            this.username = username;
        }
    }

    public void setUserSocket(Socket userSocket) {
        if (this.userSocket == null) {
            this.userSocket = userSocket;
        }
    }

    public Socket getUserSocket() {
        return userSocket;
    }

    public String getUsername() {
        return username;
    }
}
