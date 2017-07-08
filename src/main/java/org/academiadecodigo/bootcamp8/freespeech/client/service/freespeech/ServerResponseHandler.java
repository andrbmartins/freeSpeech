package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import javafx.scene.control.TextArea;
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

import java.io.ObjectInputStream;
import java.security.Key;
import java.util.HashMap;
import java.util.List;
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
        ObjectInputStream oin = sessionContainer.getInput();
        Key symKey = sessionContainer.getCrypto().getSymKey();
        SealedSendable sealedSendable;
        Sendable sendable;


        int connect = 0;
        while (run && connect < Values.MAX_CONNECT_ATTEMPT ) {

            sealedSendable = Stream.readSendable(oin);

            if (sealedSendable == null) {
                connect++;
                continue;

            }

            sendable = sealedSendable.getContent(symKey);
            process(sealedSendable.getType(), sendable);
            connect = 0;

        }

    }


    private void process(MessageType type, Sendable message) {



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
                break;
            case REPORT:
                notifyUser(message);
                break;
            default:
                throw new IllegalArgumentException("Invalid message type");
        }
    }

    private void saveReceivedFile(Sendable<HashMap<MapKey, List<Byte>>> message) {


        HashMap<MapKey, List<Byte>> map = message.getContent();
        List<Byte> extensionList = map.get(MapKey.FILE_EXTENSION);
        List<Byte> byteList = map.get(MapKey.MESSAGE);
        String fileExtension = new String(Parser.listToByteArray(extensionList));
        String sender = new String(Parser.listToByteArray(map.get(MapKey.SOURCE)));

        clientController.saveFile(sender, byteList, fileExtension);


    }

    private void printPrivateChat(Sendable<HashMap<MapKey, String>> message) {

        HashMap<MapKey, String> map = message.getContent();

        String tabId = map.get(MapKey.TAB_ID);
        String destinyString = map.get(MapKey.DESTINATION);
        String text = map.get(MapKey.MESSAGE);

        clientController.printPrivateChat(tabId, destinyString, text);
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

        //One or more characters, a colon and three spaces
        //Every whitespace
        //Every word character, digit, whitespace, punctuation and symbol
        //A single character, punctuation or symbol

        String regex = "(<.+>   )(\\s*)([\\w\\s\\p{P}\\p{S}\\p{L}]*)([\\w\\p{P}\\p{S}\\p{L}])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        String result = "";
        while (matcher.find()) {
            result = result.concat(matcher.group(1));
            result = result.concat(matcher.group(3));
            result = result.concat(matcher.group(4));
        }
        return result;
    }

    private void notifyUser(Sendable<String> msg) {
        String info = msg.getContent();

        clientController.userPromptExternal(DialogText.ACCOUNT_MANAGER, info);

    }

    private void accDeleteNotify(Sendable<String> message) {
        String info = message.getContent();
        if (info.equals(Values.ACC_DELETED)) {
            run = false;
            clientController.userPromptQuit(DialogText.ACCOUNT_MANAGER, info);
            return;
        }
        clientController.userPromptExternal(DialogText.ACCOUNT_MANAGER, info);


    }

}
