package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

//TODO documentation

public class FreeSpeechClientService implements ClientService {

    private Socket clientSocket;

    @Override
    public void sendUserText(TextArea textArea) {

        if (textArea.getText().isEmpty()) {
            return;
        }
        String text = Session.getInstance().getUsername() + ": " + textArea.getText();

        Message<String> message = new Message<>(MessageType.TEXT, text);
        writeObject(message);

        textArea.clear();
    }

    @Override
    public void sendListRequest() {
        Message<String> message = new Message<>(MessageType.REQUEST_USERS_ONLINE, "SEND NUDES");
        writeObject(message);
    }

    @Override
    public void sendUserData(File file) {

        byte[] buffer = fileToByteArray(file);
        List<Byte> byteList = byteArrayToList(buffer);

        Message<List> message = new Message<>(MessageType.DATA, byteList);
        writeObject(message);
    }

    /**
     * Converts a byte array into a byte list.
     *
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
     *
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

    //TODO - logout
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
            Stream.writeObject(clientSocket.getOutputStream(), message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return ClientService.class.getSimpleName();
    }

    @Override
    public void setSocket(Socket socket) {
        if (clientSocket == null) {
            clientSocket = socket;
        }
    }
}