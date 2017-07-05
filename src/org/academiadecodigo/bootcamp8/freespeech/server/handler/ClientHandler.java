package org.academiadecodigo.bootcamp8.freespeech.server.handler;

import org.academiadecodigo.bootcamp8.freespeech.server.Server;
import org.academiadecodigo.bootcamp8.freespeech.server.service.UserService;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.logger.Logger;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.logger.LoggerMessages;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.logger.TypeEvent;
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

    private final Socket clientSocket;
    private final Server server;
    private final UserService userService;
    private Crypto crypto;
    private String clientName;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public ClientHandler(Server server, Socket clientSocket, Key key, UserService userService) {
        crypto = new Crypto();
        crypto.setSymKey(key);
        this.clientSocket = clientSocket;
        this.server = server;
        this.userService = userService;
    }

    @Override
    public void run() {

        openStreams();
        exchangeKeys();
        authenticateClient();
        server.addUser(this);
        readFromClient();

    }

    private void openStreams() {

        try {

            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

        } catch (IOException e) {
            Logger.getInstance().eventlogger(TypeEvent.SERVER, e.getMessage());
        }

    }

    private void exchangeKeys() {

        write(crypto.getPublicKey());
        Key key = (Key) Stream.read(objectInputStream);
        crypto.setForeignKey(key);

    }

    private void authenticateClient() {

        SealedSendable sealedSendable;

        while (true) {

            sealedSendable = Stream.readSendable(objectInputStream);

            switch (sealedSendable.getType()) {

                case LOGIN:
                    if (login(sealedSendable)) {
                        sendSymKey();
                        return;
                    }
                    break;
                case REGISTER:
                    register(sealedSendable);
                    break;
                default:
                    Logger.getInstance().eventlogger(TypeEvent.CLIENT, LoggerMessages.CLIENT_ILLEGAL_LOG);
                    throw new IllegalArgumentException();

            }

        }

    }

    private void register(SealedSendable sealedSendable) {

        Sendable<HashMap<String, String>> sendable = sealedSendable.getContent(crypto.getPrivateKey());
        HashMap<String, String> register = sendable.getContent();
        String username = register.get(Values.NAME_KEY);

        synchronized (userService) {

            if (userService.getUser(username) == null &&
                    userService.addUser(new User(username, register.get(Values.PASSWORD_KEY)))) {
                Logger.getInstance().eventlogger(TypeEvent.CLIENT, LoggerMessages.CLIENT_REGISTERED + username);
                responseToClient(sealedSendable.getType(), Values.REGISTER_OK);
                return;

            }
        }

        responseToClient(sealedSendable.getType(), Values.REGISTER_FAIL);
        Logger.getInstance().eventlogger(TypeEvent.CLIENT, LoggerMessages.CLIENT_REGISTER_FAILED + username);

    }

    private boolean login(SealedSendable sealedSendable) {

        Sendable<HashMap<String, String>> sendable = sealedSendable.getContent(crypto.getPrivateKey());
        HashMap<String, String> login = sendable.getContent();
        String username = login.get(Values.NAME_KEY);
        String password = login.get(Values.PASSWORD_KEY);

        if (server.userLogged(username)) {
            responseToClient(sealedSendable.getType(), Values.ALREADY_LOGGED);
            Logger.getInstance().eventlogger(TypeEvent.CLIENT, LoggerMessages.CLIENT_ALREADY_LOGGED + username);
            return false;
        }

        if (!userService.authenticate(username, password)) {
            responseToClient(sealedSendable.getType(), Values.LOGIN_FAIL);
            Logger.getInstance().eventlogger(TypeEvent.CLIENT, LoggerMessages.CLIENT_LOGIN_FAILED + username);
            return false;
        }

        responseToClient(sealedSendable.getType(), Values.LOGIN_OK);
        clientName = username;
        Logger.getInstance().eventlogger(TypeEvent.CLIENT, LoggerMessages.CLIENT_LOGIN_OK + username);
        return true;

    }

    private void responseToClient(MessageType type, String message) {

        Sendable<String> sendable = new Message<>(message);
        SealedSendable sealedSendable = crypto.encrypt(type, sendable, crypto.getForeignKey());

        write(sealedSendable);

    }

    private void sendSymKey() {

        Sendable<Key> sendableKey = new Message<>(crypto.getSymKey());
        SealedSendable keySeal = crypto.encrypt(MessageType.KEY, sendableKey, crypto.getForeignKey());

        write(keySeal);

    }

    private void readFromClient() {

        SealedSendable msg;

        while ((msg = Stream.readSendable(objectInputStream)) != null) {

            handleMessage(msg);
        }

        server.removeUser(this);
        Logger.getInstance().eventlogger(TypeEvent.CLIENT, LoggerMessages.CLIENT_DISCONNECTED + clientName);
        Stream.close(clientSocket);

    }

    private void handleMessage(SealedSendable msg) {

        MessageType type = msg.getType();

        switch (type) {
            case REPORT:
                reportUser(msg);
                break;
            case TEXT:
                server.writeToAll(msg);
                break;
            case PRIVATE_DATA:
                server.sendFile(msg);
                break;
            case PRIVATE_TEXT:
                server.write(msg);
                break;
            case BIO:
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
                Logger.getInstance().eventlogger(TypeEvent.CLIENT, LoggerMessages.CLIENT_LOGOUT + clientName);
                server.removeUser(this);
                write(msg);
                Stream.close(clientSocket);
                break;
            case DELETE_ACCOUNT:
                if (deleteAccount(msg, type)) {
                    server.removeUser(this);
                    Stream.close(clientSocket);
                }
                break;
            default:
                throw new IllegalArgumentException();

        }
    }

    private void reportUser(SealedSendable msg) {

        Sendable<String> message = msg.getContent(crypto.getSymKey());
        String reportedUser = message.getContent();
        Logger.getInstance().eventlogger(TypeEvent.CLIENT, reportedUser + LoggerMessages.CLIENT_REPORTED + clientName);

        //TODO finish implementation of reporting
        System.out.println("REPORTED " + reportedUser);
        System.out.println(reportedUser.getClass().getSimpleName());

    }

    private void updateBio(SealedSendable msg) {

        Sendable<List<String>> message = msg.getContent(crypto.getSymKey());
        List<String> updatedBio = message.getContent();

        Sendable<String> userReply;

        if (userService.updateBio(updatedBio)) {
            userReply = new Message<>(Values.BIO_UPDATED);
        } else {
            userReply = new Message<>(Values.BIO_NOT_UPDATED);
        }

        SealedSendable sealedMsg = crypto.encrypt(msg.getType(), userReply);
        write(sealedMsg);
    }

    private void sendUserBio(SealedSendable msg) {

        Sendable<String> message = msg.getContent(crypto.getSymKey());
        List<String> messagebio = userService.getUserBio(message.getContent());

        Message<List> bio = new Message<>(messagebio);
        SealedSendable sealedMessage = crypto.encrypt(msg.getType(), bio);
        write(sealedMessage);

    }

    private void changePass(SealedSendable msg, MessageType type) {

        Sendable<HashMap<String, String>> sendable = msg.getContent(crypto.getSymKey());
        HashMap<String, String> map = sendable.getContent();
        Sendable<String> message;
        String oldPassword = map.get(Values.PASSWORD_KEY);
        String newPassword = map.get(Values.NEW_PASSWORD);

        if (userService.changePassword(clientName, oldPassword, newPassword)) {
            message = new Message<>(Values.PASS_CHANGED);
            Logger.getInstance().eventlogger(TypeEvent.CLIENT, LoggerMessages.CLIENT_PASSWORD + clientName);
        } else {
            message = new Message<>(Values.PASS_NOT_CHANGED);
            Logger.getInstance().eventlogger(TypeEvent.CLIENT, LoggerMessages.CLIENT_PASS_FAILED + clientName);
        }

        SealedSendable sealedMsg = crypto.encrypt(type, message);
        write(sealedMsg);
    }

    private boolean deleteAccount(SealedSendable msg, MessageType type) {

        Sendable<String> sendable = msg.getContent(crypto.getSymKey());
        String pass = sendable.getContent();
        Sendable<String> response;
        boolean deleted;

        if (userService.deleteAccount(clientName, pass)) {
            response = new Message<>(Values.ACC_DELETED);
            deleted = true;
            Logger.getInstance().eventlogger(TypeEvent.CLIENT, LoggerMessages.ACCOUNT_DELETED + clientName);
        } else {
            response = new Message<>(Values.NOT_VALIDATED);
            deleted = false;
            Logger.getInstance().eventlogger(TypeEvent.SERVER, LoggerMessages.ACCOUNT_DEL_FAILED + clientName);
        }

        SealedSendable sealedMsg = crypto.encrypt(type, response);
        write(sealedMsg);
        return deleted;
    }

    public void sendUsersList(Sendable userList) {
        SealedSendable sealedSendable = crypto.encrypt(MessageType.USERS_ONLINE, userList);
        write(sealedSendable);
    }

    public void write(Object object) {

        Stream.write(objectOutputStream, object);
    }

    public String getClientName() {
        return clientName;
    }
}
