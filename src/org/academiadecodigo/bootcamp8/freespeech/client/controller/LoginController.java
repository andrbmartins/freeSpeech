package org.academiadecodigo.bootcamp8.freespeech.client.controller;

import javafx.application.Platform;
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
import java.security.Key;
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
    private Button loginButton;
    @FXML
    private Label serverMessageLabel;
    @FXML
    private Button registerButton;
    @FXML
    private Button exitButton;
    private Stage stage;
    private LoginService clientService;

    @FXML
    private GridPane loginPane;
    private double[] position;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        position = new double[2];
        clientService = RegistryService.getInstance().get(LoginService.class);

        loginButton.setDisable(true);
        registerButton.setDisable(true);
        serverMessageLabel.setText("Please wait for connection");

        //TODO view for connection + controller
        Platform.runLater( new Runnable() {
            @Override
            public void run() {
                clientService.makeConnection(Values.HOST, Values.SERVER_PORT);
                loginButton.setDisable(false);
                registerButton.setDisable(false);
                serverMessageLabel.setText("Connection established");

            }
        });
        connectionEstablished();

        setDraggable();


    }

    private void connectionEstablished() {
        //TODO go from connecting screen to login
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
    void freeSpeechSelected(ActionEvent event) {
        //TODO remove
    }

    @Override
    public void setStage(Stage stage) {

        this.stage = stage;
        this.stage.setMaxWidth(Values.LOGIN_WIDTH);
        this.stage.setMaxHeight(Values.LOGIN_HEIGHT);
    }

    @FXML
    void onLogin(ActionEvent event) {

        if (fieldsAreEmpty()) {
            serverMessageLabel.setText(Values.INVALID_INPUT);
            return;
        }

        sendMsg(MessageType.LOGIN);

        SealedSendable serverRsp = Stream.readSendable(Session.getInput());
        Sendable<String> serverMsg = (Sendable<String>) Session.getCrypto().decryptWithPrivate(serverRsp);

        if (serverMsg.getContent(String.class).equals(Values.LOGIN_OK)) {
            receiveSymKey();
            Session.getInstance().setUsername(nameField.getText());
            Navigation.getInstance().loadScreen(Values.USER_SCENE);
            return;

        }

        serverMessageLabel.setText(serverMsg.getContent(String.class));
    }

    private void receiveSymKey() {
        SealedSendable s = Stream.readSendable(Session.getInput());
        Sendable<Key> key = (Sendable<Key>) Session.getCrypto().decryptWithPrivate(s);
        Session.getCrypto().setSymKey(key.<Key>getContent(Key.class));
    }


    @FXML
    void onRegister(ActionEvent event) {
// TODO check double password
        if (fieldsAreEmpty()) {
            serverMessageLabel.setText(Values.INVALID_INPUT);
            return;
        }
        if (!passwordField.getText().equals(confirmPassword.getText())) {
            serverMessageLabel.setText(Values.UNMATCHED_PASSWORD);
        }
        sendMsg(MessageType.REGISTER);

        SealedSendable s = Stream.readSendable(Session.getInput());
        Sendable<String> s1 = (Sendable<String>) Session.getCrypto().decryptWithPrivate(s);

        if (s1.getContent(String.class).equals(Values.REGISTER_OK)) {
            serverMessageLabel.setText(Values.REGISTER_OK);

        } else {
            serverMessageLabel.setText(Values.REGISTER_FAIL);
        }

    }

    private void sendMsg(MessageType messageType) {
        Map<String, String> messageContent = new HashMap<>();

        messageContent.put(Values.NAME_KEY, nameField.getText());
        messageContent.put(Values.PASSWORD_KEY, HashService.getHash(passwordField.getText()));

        Message<Map> message = new Message<>(messageContent);

        SealedSendable sealed = Session.getCrypto().encrypt(messageType, message,
                Session.getCrypto().getForeignKey());

        Stream.write(Session.getOutput(), sealed);
    }

    @FXML
    void onExitButton(ActionEvent event) {
        Session.close();
        Navigation.getInstance().close();
    }

    private boolean fieldsAreEmpty() {
        return nameField.getText().isEmpty() || passwordField.getText().isEmpty() || confirmPassword.getText().isEmpty();
    }

}