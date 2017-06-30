package org.academiadecodigo.bootcamp8.freespeech.server;

import org.academiadecodigo.bootcamp8.freespeech.server.communication.Communication;
import org.academiadecodigo.bootcamp8.freespeech.server.communication.CommunicationService;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.User;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves / Fábio Fernandes
 */

public class ClientHandler implements Runnable {
    private String cypherName;
    private final Socket clientSocket;
    private final Server server;
    private Communication communication;


    public ClientHandler(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
        communication = new CommunicationService();
    }


    @Override
    public void run() {

        communication.openStreams(clientSocket);
        authenticateClient();
        notifyNewUser();
        readFromClient();

    }


    private void authenticateClient() {

        Sendable sendable;
        boolean exit = false;
        String message = "";

        while (!exit) {
            sendable = communication.retrieveMessage();


            if (sendable.getType() == MessageType.LOGIN) {

                if(exit = makeLogIn(sendable)){
                    message = Values.LOGIN_OK;
                    cypherName = ((HashMap<String,String>)(sendable.getContent())).get(Values.NAME_KEY);
                    server.addActiveUser(this);
                    exit = true;
                } else {
                    message = Values.LOGIN_FAIL;
                }

            }

            if (sendable.getType() == MessageType.REGISTER) {

                if (makeRegistry(sendable)) { //TODO: registry is enough to log in??
                    message = Values.REGISTER_OK;
                } else {
                    message = Values.USER_TAKEN;
                }
            }

            // TODO use correct interface Sendable<TYPE>
            communication.sendMessage(sendable.updateMessage(sendable.getType(), message));
        }

    }

    private boolean makeLogIn(Sendable sendable) {

        // TODO use correct interface Sendable<TYPE>
        HashMap<String, String> map = (HashMap<String, String>) sendable.getContent();
        String username = map.get(Values.NAME_KEY);
        String password = map.get(Values.PASSWORD_KEY);

        return server.getUserService().authenticate(username,password);
    }

    private boolean makeRegistry(Sendable sendable) {

        // TODO use correct interface Sendable<TYPE>
        HashMap<String, String> mapR = (HashMap<String, String>) sendable.getContent();
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

        Message<String> message = new Message<>(MessageType.NOTIFICATION, Values.NEW_USER);
        server.writeToAll(message);
    }

    private void readFromClient() {
        Sendable msg;

        while ((msg = communication.retrieveMessage()) != null) {

            handleMessage(msg);
        }
        server.logOutUser(this);
        closeSocket();
    }

    private void handleMessage(Sendable msg) {

        MessageType type = msg.getType();

        switch (type){

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
                write(msg.updateMessage(msg.getType(),list));
                break;
        }
    }


    public void write(Sendable sendable) {
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
