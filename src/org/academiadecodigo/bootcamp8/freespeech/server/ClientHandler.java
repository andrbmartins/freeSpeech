package org.academiadecodigo.bootcamp8.freespeech.server;

import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.*;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.User;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Crypto;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.io.*;
import java.net.Socket;
import java.security.Key;
import java.util.HashMap;
import java.util.List;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves / Fábio Fernandes
 */

public class ClientHandler implements Runnable {

    private Crypto crypto;
    private String clientName;
    private final Socket clientSocket;
    private final Server server;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public ClientHandler(Server server, Socket clientSocket, Key key) {
        crypto = new Crypto();
        crypto.setSymKey(key);

        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {

        openStreams(clientSocket);
        exchangeKeys();

        authenticateClient();
        notifyNewUser();
        server.addActiveUser(this);

        readFromClient();
    }

    public void openStreams(Socket socket) {
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exchangeKeys() {

        write(crypto.getPublicKey());
        Key key = (Key) Stream.read(objectInputStream);
        crypto.setForeignKey(key);
    }

    private void authenticateClient() {

        SealedSendable sealedSendable;
        Sendable<HashMap> sendable;

        boolean exit = false;
        String message = "";

        while (!exit) {
            sealedSendable = Stream.readSendable(objectInputStream);

            //TODO check casts
            sendable = (Sendable<HashMap>) crypto.decryptWithPrivate(sealedSendable);
            HashMap<String, String> map = sendable.getContent(HashMap.class);

            //TODO switch and indentation
            if (sealedSendable.getType() == MessageType.LOGIN) {
                if (exit = makeLogIn(map)) {
                    message = Values.LOGIN_OK;
                    clientName = map.get(Values.NAME_KEY);
                } else {
                    message = Values.LOGIN_FAIL;
                }
            }

            if (sealedSendable.getType() == MessageType.REGISTER) {

                if (makeRegistry(map)) {
                    message = Values.REGISTER_OK;
                } else {
                    message = Values.REGISTER_FAIL;
                }
            }

            Sendable<String> newSendable = new Message<>(message);
            sealedSendable = crypto.encrypt(sealedSendable.getType(), newSendable, crypto.getForeignKey());

            write(sealedSendable);

            if (newSendable.getContent(String.class).equals(Values.LOGIN_OK)) {

                Sendable<Key> sendableKey = new Message<>(crypto.getSymKey());
                SealedSendable keySeal = crypto.encrypt(MessageType.KEY, sendableKey, crypto.getForeignKey());

                write(keySeal);
            }
        }
    }

    private boolean makeLogIn(HashMap<String, String> login) {

        String username = login.get(Values.NAME_KEY);
        String password = login.get(Values.PASSWORD_KEY);

        return server.getUserService().authenticate(username, password);
    }

    private boolean makeRegistry(HashMap<String, String> mapR) {

        String username = mapR.get(Values.NAME_KEY);

        synchronized (server.getUserService()) {

            if (server.getUserService().getUser(username) == null) {

                return server.getUserService().addUser(new User(username, mapR.get(Values.PASSWORD_KEY)));

            }
        }
        return false;
    }

    private void notifyNewUser() {

        Message<String> message = new Message<>(Values.NEW_USER);
        SealedSendable sealedMessage = crypto.encrypt(MessageType.NOTIFICATION, message, crypto.getSymKey());

        server.writeToAll(sealedMessage);
    }

    private void readFromClient() {
        SealedSendable msg;

        while ((msg = Stream.readSendable(objectInputStream)) != null) {
            handleMessage(msg);
        }

        server.logOutUser(this);
        closeSocket();
    }

    private void handleMessage(SealedSendable msg) {

        MessageType type = msg.getType();

        switch (type) {

            case DATA:
            case TEXT:
                server.writeToAll(msg);
                break;
            case LOGIN:
                //TODO log
                throw new IllegalArgumentException("You've already Logged In");
            case REGISTER:
                //TODO log
                throw new IllegalArgumentException("You've already Register");
            case PRIVATE_DATA:
            case PRIVATE_TEXT:
                server.write(msg);
                break;
            case REQUEST_USERS_ONLINE:
                sendUsersList();
                break;
            case BIO_UPDATE:
                //TODO - what to do in this case?
                break;
            case PASS_CHANGE:
                //TODO - what to do in this case?
                break;
        }
    }

    private void sendUsersList() {

        List<String> list = server.getUsersOnlineList();
        Message<List> message = new Message<>(list);
        SealedSendable sealedSendable = crypto.encrypt(MessageType.REQUEST_USERS_ONLINE, message, crypto.getSymKey());
        write(sealedSendable);
    }

    public void write(Object object) {
        Stream.write(objectOutputStream, object);
    }

    private void closeSocket() {

        try {
            clientSocket.close();

        } catch (IOException e) {
            //TODO log
            System.out.println(Thread.currentThread().getName() + ": could not close socket");
            e.printStackTrace();

        }
    }

    public String getClientName() {
        return clientName;
    }
}
