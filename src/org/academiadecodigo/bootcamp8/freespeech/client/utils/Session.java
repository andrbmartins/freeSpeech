package org.academiadecodigo.bootcamp8.freespeech.client.utils;

import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Crypto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private Crypto cryptographer;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    private static Session instance = null;

    private Session() {
        cryptographer = new Crypto();
    }

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
            try {
                System.out.println("SETTING SOCKET");
                this.userSocket = userSocket;
                System.out.println("SETTING OUTPUT");
                outputStream = new ObjectOutputStream(userSocket.getOutputStream());
                //outputStream.flush();
                System.out.println("SETTING INPUT");
                inputStream = new ObjectInputStream(userSocket.getInputStream());

                System.out.println("DONE");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public Crypto getCryptographer() {
        return cryptographer;
    }

    public void close() {
        try {
            userSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
