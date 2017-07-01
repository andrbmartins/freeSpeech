package org.academiadecodigo.bootcamp8.freespeech.server;

import org.academiadecodigo.bootcamp8.freespeech.server.communication.Communication;
import org.academiadecodigo.bootcamp8.freespeech.server.communication.CommunicationService;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.*;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.User;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Crypto;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

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
    private String clientName;
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

        Stream.writeObject(communication.getObjectOutputStream(), crypto.getNativePublicKey());
        Key key = (Key) Stream.readObject(communication.getObjectInputStream());
        crypto.setForeignPublicKey(key);

        authenticateClient();
        notifyNewUser();
        server.addActiveUser(this);
        readFromClient();

    }

    private void authenticateClient() {

        SealedSendable sealedSendable;
        Sendable<HashMap> sendable;
        boolean exit = false;
        String message = "";

        while (!exit) {

            sealedSendable = communication.retrieveMessage();

            sendable = (Sendable<HashMap>) crypto.decryptObjectWithPrivate(sealedSendable);
            HashMap<String, String> map = sendable.getContent(HashMap.class);

            System.out.println("TYPE " + sealedSendable.getType());


            //TODO switch
            if (sealedSendable.getType() == MessageType.LOGIN) {
                System.out.println("ENYTERED GET TYPE IF " + sealedSendable.getType());
                if (exit = makeLogIn(map)) {
                    message = Values.LOGIN_OK;
                    clientName = map.get(Values.NAME_KEY);
                    //exit = true;
                } else {
                    message = Values.LOGIN_FAIL;
                }

            }

            if (sealedSendable.getType() == MessageType.REGISTER) {

                if (makeRegistry(map)) {
                    message = Values.REGISTER_OK;
                } else {
                    message = Values.USER_TAKEN;
                }
            }

            // TODO use correct interface Sendable<TYPE>

            Sendable<String> newSendable = new Message<>(message);
            sealedSendable = crypto.encryptObject(sealedSendable.getType(), newSendable, crypto.getForeignPublicKey());
            communication.sendMessage(sealedSendable);


            if (newSendable.getContent(String.class).equals(Values.LOGIN_OK)) {

                Sendable<Key> sendableKey = new Message<>(crypto.getSymmetricKey());
                SealedSendable keySeal = crypto.encryptObject(MessageType.KEY, sendableKey, crypto.getForeignPublicKey());

                communication.sendMessage(keySeal);
            }
        }


    }

    private boolean makeLogIn(HashMap<String, String> login) {

        // TODO use correct interface Sendable<TYPE>
        String username = login.get(Values.NAME_KEY);
        String password = login.get(Values.PASSWORD_KEY);
        return server.getUserService().authenticate(username, password);

    }

    private boolean makeRegistry(HashMap<String, String> mapR) {

        // TODO use correct interface Sendable<TYPE>
        String username = mapR.get(Values.NAME_KEY);

        synchronized (server.getUserService()) {

            if (server.getUserService().getUser(username) == null) {

                server.getUserService().addUser(new User(username, mapR.get(Values.PASSWORD_KEY)));
                //server.getUserService().notifyAll();
                return true;
            } else {
                //server.getUserService().notifyAll();
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
        return clientName;
    }
}
