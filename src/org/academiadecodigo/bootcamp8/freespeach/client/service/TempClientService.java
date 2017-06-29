package org.academiadecodigo.bootcamp8.freespeach.client.service;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeach.client.utils.Navigation;
import org.academiadecodigo.bootcamp8.freespeach.shared.Values;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeach.shared.utils.Stream;
import org.academiadecodigo.bootcamp8.freespeach.tests.Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class TempClientService implements ClientService {
    private static TempClientService tempClientService;
    private Socket clientSocket;
    private String username;

    private TempClientService(Socket clientSocket) {
        this.clientSocket = clientSocket;
        username = Service.getUsername();
    }



    /**
     * @param textArea
     * @see ClientService#sendUserText(TextArea)
     */
    @Override
    public void sendUserText(TextArea textArea) {

        if (textArea.getText().isEmpty()) {
            return;
        }
        String text = username + ": " + textArea.getText();
        Message<String> message = new Message<>(MessageType.TEXT, text);
        writeObject(message);
        textArea.clear();
        textArea.requestFocus();
    }

    @Override
    public void sendUserData(File file) {

        byte[] buffer = fileToByteArray(file);
        List<Byte> byteList = byteArrayToList(buffer);

        System.out.println(byteList);

        Message<List> message = new Message<>(MessageType.DATA, byteList);
        writeObject(message);
    }

    /**
     * Converts a byte array into a byte list.
     * @param buffer - the byte array.
     * @return the byte list.
     */
    private List<Byte> byteArrayToList(byte[] buffer) {

        List<Byte> byteList = new ArrayList<>();

        for (byte b : buffer) {
            byteList.add(b);
        }
        return byteList;
    }

    /**
     * Converts a file into a byte array.
     * @param file - the file.
     * @return the byte array.
     */
    private byte[] fileToByteArray(File file) {

        byte[] buffer = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            buffer = new byte[(int) file.length()];
            fileInputStream.read(buffer);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
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
     * @param message
     * @see ClientService#writeObject(Sendable)
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

        //TODO only reading strings
        Object serverMessage = null;
        try {
            //serverMessage = input.readObject();
            serverMessage = Stream.readObject(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (Message) serverMessage;
    }

    @Override
    public boolean getConnectionServer() {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    @Override
    public void makeConnection(String server, int port) {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    public static TempClientService getInstance(Socket clientSocket) {
        if (tempClientService == null) {
            synchronized (TempClientService.class) {
                if (tempClientService == null) {
                    tempClientService = new TempClientService(clientSocket);
                }
            }
        }
        return tempClientService;
    }




}