package org.academiadecodigo.bootcamp8.client.model;

import org.academiadecodigo.bootcamp8.messages.Message;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class ClientService {

    private Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private final java.lang.String HOST = "192.168.1.19";
    private final int SERVER_PORT = 4040;

    public ClientService() {
        try {
            clientSocket = new Socket(HOST, SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setupStreams() {

        try {
            output = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            //output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }



        HashMap<String, String> no = new HashMap<>();
        no.put("username", "bqdjhv");
        no.put("password", "nzxiucaiu");
;
        try {
            output.writeObject(new Message<>(Message.Type.LOGIN, no));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("HERE");

        System.out.println(output);


        try {
            input = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("o " + output);
        System.out.println("i " + input);
    }

    public void close() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    public ObjectInputStream getInput() {
        return input;
    }
}
