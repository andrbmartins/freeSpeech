package org.academiadecodigo.bootcamp8.freespeach.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;


import org.academiadecodigo.bootcamp8.freespeach.client.service.ClientService;
import org.academiadecodigo.bootcamp8.freespeach.client.service.Services;
import org.academiadecodigo.bootcamp8.freespeach.client.utils.Navigation;
import org.academiadecodigo.bootcamp8.freespeach.shared.Values;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Sendable;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá, PedroMAlves
 */

public class LoginController implements Controller {

    @FXML
    private GridPane root;

    @FXML
    private Label nameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private TextField nameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField emailField;

    @FXML
    private Button loginButton;

    @FXML
    private Label serverMessageLabel;

    @FXML
    private Button resgisterButton;

    @FXML
    private Label serverLabel;

    @FXML
    private TextField serverTextField;

    @FXML
    private Label portLabel;

    @FXML
    private TextField portTextField;

    @FXML
    private Button connectButton;

    @FXML
    private Button disconnectButton;

    @FXML
    private Circle StatusCircle;

    @FXML
    private Button exitButton;
    private Stage stage;
    private ClientService clientService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serverMessageLabel.setVisible(false);
        clientService = Services.getLoginService();
    }


    @Override
    public void init() {
        hideLoginRegister();
    }


    @FXML
    void onLogin(ActionEvent event) {

        if (fieldsAreEmpty()) {
            serverMessageLabel.setVisible(true);
            serverMessageLabel.setText(Values.EMPTY_FIELDS);
            return;
        }

        sendMsg(MessageType.LOGIN);
        Sendable serverMsg = clientService.readObject();
        if (serverMsg.getContent().equals(Values.LOGIN_OK)) {
            Navigation.getInstance().loadScreen(Values.USER_SCENE);

        } else {
            serverMessageLabel.setVisible(true);
            serverMessageLabel.setText((String) serverMsg.getContent());
        }
    }



    @FXML
    void onRegister(ActionEvent event) {

        if (fieldsAreEmpty()) {
            serverMessageLabel.setVisible(true);
            serverMessageLabel.setText(Values.EMPTY_FIELDS);
            return;
        }

        sendMsg(MessageType.REGISTER);

        if (clientService.readObject().getContent().equals(Values.REGISTER_OK)) {
            serverMessageLabel.setVisible(true);
            serverMessageLabel.setText(Values.REGISTER_OK);
        } else {
            serverMessageLabel.setVisible(true);
            serverMessageLabel.setText(Values.USER_TAKEN);
        }
    }


    private void sendMsg(MessageType messageType) {
        // TODO check if fields are ok
        Map<String, String> messageContent = new HashMap<>();

        messageContent.put(Values.NAME_KEY, nameField.getText());
        messageContent.put(Values.PASSWORD_KEY, passwordField.getText());

        Message<Map> message = new Message(messageType, messageContent);

        clientService.writeObject(message);
    }

    @FXML
    void onExitButton(ActionEvent event) {
        if(clientService.getConnectionServer())
            clientService.closeClientSocket();
        Navigation.getInstance().close();
    }

    @FXML
    void OnDisconnetServer(ActionEvent event) {
        if(clientService.getConnectionServer()) {
            clientService.closeClientSocket();
        }
        if(!clientService.getConnectionServer()) {
            StatusCircle.setFill(Paint.valueOf("red"));
            serverMessageLabel.setText("DISCONNECTED FROM SERVER");
            hideLoginRegister();
        }
    }

    @FXML
    void onConnectServer(ActionEvent event) {

        if(!clientService.getConnectionServer()) {
           if (!portTextField.getText().isEmpty() && portTextField.getText().matches("^\\d{1,10}$"));
                clientService.makeConnection(serverTextField.getText(), Integer.parseInt(portTextField.getText()));
        }
        else {
            serverMessageLabel.setVisible(true);
            serverMessageLabel.setText("ALREADY CONNECTED");
            return;
        }

        if(clientService.getConnectionServer()) {
            StatusCircle.setFill(Paint.valueOf("green"));
            serverMessageLabel.setVisible(true);
            serverMessageLabel.setText("CONNECTION TO SERVER SUCCESSFUL");
            showLoginRegister();
        }
        else{
            serverMessageLabel.setVisible(true);
            serverMessageLabel.setText("CLIENT DISCONNECTED");
        }

    }

    private void hideLoginRegister(){
        nameLabel.setVisible(false);
        nameField.setVisible(false);
        passwordLabel.setVisible(false);
        passwordField.setVisible(false);
        emailLabel.setVisible(false);
        emailField.setVisible(false);
        loginButton.setVisible(false);
        resgisterButton.setVisible(false);
    }

    private void showLoginRegister(){
        nameLabel.setVisible(true);
        nameField.setVisible(true);
        passwordLabel.setVisible(true);
        passwordField.setVisible(true);
        emailLabel.setVisible(true);
        emailField.setVisible(true);
        loginButton.setVisible(true);
        resgisterButton.setVisible(true);
    }

    private boolean fieldsAreEmpty(){
        return nameField.getText().isEmpty() || passwordField.getText().isEmpty();

    }

}