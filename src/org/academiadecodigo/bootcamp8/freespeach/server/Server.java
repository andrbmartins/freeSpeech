package org.academiadecodigo.bootcamp8.freespeach.server;

import org.academiadecodigo.bootcamp8.freespeach.server.utils.TempUserService;
import org.academiadecodigo.bootcamp8.freespeach.server.utils.UserService;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Sendable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves / Fábio Fernandes
 */

public class Server {
    private UserService userService;
    private static int port;
    private static ServerSocket serverSocket = null;
    private ExecutorService cachedPool;
    private CopyOnWriteArrayList<ClientHandler> loggedUsers;


    public Server(int port) {
        Server.port = port;
        loggedUsers = new CopyOnWriteArrayList<>();
    }


    public void init() throws IOException {
        serverSocket = new ServerSocket(port);
        cachedPool = Executors.newCachedThreadPool();
        userService = TempUserService.getInstance();
    }


    public void start() throws IOException {

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println(Thread.currentThread().getName() + ": handshake");
            cachedPool.submit(new ClientHandler(this, clientSocket));
        }

    }


    public void closeServerSocket() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public UserService getUserService() {
        return userService;
    }


    public void addActiveUser(ClientHandler client) {
        loggedUsers.add(client);
    }

    public void logOutUser(ClientHandler client) {
        loggedUsers.remove(client);
    }


    public void writeToAll(Sendable sendable) {
        for (ClientHandler c : loggedUsers) {
            c.write(sendable);

        }
    }

    public void handleRequest(Sendable message) {


    }

    public void requestPrivateChannel(Sendable message, ClientHandler clientHandler) {
        try {

            notifyRequestClient(message, clientHandler.getName());

        }catch (Exception e){

            e.printStackTrace();
            message.updateMessage(MessageType.NOTIFICATION, "Client that you wanted to chat is unavailable.");
            write(clientHandler,message);
            return;
        }

    }

    private void notifyRequestClient(Sendable message, String name) {

        String username = (String) message.getContent();

        for (ClientHandler c : loggedUsers){

            if(c.getName().equals(username)){

                Sendable chatNotification = message.updateMessage(MessageType.NOTIFICATION,name);
            }
        }
    }

    private void write(ClientHandler clientHandler, Sendable message) {

        clientHandler.write(message);
    }
}
