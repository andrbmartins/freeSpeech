package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeech.client.service.HashService;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.*;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 * <Code Cadet> PedroMAlves
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

    // Sends a request bio to server
    @Override
    public void sendBioRequest(MessageType type, String username) {
        Message<String> message = new Message<>(username);
        writeObject(type, message);
        System.out.println("Mensagem enviada de pedido de bio");
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


    @Override
    public void sendExit() {
        Message<String> message = new Message<>(" ");
        writeObject(MessageType.EXIT, message);
    }


    public void deleteAccount (String password) {

        Message<String> message = new Message<>(HashService.getHash(password));
        writeObject(MessageType.DELETE_ACCOUNT, message);
    }

    @Override
    public void changePassword(String[] passSet) {
        Map<String, String> messageContent = new HashMap<>();

        messageContent.put(Values.PASSWORD_KEY, HashService.getHash(passSet[0]));
        messageContent.put(Values.NEW_PASSWORD, HashService.getHash(passSet[1]));

        Message<Map> message = new Message<>(messageContent);

        writeObject(MessageType.PASS_CHANGE, message);

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