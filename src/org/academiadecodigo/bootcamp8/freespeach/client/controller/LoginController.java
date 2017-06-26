package org.academiadecodigo.bootcamp8.freespeach.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.freespeach.client.service.ClientService;
import org.academiadecodigo.bootcamp8.freespeach.client.service.TempClientService;
import org.academiadecodigo.bootcamp8.freespeach.client.utils.Navigation;
import org.academiadecodigo.bootcamp8.freespeach.shared.Values;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.MessageType;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class LoginController implements Controller {

    @FXML
    private GridPane root;
    @FXML
    private Button exitButton;
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

    private Stage stage;
    private ClientService clientService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serverMessageLabel.setVisible(false);
    }

    @Override
    public void init() {
    }

    @FXML
    void onLogin(ActionEvent event) {
        readFields(MessageType.LOGIN);

        //TODO server response structure
        if (clientService.readObject() == new Message(MessageType.LOGIN, new String("l ok"))) {
            Navigation.getInstance().loadScreen(Values.USER_SCENE);
        }
    }

    @FXML
    void onRegister(ActionEvent event) {
        readFields(MessageType.REGISTER);
        ///TODO
        if (clientService.readObject() == new Message(MessageType.LOGIN, new String("r ok"))) {
            serverMessageLabel.setVisible(true);
            serverMessageLabel.setText("REGISTER OK");
        }
    }
    private void readFields(MessageType messageType) {
        Map<String, String> messageContent = new HashMap<>();
        messageContent.put(Values.NAME_KEY, nameField.getText());
        messageContent.put(Values.PASSWORD_KEY, passwordField.getText());

        Message message = new Message<>(messageType, messageContent);
        clientService.writeObject(message);
    }

    @FXML
    void onExitButton(ActionEvent event) {
        clientService.closeClientSocket();
        Navigation.getInstance().close();
    }

    @Override
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }
}