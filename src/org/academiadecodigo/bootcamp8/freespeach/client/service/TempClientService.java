package org.academiadecodigo.bootcamp8.freespeach.client.service;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeach.client.controller.ClientController;
import org.academiadecodigo.bootcamp8.freespeach.shared.Values;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeach.shared.utils.Stream;

import java.io.*;
import java.net.Socket;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class TempClientService implements ClientService {

    private Socket clientSocket;
    //private ObjectOutputStream output;
    //private ObjectInputStream input;

    public TempClientService() {
        try {
            clientSocket = new Socket(Values.HOST, Values.SERVER_PORT);
        } catch (IOException e) {
            //TODO load login with no connection error
            e.printStackTrace();
            System.out.println("NO CONNECTION");
            System.exit(1);
        }
        //setupStreams();
    }

    /**
     * Instantiates an ObjectOutputStream and an ObjectInputStream from and to the client socket.
     */
    /*
    public void setupStreams() {
        try {
            output = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            input = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("output stream: " + output);
        System.out.println("input stream: " + input);
    }
    */

    /**
     * @see ClientService#sendUserText(TextArea)
     * @param textArea
     */
    @Override
    public void sendUserText(TextArea textArea) {

        if (textArea.getText().isEmpty()) {
            return;
        }
        String text = ClientController.USERNAME + ": "+ textArea.getText();
        Message<String> message = new Message<>(MessageType.DATA, text);
        writeObject(message);
        textArea.clear();
        textArea.requestFocus();
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
    public InputStream getInput() throws IOException {
        return clientSocket.getInputStream();
    }

    /**
     * @see ClientService#writeObject(Sendable)
     * @param message
     */
    @Override
    public void writeObject(Sendable message) {
        try {
            //output.writeObject(message);
            Stream.writeObject(clientSocket.getOutputStream(), message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message readObject() {
        Object serverMessage = null;
        try {
            //serverMessage = input.readObject();
            serverMessage = Stream.readObject(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (Message) serverMessage;
    }
}