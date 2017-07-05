package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import org.academiadecodigo.bootcamp8.freespeech.client.controller.ClientController;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.DialogText;
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
    private ClientController clientController;

    public ServerResponseHandler(ClientController clientController) {
        run = true;
        this.clientController = clientController;
    }

    @Override
    public void run() {

        while (run) {
            SealedSendable sealedMessage = Stream.readSendable(Session.getInput());
            Sendable message = sealedMessage.getContent(Session.getCrypto().getSymKey());
            process(sealedMessage.getType(), message);
        }

    }

    private void process(MessageType type, Sendable message) {

        //TODO remove these souts...
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
                saveRecievedFile(message);
                break;
            case PRIVATE_TEXT:
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
                clientController.showOwnBio(message);
                break;
            case BIO:
                clientController.showUserBio(message);
                break;
            case DELETE_ACCOUNT:
                accDeleteNotify(message);
                break;
        }
    }

    private void saveRecievedFile(Sendable<HashMap<String, List<Byte>>> message) {


        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                HashMap<String, List<Byte>> map = message.getContent();
                List<Byte> extensionList = map.get(Values.FILE_EXTENSION);
                String fileExtension = new String(parseListToByteArray(extensionList));
                List<Byte> byteList = map.get(Values.MESSAGE);


                FileChooser fileChooser = new FileChooser();
                //fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("extension",fileExtension));
                fileChooser.setInitialFileName("untitled." + fileExtension);
                File file = fileChooser.showSaveDialog(new Stage());
                if (file == null) {
                    return;
                }

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

        for (int i = 0; i < bytes.length; i++) {
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

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printPrivateChat(Sendable<HashMap<String, String>> message) {

        HashMap<String, String> map = message.getContent();

        String tabId = map.get(Values.TAB_ID);
        String destinyString = map.get(Values.DESTINY);
        String text = map.get(Values.MESSAGE);
        TextArea textArea;
        Set<String> destinySet = parseStringToSet(destinyString);

        if ((textArea = clientController.getDestinyRoom(tabId)) != null) {
            clientController.updateUsersSet(tabId, destinySet);

        } else {
            clientController.createReceivedTab(destinySet, tabId);
            textArea = clientController.getDestinyRoom(tabId);
        }
        textArea.appendText((textArea.getText().isEmpty() ? "" : "\n") + text);
    }

    private Set<String> parseStringToSet(String destinyString) {

        HashSet<String> set = new HashSet<>();

        for (String s : destinyString.split(Values.SEPARATOR_CHARACTER)) {
            set.add(s);
        }

        return set;
    }

    private void printToRoom(Sendable message) {

        String messageText = (String) message.getContent();
        messageText = wipeWhiteSpaces(messageText);
        TextArea textArea = clientController.getDestinyRoom("Lobby");
        Boolean isEmpty = textArea.getText().isEmpty();

        textArea.appendText((isEmpty ? "" : "\n") + messageText);
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
        String regex = "(.+:)(\\s*)([\\w\\s\\p{P}\\p{S}\\p{L}]*)([\\w\\p{P}\\p{S}\\p{L}])";
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


    private void notifyUser(Sendable<String> msg) {
        String info = msg.getContent();

        clientController.userPromptExternal(Alert.AlertType.INFORMATION, DialogText.ACCOUNT_MANAGER, info);

    }

    private void accDeleteNotify(Sendable<String> message) {
        String info = message.getContent();
        if (info.equals(Values.ACC_DELETED)) {
            run = false;
            clientController.userPromptQuit(Alert.AlertType.INFORMATION, DialogText.ACCOUNT_MANAGER, info);
            return;
        }
        clientController.userPromptExternal(Alert.AlertType.INFORMATION, DialogText.ACCOUNT_MANAGER, info);


    }

}
