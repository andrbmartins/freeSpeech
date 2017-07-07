package org.academiadecodigo.bootcamp8.freespeech.client.utils;

import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Crypto;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class SessionContainer {

    private String username;
    private Socket userSocket;
    private Crypto cryptographer;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    private static SessionContainer instance = null;

    /**
     * Instantiates a SessionContainer and a Crypto.
     */
    private SessionContainer() {
        cryptographer = new Crypto();
    }

    /**
     * Singleton instantiation.
     *
     * @return - the instance.
     */
    public static SessionContainer getInstance() {
        if (instance == null) {
            synchronized (SessionContainer.class) {
                if (instance == null) {
                    instance = new SessionContainer();
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

    public Crypto getCrypto() {
        return cryptographer;
    }

    public ObjectInputStream getInput() {
        return inputStream;
    }

    public ObjectOutputStream getOutput() {
        return outputStream;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Closes the socket.
     */
    public static void close() {
        Stream.close(SessionContainer.getInstance().userSocket);
    }
}
