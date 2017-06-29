package org.academiadecodigo.bootcamp8.freespeach.server;

import org.academiadecodigo.bootcamp8.freespeach.server.communication.Communication;
import org.academiadecodigo.bootcamp8.freespeach.server.communication.CommunicationService;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeach.server.utils.User;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

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
        //authenticateClient(); //temporarily off to test receiving and sending msg from client
        server.addActiveUser(this); //to be removed after login gets activated
        readFromClient();

    }


    private void authenticateClient() throws IOException, ClassNotFoundException {

        Sendable sendable = null;
        boolean exit = false;
        String message = "";

        while (!exit) {

            sendable = communication.retrieveMessage();

            if (sendable.getType() == MessageType.LOGIN) {

                if(exit = makeLogIn(sendable)){
                    message = "OK";
                    server.addActiveUser(this);
                    updateUsername(sendable);

                } else {
                    message = "NOTOK";

                }

            }

            if (sendable.getType() == MessageType.REGISTER) {

                if (exit = makeRegistry(sendable)) { //TODO: registry is enough to log in??
                    message = "OK";
                    updateUsername(sendable);

                } else {
                    message = "NOTOK";
                }
            }

            communication.sendMessage(sendable.updateMessage(sendable.getType(), message));
        }
    }

    private void updateUsername(Sendable sendable) {

        HashMap<String,String> content = (HashMap<String,String>) sendable.getContent();
        cypherName = content.get("username");
    }

    private boolean makeLogIn(Sendable sendable) {

        HashMap<String, String> map = (HashMap<String, String>) sendable.getContent();
        String username = map.get("username");
        String password = map.get("password");

        return server.getUserService().authenticate(username,password);

    }

    private boolean makeRegistry(Sendable sendable) {

        HashMap<String, String> mapR = (HashMap<String, String>) sendable.getContent();
        String username = mapR.get("username");
        boolean exit = false;

        synchronized (server.getUserService()) {

            if (server.getUserService().getUser(username) == null) {

                server.getUserService().addUser(new User(mapR.get("username"), mapR.get("password")));
                server.getUserService().notifyAll();
                exit = true;
            } else {
                server.getUserService().notifyAll();
            }
        }
        return exit;
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
        }


    }


    public void write(Sendable sendable) {
        System.out.println(sendable);
        communication.sendMessage(sendable);

    }


    private void closeSocket() {
        try {
            clientSocket.close();

        } catch (IOException e) {
            System.out.println(Thread.currentThread().getName() + ": Olha, fudeu!");
            e.printStackTrace();

        }
    }

    public void setCommunication(Communication communication) {
        this.communication = communication;
    }

    public String getName() {
        return cypherName;
    }
}
