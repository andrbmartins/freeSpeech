package org.academiadecodigo.bootcamp8.server;

import org.academiadecodigo.bootcamp8.server.utils.TempUserService;
import org.academiadecodigo.bootcamp8.server.utils.UserService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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


    private Server(int port) {
        Server.port = port;
    }

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Usage: ChatServer <port>");
            System.exit(1);
        }

        Server server = new Server(Integer.parseInt(args[0]));

        try {
            server.init();
            server.start();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
        server.closeServerSocket();

        }


    }



    private void init() throws IOException {
        serverSocket = new ServerSocket(port);
        cachedPool = Executors.newCachedThreadPool();
        userService = TempUserService.getInstance();


    }

    private void start() throws IOException {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(Thread.currentThread().getName() + ": handshake");
                cachedPool.submit(new ClientHandler(this, clientSocket));
            }


    }


    private void closeServerSocket() {
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
}
