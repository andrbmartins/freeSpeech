package org.academiadecodigo.bootcamp8.freespeech.server;

import org.academiadecodigo.bootcamp8.freespeech.server.utils.TempUserService;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.UserService;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;

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
        for (ClientHandler c: loggedUsers) {
            c.write(sendable);

        }
    }
}
