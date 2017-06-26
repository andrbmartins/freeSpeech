package org.academiadecodigo.bootcamp8.freespeach.server;

import org.academiadecodigo.bootcamp8.freespeach.message.Message;
import org.academiadecodigo.bootcamp8.freespeach.message.Sendable;
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
    private String userName;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;


    public ClientHandler(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
    }


    @Override
    public void run() {

        boolean logIn = false;


        try {

            buildBufferStreams();
            System.out.println("oi");
            authenticateClient();
            System.out.println("ble");


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
            System.out.println("oioioi");
            sendable = (Sendable) objectInputStream.readObject();
            System.out.println("sdghdfkjhg");
            System.out.println(sendable.getType());
            if (sendable.getType() == Message.Type.LOGIN) {

                if(exit = makeLogIn(sendable)){
                    message = "OK";
                    server.addActiveUser(this);
                    //TODO initialize username property after login success
                } else {
                    message = "NOTOK";
                }

            }

            if (sendable.getType() == Message.Type.REGISTER) {

                if (exit = makeRegistry(sendable)) { //TODO: registry is enough to log in??
                    message = "OK";
                } else {
                    message = "NOTOK";
                }

            }

            objectOutputStream.writeObject(new Message(Message.Type.REGISTER, message));
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

    private void closeSocket() {

        try {
            clientSocket.close();

        } catch (IOException e) {
            System.out.println(Thread.currentThread().getName() + ": Olha, fudeu!");
            e.printStackTrace();

        }
    }

    private void buildBufferStreams() throws IOException {

        objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
        objectOutputStream.flush();
        objectInputStream = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        System.out.println("blabas " + objectInputStream.read());
    }

    public void write(String username, String msg) {
        try {
            objectOutputStream.writeBytes(username + " wrote: " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
