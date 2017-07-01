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

    /**
     * Singleton instantiation.
     *
     * @return - the instance.
     */
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

    /**
     * Sets property socket and corresponding ObjectStreams.
     *
     * @param userSocket - the socket.
     */
    public void setUserSocket(Socket userSocket) {
        if (this.userSocket == null) {
            setupStreams(userSocket);
        }
    }

    private void setupStreams(Socket userSocket) {
        try {
            this.userSocket = userSocket;
            outputStream = new ObjectOutputStream(userSocket.getOutputStream());
            inputStream = new ObjectInputStream(userSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ObjectOutputStream getOutput() {
        return getInstance().outputStream;
    }

    public static ObjectInputStream getInput() {
        return getInstance().inputStream;
    }

    public static Crypto getCrypto() {
        return getInstance().cryptographer;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Closes the socket.
     */
    public static void close() {
        try {
            getInstance().userSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
