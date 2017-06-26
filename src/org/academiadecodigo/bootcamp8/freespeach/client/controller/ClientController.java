package org.academiadecodigo.bootcamp8.freespeach.client.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.freespeach.client.InputHandler;
import org.academiadecodigo.bootcamp8.freespeach.client.service.ClientService;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Message;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void init() {

        System.out.println(clientService);
        //clientService.setupStreams();

        //TESTING SERVER -----------------------------

        HashMap<String, String> no = new HashMap<>();
        no.put("username", "bqdjhv");

        Message<HashMap> message = new Message(Message.Type.LOGIN, no);


        clientService.writeObject(message);


        //-----------------------------


        new Thread(new InputHandler(clientService.getInput(), this)).start();
        //TODO - invoke stuff
        //listen();
    }

    public void listen() {
        Message message;

        while ((message = clientService.readObject()) != null) {
            System.out.println(message);
        }
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

        HashMap<String, String> no = new HashMap<>();
        no.put("username", "bqdjhv");

        Message<HashMap> message = new Message(Message.Type.LOGIN, no);

        try {
            clientService.getOutput().writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onSendKey(KeyEvent event) {

    }
}
