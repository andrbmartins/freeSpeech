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
    private Communication communication;


    public ClientHandler(Server server, Socket clientSocket, Key key) {
        crypto = new Crypto();
        crypto.setSymKey(key);
        this.clientSocket = clientSocket;
        this.server = server;
        communication = new CommunicationService();
    }


    @Override
    public void run() {

        communication.openStreams(clientSocket);

        Stream.write(communication.getObjectOutputStream(), crypto.getPublicKey());
        Key key = (Key) Stream.read(communication.getObjectInputStream());
        crypto.setForeignKey(key);

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

            sendable = (Sendable<HashMap>) crypto.decryptWithPrivate(sealedSendable);
            HashMap<String, String> map = sendable.getContent(HashMap.class);

            System.out.println("TYPE " + sealedSendable.getType());

            //TODO switch
            if (sealedSendable.getType() == MessageType.LOGIN) {
                System.out.println("ENYTERED GET TYPE IF " + sealedSendable.getType());
                if (exit = makeLogIn(map)) {
                    message = Values.LOGIN_OK;
                    clientName = map.get(Values.NAME_KEY);
                    ////server.getUserService().eventlogger(Values.TypeEvent.REGISTER, Values.CLIENT_REGISTED);
                    //exit = true;
                } else {
                    message = Values.LOGIN_FAIL;
                }

            }

            if (sealedSendable.getType() == MessageType.REGISTER) {

                if (makeRegistry(map)) {
                    message = Values.REGISTER_OK;
                    //server.getUserService().eventlogger(Values.TypeEvent.REGISTER, Values.CLIENT_REGISTED);
                } else {
                    message = Values.USER_TAKEN;
                }
            }

            // TODO use correct interface Sendable<TYPE>

            Sendable<String> newSendable = new Message<>(message);
            sealedSendable = crypto.encrypt(sealedSendable.getType(), newSendable, crypto.getForeignKey());
            communication.sendMessage(sealedSendable);


            if (newSendable.getContent(String.class).equals(Values.LOGIN_OK)) {

                Sendable<Key> sendableKey = new Message<>(crypto.getSymKey());
                SealedSendable keySeal = crypto.encrypt(MessageType.KEY, sendableKey, crypto.getForeignKey());

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
        System.out.println(username);
        synchronized (server.getUserService()) {
            System.out.println(username);
            if (server.getUserService().getUser(username) == null) {
                System.out.println(username);
                server.getUserService().addUser(new User(username, mapR.get(Values.PASSWORD_KEY)));

                if(server.getUserService().getUser(username) != null){
                    return true;
                }
                //server.getUserService().notifyAll();
            } else {
                //server.getUserService().notifyAll();
                server.getUserService().eventlogger(Values.TypeEvent.REGISTER, Values.CLIENT_REGISTER_FAILED + "--" + username );
                //server.getUserService().notifyAll();
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
                SealedSendable sealedSendable = crypto.encrypt(MessageType.REQUEST_USERS_ONLINE, message, crypto.getSymKey());
                write(sealedSendable);
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
// Utilizador desligou-se do server
            server.getUserService().eventlogger(Values.TypeEvent.CLIENT, Values.CLIENT_DISCONNECTED + "--" + clientSocket.toString());
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
