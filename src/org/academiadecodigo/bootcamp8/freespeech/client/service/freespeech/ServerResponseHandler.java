package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeech.client.controller.ClientController;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

            Sendable message = (Sendable) Stream.readObject(Session.getInstance().getInputStream());
            System.out.println("MESSAGE RECEIVED: "+ message);
            process(message);

        }
    }

    private void process(Sendable message) {

        switch (message.getType()) {
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

        String user = (String) ((HashMap<String,String>)message.getContent()).get(Values.DESTINY_USER);
        String text = (String) ((HashMap<String,String>)message.getContent()).get(Values.MESSAGE);

        text = wipeWhiteSpaces(text);

        clientController.addMessageToTab(user,text);

    }

    private void printToRoom(Sendable message) {

        String text = (String) message.getContent();
        text = wipeWhiteSpaces(text);
        clientController.getCurrentRoom().appendText((clientController.getCurrentRoom().getText().isEmpty() ? "" : "\n") + text);
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

        Pattern pattern = Pattern.compile("(.+:)(\\s*)([\\w\\s\\p{P}\\p{S}çÇ]*)([\\w\\p{P}\\p{S}çÇ])");
        Matcher matcher = pattern.matcher(text);

        String result = "";
        while (matcher.find()) {
            result = result.concat(matcher.group(1) + " "); //username and colon
            result = result.concat(matcher.group(3));       //string content
            result = result.concat(matcher.group(4));       //last valid character
        }
        return result;
    }
}
