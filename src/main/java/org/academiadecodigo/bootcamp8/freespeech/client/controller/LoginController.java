package org.academiadecodigo.bootcamp8.freespeech.client.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.academiadecodigo.bootcamp8.freespeech.client.utils.Hash;
import org.academiadecodigo.bootcamp8.freespeech.client.service.RegistryService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.login.LoginService;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Navigation;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.SessionContainer;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.communication.MapKey;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.*;

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

//TODO refactor

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


    /**
     * Adds mouse event listeners to allow for stage to be dragged.
     */
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

    /**
     * Logs in or registers user according to current scene status.
     *
     * @param event
     */
    @FXML
    void onEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Button option = confirmPassword.isVisible() ? registerButton : loginButton;
            option.fire();
        }
    }

    /**
     * Attempts to log in user.
     */
    @FXML
    void onLogin(ActionEvent event) {

        final int MAX_NAME_CHARACTERS = 15;

        if (confirmPassword.isVisible()) {
            passwordConfirmation(false);
            passwordField.setText("");
            loginButton.setId("loginButton");
            registerButton.setId("registerButton");
            serverMessageLabel.setText("");
            return;
        }

        if (emptyFields(false)) {
            serverMessageLabel.setText(Values.INVALID_INPUT);
            return;
        }

        if (nameField.getText().length() > MAX_NAME_CHARACTERS) {
            serverMessageLabel.setText(Values.NAME_TOO_LONG);
            return;
        }

        sendData(MessageType.LOGIN);
        Sendable<String> serverResponse = loginService.readMessage();

        if (serverResponse.getContent().equals(Values.REPORTED)) {
            serverMessageLabel.setText(serverResponse.getContent());
            return;
        }

        if (serverResponse.getContent().equals(Values.LOGIN_OK)) {
            loginService.receiveSymKey();
            SessionContainer.getInstance().setUsername(nameField.getText());
            Navigation.getInstance().loadScreen(Values.USER_SCENE);
            return;
        }


        serverMessageLabel.setText(serverResponse.getContent());
    }

    /**
     * Attempts to register user.
     */
    @FXML
    void onRegister(ActionEvent event) {


        if (!confirmPassword.isVisible()) {
            passwordConfirmation(true);
            passwordField.setText("");
            confirmPassword.setText("");
            loginButton.setId("backButton");
            registerButton.setId("confirmButton");
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

        if (serverResponse.getContent().equals(Values.REGISTER_OK)) {
            serverMessageLabel.setText(Values.REGISTER_OK);
            return;
        }
        serverMessageLabel.setText(Values.REGISTER_FAIL);
    }

    /**
     * Quits the application.
     */
    @FXML
    void onClose(ActionEvent event) {
        loginService.exit();
        SessionContainer.close();
        Navigation.getInstance().close();
    }

    /**
     * Creates a message with the with given type and sends it to the server.
     *
     * @param messageType - the type.
     */
    private void sendData(MessageType messageType) {

        Map<MapKey, String> messageContent = new HashMap<>();
        messageContent.put(MapKey.USERNAME, nameField.getText());
        messageContent.put(MapKey.PASSWORD, Hash.getHash(passwordField.getText()));

        loginService.sendMessage(messageType, messageContent);
    }

    /**
     * Verifies if there are any empty fields in the current scene.
     *
     * @param register - scene identifier.
     * @return verification result.
     */
    private boolean emptyFields(boolean register) {

        boolean empty = nameField.getText().isEmpty() || passwordField.getText().isEmpty();

        if (register) {
            empty = empty || confirmPassword.getText().isEmpty();
        }

        return empty;
    }

    /**
     * Toggles need to confirm password.
     *
     * @param show
     */
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