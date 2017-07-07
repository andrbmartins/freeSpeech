package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import org.academiadecodigo.bootcamp8.freespeech.client.controller.ClientController;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.SessionContainer;
import org.academiadecodigo.bootcamp8.freespeech.dialog.DialogText;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.communication.MapKey;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.SealedSendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Parser;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

        SessionContainer sessionContainer = SessionContainer.getInstance();
        SealedSendable sealedMessage;
        Sendable message;

        while (run) {
            sealedMessage = Stream.readSendable(sessionContainer.getInput());
            message = sealedMessage.getContent(sessionContainer.getCrypto().getSymKey());
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
            case USERS_ONLINE:
                clientController.processUsersList(message);
                break;
            case DATA:
                saveReceivedFile(message);
                break;
            case PRIVATE_TEXT:
                printPrivateChat(message);
                break;
            case PASS_CHANGE:
                notifyUser(message);
                break;
            case EXIT:
                run = false;
                SessionContainer.close();
                break;
            case BIO_UPDATE:
                notifyUser(message);
                break;
            case BIO:
                clientController.showOwnBio(message);
                break;
            case PROFILE:
                clientController.showUserBio(message);
                break;
            case DELETE_ACCOUNT:
                accDeleteNotify(message);
            case REPORT:
                notifyUser(message);
                break;
            default:
                throw new IllegalArgumentException("Invalid message type");
        }
    }

    private void saveReceivedFile(Sendable<HashMap<MapKey, List<Byte>>> message) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                HashMap<MapKey, List<Byte>> map = message.getContent();
                List<Byte> extensionList = map.get(MapKey.FILE_EXTENSION);
                List<Byte> byteList = map.get(MapKey.MESSAGE);
                String fileExtension = new String(Parser.listToByteArray(extensionList));
                String sender = new String(Parser.listToByteArray(map.get(MapKey.SOURCE)));

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("You have received a file from " + sender);
                fileChooser.setInitialFileName("My file." + fileExtension);
                File file = fileChooser.showSaveDialog(new Stage());

                try {

                    if (file != null && file.createNewFile()) {
                        Parser.byteListToFile(Parser.listToByteArray(byteList), file);
                    }

                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }

            }
        };

        Platform.runLater(runnable);

    }

    private void printPrivateChat(Sendable<HashMap<MapKey, String>> message) {

        HashMap<MapKey, String> map = message.getContent();

        String tabId = map.get(MapKey.TAB_ID);
        String destinyString = map.get(MapKey.DESTINATION);
        String text = map.get(MapKey.MESSAGE);
        TextArea textArea;
        Set<String> destinySet = Parser.stringToSet(destinyString);

        if ((textArea = clientController.getDestinyRoom(tabId)) != null) {
            clientController.updateUsersSet(tabId, destinySet);
            clientController.updateTooltipText(tabId, destinySet);

        } else {
            clientController.createReceivedTab(destinySet, tabId);
            textArea = clientController.getDestinyRoom(tabId);
        }
        textArea.appendText((textArea.getText().isEmpty() ? "" : "\n") + text);
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
