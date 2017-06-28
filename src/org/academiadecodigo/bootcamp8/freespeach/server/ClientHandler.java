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
        try {
            authenticateClient(); //temporarily off to test receiving and sending msg from client
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
                } else {
                    message = "NOTOK";
                }

            }

            if (sendable.getType() == MessageType.REGISTER) {

                if (exit = makeRegistry(sendable)) { //TODO: registry is enough to log in??
                    message = "OK";
                } else {
                    message = "NOTOK";
                }

            }

            communication.sendMessage(sendable.updateMessage(sendable.getType(), message));
        }

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

            server.writeToAll(msg);

        }
        server.logOutUser(this);
        closeSocket();
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
}
