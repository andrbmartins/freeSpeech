package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.academiadecodigo.bootcamp8.freespeech.client.controller.ClientController;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.DialogText;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Navigation;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.SealedSendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class ServerResponseHandler implements Runnable {
    private boolean run;
    private ClientService clientService;
    private ClientController clientController;

    public ServerResponseHandler(ClientService clientService, ClientController clientController) {
        run = true;
        this.clientService = clientService;
        this.clientController = clientController;
    }

    @Override
    public void run() {

        while (run) {
            SealedSendable sealedMessage = Stream.readSendable(Session.getInput());
            Sendable message = Session.getCrypto().decryptSendable(sealedMessage, Session.getCrypto().getSymKey());
            process(sealedMessage.getType(), message);
        }

    }

    private void process(MessageType type, Sendable message) {

        System.out.println(type.toString());
        System.out.println(message.toString());
        System.out.println();

        switch (type) {
            case TEXT:
                printToRoom(message);
                break;
            case DATA:
                //TODO - Empty switch case ???
                break;
            case USERS_ONLINE:
                clientController.processUsersList(message);
                break;
            case PRIVATE_DATA:
                //TODO - Empty switch case ???
                saveRecievedFile(message);
                break;
            case PRIVATE_TEXT:
                //TODO - Empty switch case ???
                printPrivateChat(message);
                break;
            case PASS_CHANGE:
                notifyUser(message);
                break;
            case EXIT:
                run = false;
                Session.close();
                break;
            case BIO_UPDATE:
                notifyUser(message);
                break;
            case OWN_BIO:
                clientController.setOwnBio(message);
                break;
            case BIO:
                clientController.showUserBio(message);
                break;
            case DELETE_ACCOUNT:
                accDeleteNotify(message);
                break;
        }
    }

    private void saveRecievedFile(Sendable message) {


        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                HashMap<String, List<Byte>> map;
                map = (HashMap<String, List<Byte>>) message.<HashMap<String, List<Byte>>>getContent(HashMap.class);
                List<Byte> extensionList = map.get(Values.FILE_EXTENSION);
                String fileExtension = new String(parseListToByteArray(extensionList));
                List<Byte> byteList = map.get(Values.MESSAGE);


                FileChooser fileChooser = new FileChooser();
                //fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("extension",fileExtension));
                fileChooser.setInitialFileName("untitled." + fileExtension);
                File file = fileChooser.showSaveDialog(new Stage());


                try {
                    file.createNewFile();
                    byteListToFile(parseListToByteArray(byteList), file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        Platform.runLater(runnable);

    }

    private byte[] parseListToByteArray(List<Byte> byteList) {
        byte[] bytes = new byte[byteList.size()];

        for(int i = 0; i < bytes.length; i++){
            bytes[i] = byteList.get(i);
        }

        return bytes;
    }

    private void byteListToFile(byte[] byteArray, File file) {

        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(byteArray);
            stream.flush();
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printPrivateChat(Sendable message) {

        HashMap<String,String> map = (HashMap<String,String>)message.<HashMap<String,String>>getContent(HashMap.class);

        String tabId = map.get(Values.TAB_ID);
        String destinyString = map.get(Values.DESTINY);
        String text = map.get(Values.MESSAGE);
        TextArea textArea;
        Set<String> destinySet = parseStringToSet(destinyString);

        if((textArea = clientController.getDestinyRoom(tabId)) != null){
            clientController.updateUsersSet(tabId,destinySet);

        }else{
            clientController.createReceivedTab(destinySet,tabId);
            textArea = clientController.getDestinyRoom(tabId);
        }
            textArea.appendText((textArea.getText().isEmpty() ? "" :"\n") + text);
    }

    private Set<String> parseStringToSet(String destinyString) {

        HashSet<String> set = new HashSet<>();

        for(String s : destinyString.split(Values.SEPARATOR_CHARACTER)){
            set.add(s);
        }

        return set;
    }

    private void printToRoom(Sendable message) {

        String roomText = clientController.getCurrentRoom().getText();
        String messageText = (String) message.getContent(String.class);

        messageText = wipeWhiteSpaces(messageText);
        //clientController.getCurrentRoom().appendText((roomText.isEmpty() ? "" : "\n") + messageText);

        ((TextArea)(clientController.getSelectedTab().getContent())).appendText((((TextArea)(clientController.getSelectedTab().getContent())).getText().isEmpty() ? "" : "\n") + messageText);
    }

    /**
     * Removes all whitespaces before and after the specified string.
     *
     * @param text - the specified string.
     * @return the resulting text.
     */
    private String wipeWhiteSpaces(String text) {

        //One or more characters and a colon
        //Every whitespace
        //Every word character, digit, whitespace, punctuation and symbol
        //A single character, punctuation or symbol

        //TODO allow specials char
        String regex = "(.+:)(\\s*)([\\w\\s\\p{P}\\p{S}çÇ]*)([\\w\\p{P}\\p{S}çÇ])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        String result = "";
        while (matcher.find()) {
            result = result.concat(matcher.group(1) + " ");
            result = result.concat(matcher.group(3));
            result = result.concat(matcher.group(4));
        }
        return result;
    }


    private void notifyUser(Sendable msg) {
        String info = (String) msg.getContent(String.class);

        clientController.userPromptExternal(Alert.AlertType.INFORMATION, DialogText.ACCOUNT_MANAGER, info);

    }

    private void accDeleteNotify(Sendable message) {
        String info = (String) message.getContent(String.class);
        if (info.equals(Values.ACC_DELETED)) {
            run = false;
            clientController.userPromptQuit(Alert.AlertType.INFORMATION, DialogText.ACCOUNT_MANAGER, info);
            return;
        }
        clientController.userPromptExternal(Alert.AlertType.INFORMATION, DialogText.ACCOUNT_MANAGER, info);


    }

}
