package org.academiadecodigo.bootcamp8.freespeech.server;

import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
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
        //TODO do we really need 2 more layers of encapsulation?
        communication = new CommunicationService();
    }

    @Override
    public void run() {

        communication.openStreams(clientSocket);
        exchangeKeys();

        authenticateClient();
        notifyNewUser();
        server.addActiveUser(this);

        readFromClient();
    }

    private void exchangeKeys() {

        Stream.write(communication.getObjectOutputStream(), crypto.getPublicKey());
        Key key = (Key) Stream.read(communication.getObjectInputStream());
        crypto.setForeignKey(key);
    }

    private void authenticateClient() {

        SealedSendable sealedSendable;
        Sendable<HashMap> sendable;

        boolean exit = false;
        String message = "";

        while (!exit) {
            sealedSendable = communication.retrieveMessage();

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
                    //authenticateClient();

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

            communication.sendMessage(sealedSendable);

            if (newSendable.getContent(String.class).equals(Values.LOGIN_OK)) {

                Sendable<Key> sendableKey = new Message<>(crypto.getSymKey());
                SealedSendable keySeal = crypto.encrypt(MessageType.KEY, sendableKey, crypto.getForeignKey());

                communication.sendMessage(keySeal);
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

                server.getUserService().addUser(new User(username, mapR.get(Values.PASSWORD_KEY)));
                return true;
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
        // Introduzir no log server que o client fez logout e se desligou
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
            case BIO: {
                System.out.println("Recebi msg de request de bio" + msg.toString());
                // IF Message is BIO request
                sendUserBio(msg);
                break;
            }
        }
    }

    // Retrieve bio from database and send to client
    private void sendUserBio(SealedSendable msg) {
        System.out.println("Vou mandar a mesma message que recebi para testar" + msg );

        Sendable message = crypto.decryptSendable(msg, crypto.getSymKey());

        System.out.println("Mensagem desemcrytada enviada pelo cliente" + message.getContent(String.class));
        // Aqui vou buscar a bio ha BD atraves da query (Tem de retornar a bio)

        List<String> messagebio = server.getUserService().getUserBio((String) message.getContent(String.class));
        Message<List> bio = new Message<>(messagebio);
        SealedSendable sealedMessage = crypto.encrypt(MessageType.BIO, bio, crypto.getSymKey());
        write(sealedMessage);

        System.out.println("Enviei mensagem com bio ao cliente" + sealedMessage.toString());
    }

    private void sendUsersList() {

        List<String> list = server.getUsersOnlineList();
        Message<List> message = new Message<>(list);
        SealedSendable sealedSendable = crypto.encrypt(MessageType.REQUEST_USERS_ONLINE, message, crypto.getSymKey());
        write(sealedSendable);
    }

    public void write(SealedSendable sendable) {
        communication.sendMessage(sendable);
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

    public String getName() {
        return clientName;
    }
}
