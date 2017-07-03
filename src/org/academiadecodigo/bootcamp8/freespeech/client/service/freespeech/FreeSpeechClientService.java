package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.*;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

//TODO documentation

public class FreeSpeechClientService implements ClientService {

    private final int MAX_FILE_SIZE = 52428800; //50 MB

    @Override
    public void sendUserText(TextArea textArea) {

        if (textArea.getText().isEmpty()) {
            return;
        }
        String text = Session.getInstance().getUsername() + ": " + textArea.getText();

        Message<String> message = new Message<>(text);
        writeObject(MessageType.TEXT, message);

        textArea.clear();
    }

    @Override
    public void sendListRequest() {
        Message<Object> message = new Message<>("");
        writeObject(MessageType.REQUEST_USERS_ONLINE, message);
    }

    @Override
    public void sendUserData(File file) {

        if (file.length() > MAX_FILE_SIZE) {
            //TODO popup for user
            System.out.println("file too big");
            return;
        }

        byte[] buffer = fileToByteArray(file);
        List<Byte> byteList = byteArrayToList(buffer);

        Message<List> message = new Message<>(byteList);
        writeObject(MessageType.DATA, message);
    }


    //TODO file manager?
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

    private void writeObject(MessageType type, Sendable message) {

        SealedSendable sealedMessage = Session.getCrypto().encrypt(type, message, Session.getCrypto().getSymKey());
        Stream.write(Session.getOutput(), sealedMessage);
    }

    @Override
    public String getName() {
        return ClientService.class.getSimpleName();
    }
}