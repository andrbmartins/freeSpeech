package org.academiadecodigo.bootcamp8.freespeach.client.service;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeach.shared.utils.Stream;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá, PedroMAlves
 */

public class LoginClientService implements ClientService {

    private Socket clientSocket;
    private boolean connectionServer;


    public void makeConnection(String server, int port) {
        try {

            InetAddress address = InetAddress.getByName(server);
            System.out.println(address);

            if (!connectionServer) {
                clientSocket = new Socket(server, port);
                Service.setClientSocket(clientSocket);
            } else {
                System.out.println("Client already connected");
            }

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Connection to server not successful");
            connectionServer = false;
            return;
        }

        System.out.println("Connection to server successful");
        connectionServer = true;

    }


    public boolean getConnectionServer() {
        return connectionServer;
    }

    @Override
    public void sendUserText(TextArea textField) {

        if (textField.getText().isEmpty()) {
            return;
        }

        Message<String> message = new Message<>(MessageType.DATA, textField.getText());
        writeObject(message);
        System.out.println("SENT: " + message);
        textField.clear();
        textField.requestFocus();
    }


    @Override
    public InputStream getInput() throws IOException {
        return clientSocket.getInputStream();
    }

    /**
     * @param message
     * @see ClientService#writeObject(Sendable)
     */
    @Override
    public void writeObject(Sendable message) {
        try {
            Stream.writeObject(clientSocket.getOutputStream(), message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message readObject() {
        Object serverMessage = null;

        try {
            serverMessage = Stream.readObject(clientSocket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return (Message) serverMessage;
    }

    @Override
    public void closeClientSocket() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void sendUserData(File file) {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

}
