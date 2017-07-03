package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import javafx.scene.control.Alert;
import org.academiadecodigo.bootcamp8.freespeech.client.controller.ClientController;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.DialogText;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.SealedSendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

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

        switch (type) {
            case NOTIFICATION:
                clientService.sendListRequest();
                break;
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
                break;
            case PRIVATE_TEXT:
                //TODO - Empty switch case ???
                break;
            case PASS_CHANGE:
                notifyUser(message);
                break;
            case LOGOUT:
                run = false;
                break;
            case EXIT:
                run = false;
                Session.close();
                break;
            case BIO:
                System.out.println("Recebi a mensagem com a bio " + message.toString());
                clientController.ShowUserBio(message);
                break;
        }
    }

    private void printToRoom(Sendable message) {

        String roomText = clientController.getCurrentRoom().getText();
        String messageText = (String) message.getContent(String.class);

        messageText = wipeWhiteSpaces(messageText);
        clientController.getCurrentRoom().appendText((roomText.isEmpty() ? "" : "\n") + messageText);
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

        clientController.userPromptExternal(Alert.AlertType.INFORMATION, DialogText.PASS_MANAGER, info);
    }

}
