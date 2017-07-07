package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Hash;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.SessionContainer;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.communication.MapKey;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.*;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Parser;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.io.*;
import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 * <Code Cadet> PedroMAlves
 */

public class FreeSpeechClientService implements ClientService {

    @Override
    public void sendUserText(String textArea) {

        if (textArea.isEmpty()) {
            return;
        }

        String text = SessionContainer.getInstance().getUsername() + ": " + textArea;

        Message<String> message = new Message<>(text);
        writeObject(MessageType.TEXT, message);
    }

    @Override
    public void sendPrivateText(String textArea, String tabId, Set<String> destinySet) {

        if (textArea.isEmpty()) {
            return;
        }

        String text = SessionContainer.getInstance().getUsername() + ": " + textArea;

        HashMap<MapKey, String> map = new HashMap<>();
        map.put(MapKey.TAB_ID, tabId);
        map.put(MapKey.DESTINATION, Parser.setToString(destinySet));
        map.put(MapKey.MESSAGE, text);

        System.out.println(map.toString());

        Message<HashMap<MapKey, String>> message = new Message<>(map);
        writeObject(MessageType.PRIVATE_TEXT, message);
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
        HashMap<MapKey, List<Byte>> map = new HashMap<>();

        List<Byte> destinyList = Parser.byteArrayToList(destiny.getBytes());
        List<Byte> originList = Parser.byteArrayToList(origin.getBytes());
        List<Byte> extensionList = Parser.byteArrayToList(fileExtension.getBytes());

        map.put(MapKey.DESTINATION, destinyList);
        map.put(MapKey.SOURCE, originList);
        map.put(MapKey.FILE_EXTENSION, extensionList);
        map.put(MapKey.MESSAGE, byteList);

        Message<HashMap<MapKey, List<Byte>>> message = new Message<>(map);
        writeObject(MessageType.DATA, message);
    }

    @Override
    public void sendExit() {
        Message<String> message = new Message<>(" ");
        writeObject(MessageType.EXIT, message);
    }


    public void deleteAccount(String password) {

        Message<String> message = new Message<>(Hash.getHash(password));
        writeObject(MessageType.DELETE_ACCOUNT, message);
    }

    @Override
    public void sendReport(String userToReport) {
        Message<String> message = new Message<>(userToReport);
        writeObject(MessageType.REPORT, message);
    }

    @Override
    public void changePassword(String[] passSet) {
        Map<MapKey, String> messageContent = new HashMap<>();

        messageContent.put(MapKey.PASSWORD, Hash.getHash(passSet[0]));
        messageContent.put(MapKey.NEW_PASSWORD, Hash.getHash(passSet[1]));

        Message<Map> message = new Message<>(messageContent);

        writeObject(MessageType.PASS_CHANGE, message);

    }

    private void writeObject(MessageType type, Sendable message) {

        SessionContainer sessionContainer = SessionContainer.getInstance();

        SealedSendable sealedMessage = sessionContainer.getCrypto().encrypt(type, message);
        Stream.write(sessionContainer.getOutput(), sealedMessage);
    }

    @Override
    public String getName() {
        return ClientService.class.getSimpleName();
    }

}