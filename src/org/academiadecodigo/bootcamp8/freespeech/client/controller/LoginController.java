package org.academiadecodigo.bootcamp8.freespeech.client.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.academiadecodigo.bootcamp8.freespeech.client.service.HashService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.RegistryService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.login.LoginService;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Navigation;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.*;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> JPM Ramos
 * <Code Cadet> PedroMAlves
 */

public class LoginController implements Controller {

    @FXML
    private TextField nameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private Label serverMessageLabel;
    @FXML
    private GridPane loginPane;
    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    private Stage stage;
    private LoginService loginService;
    private double[] position;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        position = new double[2];
        loginService = RegistryService.getInstance().get(LoginService.class);
        setDraggable();
    }

    private void setDraggable() {

        loginPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                position[0] = event.getSceneX();
                position[1] = event.getSceneY();
            }
        });

        loginPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - position[0]);
                stage.setY(event.getScreenY() - position[1]);
            }
        });
    }

    @FXML
    void onLogin(ActionEvent event) {

        if (confirmPassword.isVisible()) {
            passwordConfirmation(false);
            passwordField.setText("");
            loginButton.setText("Login");
            registerButton.setText("Register");
            serverMessageLabel.setText("");
            return;
        }

        passwordConfirmation(false);

        if (emptyFields(false)) {
            serverMessageLabel.setText(Values.INVALID_INPUT);
            return;
        }

        sendData(MessageType.LOGIN);
        Sendable<String> serverResponse = loginService.readMessage();

        if (serverResponse.getContent(String.class).equals(Values.LOGIN_OK)) {
            loginService.receiveSymKey();
            Session.getInstance().setUsername(nameField.getText());
            //TODO method to reset fields for when logout is requested
            //resetFields();
            Navigation.getInstance().loadScreen(Values.USER_SCENE);
            return;
        }

        serverMessageLabel.setText(serverResponse.getContent(String.class));
    }

    @FXML
    void onRegister(ActionEvent event) {

        if (!confirmPassword.isVisible()) {
            passwordConfirmation(true);
            passwordField.setText("");
            confirmPassword.setText("");
            loginButton.setText("Back");
            registerButton.setText("Confirm");
            serverMessageLabel.setText("");
            return;
        }

        if (emptyFields(true)) {
            serverMessageLabel.setText(Values.INVALID_INPUT);
            return;
        }

        if (!passwordField.getText().equals(confirmPassword.getText())) {
            serverMessageLabel.setText(Values.UNMATCHED_PASSWORD);
            return;
        }

        sendData(MessageType.REGISTER);
        Sendable<String> serverResponse = loginService.readMessage();

        if (serverResponse.getContent(String.class).equals(Values.REGISTER_OK)) {
            serverMessageLabel.setText(Values.REGISTER_OK);
            return;
        }
        serverMessageLabel.setText(Values.REGISTER_FAIL);
    }

    @FXML
    void onClose(ActionEvent event) {
        loginService.exit();
        Session.close();
        Navigation.getInstance().close();
    }

    private void sendData(MessageType messageType) {

        Map<String, String> messageContent = new HashMap<>();
        messageContent.put(Values.NAME_KEY, nameField.getText());
        messageContent.put(Values.PASSWORD_KEY, HashService.getHash(passwordField.getText()));

        loginService.sendMessage(messageType, messageContent);
    }

    private boolean emptyFields(boolean register) {

        boolean empty = nameField.getText().isEmpty() || passwordField.getText().isEmpty();

        if(register) {
            empty = empty || confirmPassword.getText().isEmpty();
        }

        return empty;
    }

    private void passwordConfirmation(boolean show) {

        confirmPassword.setVisible(show);
    }

    @Override
    public void setStage(Stage stage) {

        this.stage = stage;

        this.stage.setMinHeight(Values.LOGIN_HEIGHT);
        this.stage.setMaxHeight(Values.LOGIN_HEIGHT);

        this.stage.setMinWidth(Values.LOGIN_WIDTH);
        this.stage.setMaxWidth(Values.LOGIN_WIDTH);
    }
}