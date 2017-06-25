package org.academiadecodigo.bootcamp8.client.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.client.InputHandler;
import org.academiadecodigo.bootcamp8.client.service.ClientService;
import org.academiadecodigo.bootcamp8.message.Message;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class ClientController implements Controller {

    @FXML
    private Tab lobbyTab;
    @FXML
    private TextArea lobbyTextArea;
    @FXML
    private Tab privateTab;
    @FXML
    private TextArea privateTextArea;
    @FXML
    private ListView<?> onlineUsersList;
    @FXML
    private TextField textField;
    @FXML
    private Button send;

    private Stage stage;
    private ClientService clientService;

    //One input handler for each tab
    private ExecutorService inputHandlerPool = Executors.newCachedThreadPool();
    private TextArea currentRoom;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void init() {

        currentRoom = lobbyTextArea;
        System.out.println(clientService);

        //TESTING SERVER -----------------------------
/*
        HashMap<String, String> no = new HashMap<>();
        no.put("username", "bqdjhv");
        Message<HashMap> message = new Message(Message.Type.LOGIN, no);
        System.out.println("SENDING HARCODED");
        clientService.writeObject(message);
        System.out.println("SENT HARDCODED");
*/
        //-----------------------------

        InputHandler inputHandler = new InputHandler(clientService.getInput(), this);
        inputHandlerPool.submit(inputHandler);
    }


    @Override
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @FXML
    void onTabClicked(Event event) {

    }

    @FXML
    void onSend(ActionEvent event) {
        clientService.sendUserText(textField);
    }

    @FXML
    void onSendKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            clientService.sendUserText(textField);
        }
    }


}
