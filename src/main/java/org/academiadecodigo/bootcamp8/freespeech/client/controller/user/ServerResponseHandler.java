package org.academiadecodigo.bootcamp8.freespeech.client.controller.user;

import org.academiadecodigo.bootcamp8.freespeech.client.utils.SessionContainer;
import org.academiadecodigo.bootcamp8.freespeech.client.dialog.DialogText;
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

//TODO remove commented code after testing

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
        ObjectInputStream input = sessionContainer.getInput();
        Key simKey = sessionContainer.getCrypto().getSymKey();
        SealedSendable sealed;
        Sendable sendable;

        int readingAttempts = 0;
        while (run && readingAttempts < Values.MAX_CONNECT_ATTEMPT) {

            sealed = Stream.readSendable(input);

            if (sealed == null) {
                readingAttempts++;
                continue;
            }

            sendable = sealed.getContent(simKey);
            process(sealed.getType(), sendable);
            readingAttempts = 0;

        }
        if (readingAttempts == Values.MAX_CONNECT_ATTEMPT) {
            clientController.infoPrompt(DialogText.SERVER_DOWN);
        }
    }

    /**
     * Processes the specified Message instance according to the specified type.
     *
     * @param type    - the type.
     * @param message - the instance.
     */
    private void process(MessageType type, Sendable message) {

        switch (type) {
            case TEXT:
                processPublicMessage(message);
                break;
            case PRIVATE_TEXT:
                processPrivateMessage(message);
                break;
            case DATA:
                saveFile(message);
                break;
            case USERS_ONLINE:
                clientController.processUsersList(message);
                break;
           /* case PASS_CHANGE:
                notifyUser(message);
                break;*/
            case PASS_CHANGE:
            case BIO_UPDATE:
            case REPORT:
                notifyUser(message);
                break;
            case BIO:
                clientController.showProfile(message, true);
                break;
            case PROFILE:
                clientController.showProfile(message, false);
                break;
            case EXIT:
                run = false;
                SessionContainer.close();
                break;
            case DELETE_ACCOUNT:
                deletionNotification(message);
                break;
         /*   case REPORT:
                notifyUser(message);
                break;*/
            default:
                throw new IllegalArgumentException("Unsupported message type.");
        }
    }

    /**
     * Processes the specified message and gives the user the chance to save its content.
     *
     * @param message - the message.
     */
    private void saveFile(Sendable<HashMap<MapKey, List<Byte>>> message) {

        HashMap<MapKey, List<Byte>> map = message.getContent();

        List<Byte> extensionList = map.get(MapKey.FILE_EXTENSION);
        List<Byte> byteList = map.get(MapKey.MESSAGE);

        String fileExtension = new String(Parser.listToByteArray(extensionList));
        String sender = new String(Parser.listToByteArray(map.get(MapKey.SOURCE)));

        clientController.saveFile(sender, byteList, fileExtension);
    }

    /**
     * Processes the specified message and prints its content to the corresponding room.
     *
     * @param message - the message.
     */
    private void processPrivateMessage(Sendable<HashMap<MapKey, String>> message) {

        HashMap<MapKey, String> map = message.getContent();

        String tabId = map.get(MapKey.TAB_ID);
        String destinyString = map.get(MapKey.DESTINATION);
        String text = map.get(MapKey.MESSAGE);

        clientController.printPrivateChat(tabId, destinyString, text);
    }

    /**
     * Processes the specified message, removing all the excess whitespaces and printing it to the lobby room.
     *
     * @param message - the message.
     */
    private void processPublicMessage(Sendable message) {

        String messageText = (String) message.getContent();
        messageText = wipeWhiteSpaces(messageText);

        clientController.printToLobby(messageText);
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

    /**
     * Displays the specified message content in a notification to the user.
     *
     * @param message - the message.
     */
    private void notifyUser(Sendable<String> message) {

        String info = message.getContent();
        clientController.infoPrompt(info);
    }

    /**
     * Notifies the user about the success of the account deletion attempt according to the specified message.
     *
     * @param message - the message.
     */
    private void deletionNotification(Sendable<String> message) {

        String info = message.getContent();

        if (info.equals(Values.ACC_DELETED)) {
            run = false;
            clientController.quitPrompt(info);
            return;
        }

        clientController.infoPrompt(info);
    }
}