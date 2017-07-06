package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeech.client.service.HashService;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.*;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Parser;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
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

//TODO documentation - file manager singleton?

public class FreeSpeechClientService implements ClientService {

    @Override
    public void sendUserText(TextArea textArea) {

        if (textArea.getText().isEmpty()) {
            return;
        }

        String text = Session.getUsername() + ": " + textArea.getText();

        Message<String> message = new Message<>(text);
        writeObject(MessageType.TEXT, message);

        textArea.clear();
    }

    // Sends a request bio to server
    @Override
    public void sendPrivateText(TextArea textArea, String tabId, Set<String> destinySet) {

        if (textArea.getText().isEmpty()) {
            return;
        }

        String text = Session.getUsername() + ": " + textArea.getText();

        HashMap<String,String> map = new HashMap<>();
        map.put(Values.TAB_ID, tabId);
        map.put(Values.DESTINY,parseSetToString(destinySet));
        map.put(Values.MESSAGE,text);

        System.out.println(map.toString());

        Message<HashMap<String,String>> message = new Message<>(map);
        writeObject(MessageType.PRIVATE_TEXT, message);

        textArea.clear();
    }

    private String parseSetToString(Set<String> destinySet) {

        StringBuilder stringBuilder = new StringBuilder();

        for (String s : destinySet){
            stringBuilder.append(s);
            stringBuilder.append(Values.SEPARATOR_CHARACTER);
        }

        return stringBuilder.toString();
    }

    @Override
    public void sendBioRequest(MessageType type, String username) {
        Message<String> message = new Message<>(username);
        writeObject(type, message);

    }

    @Override
    public void updateBio(List<String> updatedBio) {
        Message<List> message = new Message<>(updatedBio);
        writeObject(MessageType.BIO_UPDATE, message);

    }

    @Override
    public void sendUserData(File file, String destiny, String origin) {

        String fileExtension = file.getName();
        fileExtension = fileExtension.substring(fileExtension.lastIndexOf(".") + 1);

        System.out.println("file extension: " + fileExtension);

        byte[] buffer = Parser.fileToByteArray(file);
        List<Byte> byteList = Parser.byteArrayToList(buffer);
        HashMap<String, List<Byte>> map = new HashMap<>();

        List<Byte> destinyList = Parser.byteArrayToList(destiny.getBytes());
        List<Byte> originList = Parser.byteArrayToList(origin.getBytes());
        List<Byte> extensionList = Parser.byteArrayToList(fileExtension.getBytes());

        map.put(Values.DESTINY, destinyList);
        map.put(Values.ORIGIN, originList);
        map.put(Values.FILE_EXTENSION, extensionList);
        map.put(Values.MESSAGE, byteList);

        Message<HashMap<String, List<Byte>>> message = new Message<>(map);
        writeObject(MessageType.PRIVATE_DATA, message);
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
    public void sendReport(String userToReport) {
        Message<String> message = new Message<>(userToReport);
        writeObject(MessageType.REPORT, message);
    }

    @Override
    public void changePassword(String[] passSet) {
        Map<String, String> messageContent = new HashMap<>();

        messageContent.put(Values.PASSWORD_KEY, HashService.getHash(passSet[0]));
        messageContent.put(Values.NEW_PASSWORD, HashService.getHash(passSet[1]));

        Message<Map> message = new Message<>(messageContent);

        writeObject(MessageType.PASS_CHANGE, message);

    }

    //TODO - logout

    private void writeObject(MessageType type, Sendable message) {

        SealedSendable sealedMessage = Session.getCrypto().encrypt(type, message);
        Stream.write(Session.getOutput(), sealedMessage);
    }

    @Override
    public String getName() {
        return ClientService.class.getSimpleName();
    }

}