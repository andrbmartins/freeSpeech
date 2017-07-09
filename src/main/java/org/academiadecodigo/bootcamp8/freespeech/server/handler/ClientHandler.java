package org.academiadecodigo.bootcamp8.freespeech.server.handler;

import org.academiadecodigo.bootcamp8.freespeech.server.Server;
import org.academiadecodigo.bootcamp8.freespeech.server.service.UserService;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.logger.Logger;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.logger.LoggerMessages;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.logger.TypeEvent;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.communication.MapKey;
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
    private boolean run;
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
        run = true;
    }

    @Override
    public void run() {

        openStreams();
        exchangeKeys();
        authenticateClient();
        server.addUser(this);
        readFromClient();

    }

    /**
     * Instantiates the Object Streams
     */
    private void openStreams() {

        try {

            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

        } catch (IOException e) {
            Logger.getInstance().eventlogger(TypeEvent.SERVER, e.getMessage());
        }
    }

    /**
     * Execute the exchange of the public keys between server and client.
     */
    private void exchangeKeys() {
        write(crypto.getPublicKey());
        Key key = (Key) Stream.read(objectInputStream);
        crypto.setForeignKey(key);
    }

    /**
     * Execute the authentification of the client
     */
    private void authenticateClient() {

        SealedSendable sealedSendable;

        while (true) {

            sealedSendable = Stream.readSendable(objectInputStream);

            if (sealedSendable == null) {
                continue;
            }

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

            }

        }

    }

    /**
     * Method responsible for the register of the client
     * @param sealedSendable Encrypted message that contains the username and the password of the user
     */
    private void register(SealedSendable sealedSendable) {

        Sendable<HashMap<MapKey, String>> sendable = sealedSendable.getContent(crypto.getPrivateKey());
        HashMap<MapKey, String> register = sendable.getContent();
        String username = register.get(MapKey.USERNAME);

        synchronized (userService) {

            if (userService.getUser(username) == null &&
                    userService.addUser(new User(username, register.get(MapKey.PASSWORD)))) {
                Logger.getInstance().eventlogger(TypeEvent.CLIENT, LoggerMessages.CLIENT_REGISTERED + username);
                responseToClient(sealedSendable.getType(), Values.REGISTER_OK);
                return;
            }
        }

        responseToClient(sealedSendable.getType(), Values.REGISTER_FAIL);
        Logger.getInstance().eventlogger(TypeEvent.CLIENT, LoggerMessages.CLIENT_REGISTER_FAILED + username);

    }

    /**
     * Method responsible for the login of the client
     * @param sealedSendable Encrypted message that contains the username and the password of the user
     * @return Returns true if the log in was successful and false otherwise
     */
    private boolean login(SealedSendable sealedSendable) {

        Sendable<HashMap<MapKey, String>> sendable = sealedSendable.getContent(crypto.getPrivateKey());
        HashMap<MapKey, String> login = sendable.getContent();
        String username = login.get(MapKey.USERNAME);
        String password = login.get(MapKey.PASSWORD);

        if (userService.verifyUserReported(username) > Values.MAX_REPORT_USER) {
            responseToClient(sealedSendable.getType(), Values.REPORTED);
            Logger.getInstance().eventlogger(TypeEvent.CLIENT, LoggerMessages.CLIENT_BLOCKED + username);
            return false;
        }

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

    /**
     * Method responsible for responding to the client if the login or register was successful or not
     * @param type Type of message: LOGIN or REGISTER
     * @param message Message to send to the client
     */
    private void responseToClient(MessageType type, String message) {

        Sendable<String> sendable = new Message<>(message);
        SealedSendable sealedSendable = crypto.encrypt(type, sendable, crypto.getForeignKey());

        write(sealedSendable);

    }

    /**
     * Sends to the client the symmetric key
     */
    private void sendSymKey() {

        Sendable<Key> sendableKey = new Message<>(crypto.getSymKey());
        SealedSendable keySeal = crypto.encrypt(MessageType.KEY, sendableKey, crypto.getForeignKey());

        write(keySeal);

    }

    /**
     * Method responsible to read the client messages and delegate to the handleMessage what to do with those messages
     */
    private void readFromClient() {

        SealedSendable msg;

        int connect = 0;
        while (run && connect < Values.MAX_CONNECT_ATTEMPT) {

            if ((msg = Stream.readSendable(objectInputStream)) == null) {
                connect++;
                continue;
            }

            handleMessage(msg);
            connect = 0;
        }

        server.removeUser(this);
        Logger.getInstance().eventlogger(TypeEvent.CLIENT, LoggerMessages.CLIENT_DISCONNECTED + clientName);
        Stream.close(clientSocket);

    }
    //TODO and this????
    private class MessageHandler implements Runnable {

        private final SealedSendable msg;

        public MessageHandler(SealedSendable msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            handleMessage(msg);
        }
    }

    /**
     * Calls the right method for the received message.
     * @param msg Message received
     */
    private void handleMessage(SealedSendable msg) {

        MessageType type = msg.getType();

        switch (type) {
            case REPORT:
                reportUser(msg);
                break;
            case TEXT:
                server.writeToAll(msg);
                break;
            case DATA:
                server.sendFile(msg);
                break;
            case PRIVATE_TEXT:
                server.write(msg);
                break;
            case PROFILE:
            case BIO:
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
                Logger.getInstance().eventlogger(TypeEvent.CLIENT, LoggerMessages.CLIENT_LOGOUT + clientName);
                write(msg);
                server.removeUser(ClientHandler.this);
                Stream.close(clientSocket);
                break;
            case DELETE_ACCOUNT:
                if (deleteAccount(msg, type)) {
                    run = false;
                    server.removeUser(ClientHandler.this);
                    Stream.close(clientSocket);
                }
                break;
            default:
                throw new IllegalArgumentException();

        }
    }

    /**
     * Reports the user indicated in the message msg
     * @param msg Name of the reported user
     */
    private void reportUser(SealedSendable msg) {

        Sendable<String> message = msg.getContent(crypto.getSymKey());
        String reportedUser = message.getContent();
        Sendable<String> userReply;

        if (userService.verifyReport(clientName, reportedUser) == 0 ){
            userService.reportUser(clientName,reportedUser);
            Logger.getInstance().eventlogger(TypeEvent.CLIENT, reportedUser + LoggerMessages.CLIENT_REPORTED + clientName);
            userReply = new Message<>(Values.REPORT_OK);
        }
        else {
            userReply = new Message<>(Values.REPORT_KO);
        }

        SealedSendable sealedMsg = crypto.encrypt(msg.getType(), userReply);
        write(sealedMsg);
    }

    /**
     * Updates the Biography of the user with the content of the received message
     * @param msg Message received
     */
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

    /**
     * Sends to the client his biography
     * @param msg Message containing the biography
     */
    private void sendUserBio(SealedSendable msg) {

        Sendable<String> message = msg.getContent(crypto.getSymKey());
        List<String> messagebio = userService.getUserBio(message.getContent());

        Message<List<String>> bio = new Message<>(messagebio);
        SealedSendable sealedMessage = crypto.encrypt(msg.getType(), bio);
        write(sealedMessage);

    }

    /**
     * Changes the client password.
     * @param msg Encrypted message containing the old password and the new one
     * @param type Message type
     */
    private void changePass(SealedSendable msg, MessageType type) {

        Sendable<HashMap<MapKey, String>> sendable = msg.getContent(crypto.getSymKey());
        HashMap<MapKey, String> map = sendable.getContent();
        Sendable<String> message;
        String oldPassword = map.get(MapKey.PASSWORD);
        String newPassword = map.get(MapKey.NEW_PASSWORD);

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

    /**
     * Deletes the client account from the server DB
     * @param msg Message received with the request to delete the account
     * @param type Message type
     * @return Returns if the operation was successfull
     */
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

    /**
     * Sends a list containing the names of the users currently online
     * @param userList message containing a list of the users online
     */
    public void sendUsersList(Sendable userList) {
        SealedSendable sealedSendable = crypto.encrypt(MessageType.USERS_ONLINE, userList);
        write(sealedSendable);
    }

    /**
     * Sends a message to the client.
     * @param object Message to send
     */
    public void write(Object object) {
        Stream.write(objectOutputStream, object);
    }

    /**
     * Returns the client name
     * @return Returns the client name
     */
    public String getClientName() {
        return clientName;
    }
}
