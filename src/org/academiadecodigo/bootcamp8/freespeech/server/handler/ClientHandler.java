package org.academiadecodigo.bootcamp8.freespeech.server.handler;

import org.academiadecodigo.bootcamp8.freespeech.server.Server;
import org.academiadecodigo.bootcamp8.freespeech.server.service.UserService;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.*;
import org.academiadecodigo.bootcamp8.freespeech.server.model.User;
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
    private UserService userService;
    private boolean run;

    public ClientHandler(Server server, Socket clientSocket, Key key, UserService userService) {
        crypto = new Crypto();
        crypto.setSymKey(key);
        this.clientSocket = clientSocket;
        this.server = server;
        this.userService = userService;
        run = true;
    }

    @Override
    public void run() {

        openStreams(clientSocket);
        exchangeKeys();
        authenticateClient();
        server.addUser(this);

        readFromClient();


    }


    private void openStreams(Socket socket) {
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

        return userService.authenticate(username, password);
    }

    private boolean makeRegistry(HashMap<String, String> mapR) {

        String username = mapR.get(Values.NAME_KEY);

        synchronized (userService) {

            if (userService.getUser(username) == null) {

                return userService.addUser(new User(username, mapR.get(Values.PASSWORD_KEY)));

            }
        }
        return false;
    }


    private void readFromClient() {
        SealedSendable msg;

        while (run) {
            if ((msg = Stream.readSendable(objectInputStream)) != null) {
                handleMessage(msg);
                continue;
            }
            server.removeUser(this);
            run = false;
        }

        server.removeUser(this);
    }

    private void handleMessage(SealedSendable msg) {

        MessageType type = msg.getType();

        switch (type) {
            case REPORT:
                //TODO make this a method and log it
                Sendable<String> message = crypto.decryptSendable(msg, crypto.getSymKey());
                String reportedUser = message.getContent(String.class);
                System.out.println("REPORTED " + reportedUser);
                System.out.println(reportedUser.getClass().getSimpleName());
                break;
            case TEXT:
                server.writeToAll(msg);
                break;
            case DATA:
            case PRIVATE_TEXT:
                server.write(msg);
                break;
            case OWN_BIO:
                sendUserBio(msg);
                break;
            case BIO_UPDATE:
                updateBio(msg);
                break;
            case PASS_CHANGE:
                changePass(msg, type);
                break;
            case EXIT:
                run = false;
                server.removeUser(this);
                write(msg);
                closeSocket();
                break;
            case BIO:
                sendUserBio(msg);
                break;
            case DELETE_ACCOUNT:
                if (deleteAccount(msg, type)){
                    run = false;
                    server.removeUser(this);
                    closeSocket();
                }
                break;


        }
    }

    private void updateBio(SealedSendable msg) {
        Sendable<List> message = (Sendable<List>) crypto.decryptSendable(msg, crypto.getSymKey());
        List<String> updatedBio = message.getContent(List.class);

        Sendable<String> userReply;

        if (userService.updateBio(updatedBio)) {
            userReply = new Message<>(Values.BIO_UPDATED);
        } else {
            userReply = new Message<>(Values.BIO_NOT_UPDATED);
        }

        SealedSendable sealedMsg = crypto.encrypt(msg.getType(), userReply, crypto.getSymKey());
        write(sealedMsg);
    }

    // Retrieve bio from database and send to client
    private void sendUserBio(SealedSendable msg) {

        Sendable message = crypto.decryptSendable(msg, crypto.getSymKey());

        List<String> messagebio = userService.getUserBio((String) message.getContent(String.class));

        Message<List> bio = new Message<>(messagebio);
        SealedSendable sealedMessage = crypto.encrypt(msg.getType(), bio, crypto.getSymKey());
        write(sealedMessage);

    }

    private void changePass(SealedSendable msg, MessageType type) {
        Sendable<HashMap> sendable;
        sendable = (Sendable<HashMap>) crypto.decrypt(msg, crypto.getSymKey());
        HashMap<String, String> map = sendable.getContent(HashMap.class);
        Sendable<String> message;
        if (userService.changePassword(clientName, map.get(Values.PASSWORD_KEY),
                map.get(Values.NEW_PASSWORD))) {
            message = new Message<>(Values.PASS_CHANGED);
        } else {
            message = new Message<>(Values.PASS_NOT_CHANGED);
        }

        SealedSendable sealedMsg = crypto.encrypt(type, message, crypto.getSymKey());
        write(sealedMsg);
    }

    private boolean deleteAccount(SealedSendable msg, MessageType type) {
        Sendable<String> sendable = (Sendable<String>) crypto.decrypt(msg, crypto.getSymKey());
        String pass = sendable.getContent(String.class);
        Sendable<String> response;
        boolean deleted;

        if (userService.deleteAccount(clientName, pass)) {
            response = new Message<>(Values.ACC_DELETED);
            deleted = true;
        } else {
            response = new Message<>(Values.NOT_VALIDATED);
            deleted = false;
        }

        SealedSendable sealedMsg = crypto.encrypt(type, response, crypto.getSymKey());
        write(sealedMsg);
        return deleted;
    }

    public void sendUsersList(Sendable userList) {
        SealedSendable sealedSendable = crypto.encrypt(MessageType.USERS_ONLINE, userList, crypto.getSymKey());
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
