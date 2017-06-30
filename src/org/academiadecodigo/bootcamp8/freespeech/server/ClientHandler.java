package org.academiadecodigo.bootcamp8.freespeech.server;

import org.academiadecodigo.bootcamp8.freespeech.server.communication.Communication;
import org.academiadecodigo.bootcamp8.freespeech.server.communication.CommunicationService;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.*;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.User;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Crypto;

import javax.crypto.IllegalBlockSizeException;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves / Fábio Fernandes
 */

public class ClientHandler implements Runnable {
    private Crypto crypto;
    private String cypherName;
    private final Socket clientSocket;
    private final Server server;
    private Communication communication;


    public ClientHandler(Server server, Socket clientSocket, Key key) {
        crypto = new Crypto();
        crypto.setSymmetricKey(key);
        this.clientSocket = clientSocket;
        this.server = server;
        communication = new CommunicationService();
    }

    @Override
    public void run() {

        communication.openStreams(clientSocket);
        authenticateClient();
        notifyNewUser();
        server.addActiveUser(this);
        readFromClient();

    }

    private void authenticateClient() {

        SealedSendable sendable;
        Sendable sendable1;
        boolean exit = false;
        String message = "";

        while (!exit) {
            sendable = communication.retrieveMessage();
            sendable1 = crypto.decryptObject(sendable, crypto.getSymmetricKey());

            if (sendable.getType() == MessageType.LOGIN) {

                if (exit = makeLogIn(sendable1)) {
                    message = Values.LOGIN_OK;
                    cypherName = ((HashMap<String,String>)sendable1.getContent(HashMap.class)).get(Values.NAME_KEY);
                    exit = true;
                } else {
                    message = Values.LOGIN_FAIL;
                }

            }

            if (sendable.getType() == MessageType.REGISTER) {

                if (makeRegistry(sendable1)) { //TODO: registry is enough to log in??
                    message = Values.REGISTER_OK;
                } else {
                    message = Values.USER_TAKEN;
                }
            }

            // TODO use correct interface Sendable<TYPE>

            sendable1 = sendable1.updateContent(message);
            sendable = crypto.encryptObject(sendable.getType(), sendable1, crypto.getSymmetricKey());
            communication.sendMessage(sendable);
        }

    }

    private boolean makeLogIn(Sendable sendable) {

        // TODO use correct interface Sendable<TYPE>
        HashMap<String, String> map = (HashMap<String, String>)sendable.getContent(HashMap.class);
        String username = map.get(Values.NAME_KEY);
        String password = map.get(Values.PASSWORD_KEY);
        return server.getUserService().authenticate(username, password);

    }

    private boolean makeRegistry(Sendable sendable) {

        // TODO use correct interface Sendable<TYPE>
        HashMap<String, String> mapR = (HashMap<String, String>)sendable.getContent(HashMap.class);
        String username = mapR.get(Values.NAME_KEY);

        synchronized (server.getUserService()) {

            if (server.getUserService().getUser(username) == null) {

                server.getUserService().addUser(new User(username, mapR.get(Values.PASSWORD_KEY)));
                server.getUserService().notifyAll();
                return true;
            } else {
                server.getUserService().notifyAll();
            }
        }
        return false;
    }

    private void notifyNewUser() {

        Message<String> message = new Message<>(Values.NEW_USER);
        SealedSendable sealedMessage = crypto.encryptObject(MessageType.NOTIFICATION, message, crypto.getSymmetricKey());
        server.writeToAll(sealedMessage);
    }

    private void readFromClient() {
        SealedSendable msg;

        while ((msg = communication.retrieveMessage()) != null) {

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
                throw new IllegalArgumentException("You've already Logged In");
            case REGISTER:
                throw new IllegalArgumentException("You've already Register");
            case PRIVATE_DATA:
            case PRIVATE_TEXT:
                server.write(msg);
                break;
            case REQUEST_USERS_ONLINE:
                List<String> list = server.getUsersOnlineList();
                Message<List> message = new Message<>(list);
                SealedSendable sealedSendable = crypto.encryptObject(MessageType.REQUEST_USERS_ONLINE, message, crypto.getSymmetricKey());
                break;
        }
    }

    public void write(SealedSendable sendable) {
        //TODO to remove after tests completed
        System.out.println(sendable);
        communication.sendMessage(sendable);
    }

    private void closeSocket() {
        try {
            clientSocket.close();

        } catch (IOException e) {
            System.out.println(Thread.currentThread().getName() + ": could not close socket");
            e.printStackTrace();

        }
    }

    public String getName() {
        return cypherName;
    }
}
