package org.academiadecodigo.bootcamp8.server;

import org.academiadecodigo.bootcamp8.server.messages.Message;
import org.academiadecodigo.bootcamp8.server.messages.Sendable;
import org.academiadecodigo.bootcamp8.server.messages.Type;
import org.academiadecodigo.bootcamp8.server.utils.User;

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
    private String userName;
    private BufferedWriter outputStream;
    private BufferedReader inputStream;

    public ClientHandler(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {

        boolean logIn = false;


        try {

            buildBufferStreams();
            authenticateClient();


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            closeSocket();
            return;
        }


    }

    private void authenticateClient() throws IOException, ClassNotFoundException {

        Sendable sendable = null;
        boolean exit = false;
        String message = "";

        while (!exit) {

            sendable = (Sendable) inputStream.readLine();

            if (sendable.getType() == Type.LOGIN) {

                if(exit = makeLogIn(sendable)){
                    message = "OK";
                } else {
                    message = "NOTOK";
                }

            }

            if (sendable.getType() == Type.REGISTER) {

                if (exit = makeRegistry(sendable)) { //TODO: registry is enough to log in??
                    message = "OK";
                } else {
                    message = "NOTOK";
                }

            }

            outputStream.writeObject(new Message(Type.REGISTER, message));
        }

    }

    private boolean makeLogIn(Sendable sendable) {

        HashMap<String, String> map = sendable.getContent();
        String username = map.get("username");
        String password = map.get("password");

        return server.getUserService().authenticate(username,password);

    }

    private boolean makeRegistry(Sendable sendable) {

        HashMap<String, String> mapR = sendable.getContent();
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

    private void closeSocket() {

        try {
            clientSocket.close();

        } catch (IOException e) {
            System.out.println(Thread.currentThread().getName() + ": Olha, fudeu!");
            e.printStackTrace();

        }
    }

    private void buildBufferStreams() throws IOException {

        outputStream = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
}
