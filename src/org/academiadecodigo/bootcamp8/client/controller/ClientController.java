package org.academiadecodigo.bootcamp8.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class ClientController extends Controller {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void onTabClicked(ActionEvent event) {

    }

    @FXML
    void onSend(ActionEvent event) {

       String message =  textField.getText();
        try {
            super.getOutput().writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void onSendKey(KeyEvent event) {

    }
}
