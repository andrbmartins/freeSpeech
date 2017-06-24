package org.academiadecodigo.bootcamp8.client.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.client.InputHandler;
import org.academiadecodigo.bootcamp8.client.model.ClientService;
import org.academiadecodigo.bootcamp8.messages.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
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
        clientService.setupStreams();

        //TESTING SERVER -----------------------------

        HashMap<String, String> no = new HashMap<>();
        no.put("username", "bqdjhv");

        Message<HashMap> message = new Message(Message.Type.LOGIN, no);

        try {
            getOutput().writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //-----------------------------


        new Thread(new InputHandler(clientService.getInput(), this)).start();
        //TODO - invoke stuff
        //listen();
    }

    @Override
    public void listen() {
        while (true) {

            try {
                System.out.println(clientService.getInput().readObject());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public ObjectOutputStream getOutput() {
        return clientService.getOutput();
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
            getOutput().writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onSendKey(KeyEvent event) {

    }
}
