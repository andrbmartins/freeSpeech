package org.academiadecodigo.bootcamp8.freespeech.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import org.academiadecodigo.bootcamp8.freespeech.client.service.HashService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.RegisterService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.login.LoginService;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Navigation;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;

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
    private Label nameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPassword;

    @FXML
    private Button loginButton;

    @FXML
    private Label serverMessageLabel;

    @FXML
    private Button registerButton;

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
    private LoginService clientService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clientService = RegisterService.getInstance().get(LoginService.class);
    }


    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
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
            Session.getInstance().setUsername(nameField.getText());
            Navigation.getInstance().loadScreen(Values.USER_SCENE);

        } else {
            serverMessageLabel.setText((String) serverMsg.getContent());
        }
    }



    @FXML
    void onRegister(ActionEvent event) {

        if (fieldsAreEmpty()) {
            serverMessageLabel.setText(Values.EMPTY_FIELDS);
            return;
        }
        if (!passwordField.getText().equals(confirmPassword.getText())) {
            serverMessageLabel.setText(Values.CHECK_PASSWORD);
        }
        sendMsg(MessageType.REGISTER);

        if (clientService.readObject().getContent().equals(Values.REGISTER_OK)) {
            serverMessageLabel.setText(Values.REGISTER_OK);
        } else {
            serverMessageLabel.setText(Values.USER_TAKEN);
        }

    }

    private void sendMsg(MessageType messageType) {
        // TODO check if fields are ok
        Map<String, String> messageContent = new HashMap<>();

        messageContent.put(Values.NAME_KEY, nameField.getText());
        messageContent.put(Values.PASSWORD_KEY, HashService.getHash(passwordField.getText()));

        Message<Map> message = new Message<>(messageType, messageContent);

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
            StatusCircle.setFill(Paint.valueOf("red"));
            serverMessageLabel.setText("DISCONNECTED FROM SERVER");
            toggleView(false);
        }
        else
            serverMessageLabel.setText("ALREADY DISCONNECTED FROM SERVER");
    }

    @FXML
    void onConnectServer(ActionEvent event) {

        if(!clientService.getConnectionServer()) {
           if (!portTextField.getText().isEmpty() && portTextField.getText().matches("^\\d{0,9}$"));
                clientService.makeConnection(serverTextField.getText(), Integer.parseInt(portTextField.getText()));
        }
        else {
            serverMessageLabel.setText("ALREADY CONNECTED");
            return;
        }

        if(clientService.getConnectionServer()) {
            StatusCircle.setFill(Paint.valueOf("green"));
            serverMessageLabel.setText("CONNECTION TO SERVER SUCCESSFUL");
            toggleView(true);
        }
        else{
            serverMessageLabel.setVisible(true);
            serverMessageLabel.setText("CLIENT DISCONNECTED");
        }
    }

    private void toggleView(boolean value) {
        nameLabel.setVisible(value);
        nameField.setVisible(value);
        passwordLabel.setVisible(value);
        passwordField.setVisible(value);
        emailLabel.setVisible(value);
        confirmPassword.setVisible(value);
        loginButton.setVisible(value);
        registerButton.setVisible(value);
    }


    private boolean fieldsAreEmpty(){
        return nameField.getText().isEmpty() || passwordField.getText().isEmpty() || confirmPassword.getText().isEmpty();

    }

}