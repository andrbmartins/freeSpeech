package org.academiadecodigo.bootcamp8.freespeech.server.handler;

import org.academiadecodigo.bootcamp8.freespeech.server.Server;
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
    private boolean run;

    public ClientHandler(Server server, Socket clientSocket, Key key) {
        crypto = new Crypto();
        crypto.setSymKey(key);
        this.clientSocket = clientSocket;
        this.server = server;
        run = true;
    }

    @Override
    public void run() {

        openStreams(clientSocket);
        exchangeKeys();
        init();

    }

    private void init() {
        authenticateClient();
        //notifyNewUser();
        server.addUser(this);

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

    //TODO deprecated because there is newer faster method
   /* private void notifyNewUser() {

        Message<String> message = new Message<>(Values.NEW_USER);
        SealedSendable sealedMessage = crypto.encrypt(MessageType.NOTIFICATION, message, crypto.getSymKey());

        server.writeToAll(sealedMessage);
    }*/

    private void readFromClient() {
        SealedSendable msg;

        while (run) {
            if ((msg = Stream.readSendable(objectInputStream)) != null) {
                handleMessage(msg);
            } else {
                run = false;
            }
        }
        // Introduzir no log server que o client fez logout e se desligou
        server.remUser(this);
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
            //TODO NO LONGER REQUESTED BUT ALWAYS SENT ON STATE CHANGE Delete switch
            /*case USERS_ONLINE:
                sendUsersList();
                break;*/
            case GET_BIO:
                //TODO
                break;
            case BIO_UPDATE:
                //TODO - what to do in this case?
                break;
            case PASS_CHANGE:
                //TODO - what to do in this case?
                changePass(msg, type);
                break;
            case LOGOUT:
                //TODO not fully working yet
                write(msg);
                server.remUser(this);
                break;
            case EXIT:
                run = false;
                write(msg);
                server.remUser(this);
                closeSocket();
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
        System.out.println("Vou mandar a mesma message que recebi para testar" + msg);

        Sendable message = crypto.decryptSendable(msg, crypto.getSymKey());

        System.out.println("Mensagem desemcrytada enviada pelo cliente" + message.getContent(String.class));
        // Aqui vou buscar a bio ha BD atraves da query (Tem de retornar a bio)

        List<String> messagebio = server.getUserService().getUserBio((String) message.getContent(String.class));
        Message<List> bio = new Message<>(messagebio);
        SealedSendable sealedMessage = crypto.encrypt(MessageType.BIO, bio, crypto.getSymKey());
        write(sealedMessage);

        System.out.println("Enviei mensagem com bio ao cliente" + sealedMessage.toString());
    }

    private void changePass(SealedSendable msg, MessageType type) {
        Sendable<HashMap> sendable;
        sendable = (Sendable<HashMap>) crypto.decrypt(msg, crypto.getSymKey());
        HashMap<String, String> map = sendable.getContent(HashMap.class);
        Sendable<String> message;
        if (server.getUserService().changePassword(clientName, map.get(Values.PASSWORD_KEY),
                map.get(Values.NEW_PASSWORD))) {
            message = new Message<>(Values.PASS_CHANGED);
        } else {
            message = new Message<>(Values.PASS_NOT_CHANGED);
        }

        SealedSendable sealedMsg = crypto.encrypt(type, message, crypto.getSymKey());
        write(sealedMsg);
    }


    public void sendUsersList(Sendable userList) {
        SealedSendable sealedSendable = crypto.encrypt(MessageType.USERS_ONLINE, userList, crypto.getSymKey());
        write(sealedSendable);
    }

    //TODO remove method which is now obsolete by the above method
  /*  private void sendUsersList() {

        List<String> list = server.getUsersOnlineList();
        Message<List> message = new Message<>(list);
        SealedSendable sealedSendable = crypto.encrypt(MessageType.USERS_ONLINE, message, crypto.getSymKey());
        write(sealedSendable);
    }*/

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
