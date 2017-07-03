package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeech.client.controller.ClientController;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.SealedSendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class ServerResponseHandler implements Runnable {

    private ClientService clientService;
    private ClientController clientController;

    public ServerResponseHandler(ClientService clientService, ClientController clientController) {
        this.clientService = clientService;
        this.clientController = clientController;
    }

    @Override
    public void run() {

        while (true) {
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
            case NOTIFICATION:
                clientService.sendListRequest();
                break;
            case TEXT:
                printToRoom(message);
                break;
            case DATA:
                //TODO
                break;
            case REQUEST_USERS_ONLINE:
                clientController.processUsersList(message);
                break;
            case PRIVATE_DATA:
                //TODO
                break;
            case PRIVATE_TEXT:
                //TODO
                printPrivateChat(message);
                break;
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
        ((TextArea)(clientController.getCurrentRoom().getContent())).appendText((((TextArea)(clientController.getCurrentRoom().getContent())).getText().isEmpty() ? "" : "\n") + messageText);
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

        //TODO allow specials characs

        Pattern pattern = Pattern.compile("(.+:)(\\s*)([\\w\\s\\p{P}\\p{S}çÇ]*)([\\w\\p{P}\\p{S}çÇ])");
        Matcher matcher = pattern.matcher(text);

        String result = "";
        while (matcher.find()) {
            result = result.concat(matcher.group(1) + " ");
            result = result.concat(matcher.group(3));
            result = result.concat(matcher.group(4));
        }
        return result;
    }
}
