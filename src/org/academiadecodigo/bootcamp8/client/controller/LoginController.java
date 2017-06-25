package org.academiadecodigo.bootcamp8.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.client.service.ClientService;
import org.academiadecodigo.bootcamp8.client.utils.Navigation;
import org.academiadecodigo.bootcamp8.client.utils.Values;
import org.academiadecodigo.bootcamp8.message.Message;

import java.net.Socket;
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
    }

    @Override
    public void init() {
        hideLoginRegister();
    }


    @FXML
    void onLogin(ActionEvent event) {

        if (checkTextField()) {    //Check if fields are not empty
            readFields(Message.Type.LOGIN);
            if (clientService.readObject() == new Message(Message.Type.LOGIN, new String("l ok"))) {
                Navigation.getInstance().loadScreen(Values.USER_SCENE);       // Opens the chat room
            }
            else {
                serverMessageLabel.setVisible(true);
                serverMessageLabel.setText("LOGIN FAILED");
            }
        }
        else{
            serverMessageLabel.setVisible(true);
            serverMessageLabel.setText("INVALID FIELD LOGIN/PASSWORD ");
        }
    }

    @FXML
    void onRegister(ActionEvent event) {

        if (checkTextField()) {
            readFields(Message.Type.REGISTER);
            if (clientService.readObject() == new Message(Message.Type.LOGIN, new String("r ok"))) {
                serverMessageLabel.setVisible(true);
                serverMessageLabel.setText("REGISTER OK");
            } else {
                serverMessageLabel.setVisible(true);
                serverMessageLabel.setText("REGISTER FAILED");
            }
        }
        else{
            serverMessageLabel.setVisible(true);
            serverMessageLabel.setText("INVALID FIELD LOGIN/PASSWORD ");
        }
    }



    private void readFields(Message.Type messageType) {
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
            clientService.close();
        Navigation.getInstance().close();
    }

    @FXML
    void OnDisconnetServer(ActionEvent event) {             // Disconnect from server
        if(clientService.getConnectionServer()) {           // If is connected
            clientService.close();
        }
        if(!clientService.getConnectionServer()) {          // Checks if disconnect went ok
            StatusCircle.setFill(Paint.valueOf("red"));
            serverMessageLabel.setText("DISCONNECTED FROM SERVER");
            hideLoginRegister();                            // If is disconnected from server hides fields
        }
    }

    @FXML
    void onConnectServer(ActionEvent event) {

        if(!clientService.getConnectionServer() ){          // Checks flag of connection if is connected
           if (!portTextField.getText().isEmpty() && portTextField.getText().matches("^\\d{1,10}$"))  // If not empty and only numbers (Port Field)
                clientService.makeConnection(serverTextField.getText(), Integer.parseInt(portTextField.getText()));    // Makes connection to server
            System.out.println(clientService.getConnectionServer());
        }
        else{                                               // If client is already connected to the server
            serverMessageLabel.setVisible(true);
            serverMessageLabel.setText("ALREADY CONNECTED");
            return;
        }

        if(clientService.getConnectionServer()) {               // Shows message if succeeded
            StatusCircle.setFill(Paint.valueOf("green"));
            serverMessageLabel.setVisible(true);
            serverMessageLabel.setText("CONNECTION TO SERVER SUCCESSFUL");
            showLoginRegister();                                // If connection ok client can make login or register
        }
        else{                                                   // If not sucsseded
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

    private boolean checkTextField(){
        if (!nameField.getText().isEmpty() && !passwordField.getText().isEmpty())
            return true;
        else
            return false;
    }

    @Override
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }
}