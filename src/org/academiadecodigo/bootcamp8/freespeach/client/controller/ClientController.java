package org.academiadecodigo.bootcamp8.freespeach.client.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.freespeach.client.InputHandler;
import org.academiadecodigo.bootcamp8.freespeach.client.service.ClientService;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class ClientController implements Controller {

    @FXML
    private TabPane tabPane;
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
    @FXML
    private TextArea inputTextArea;

    private Stage stage;
    private ClientService clientService;

    //One input handler for each tab
    private ExecutorService inputHandlerPool = Executors.newCachedThreadPool();
    private TextArea currentRoom;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void init() {

        currentRoom = lobbyTextArea;
        System.out.println(clientService);

        //TESTING SERVER -----------------------------
 /*
         HashMap<String, String> no = new HashMap<>();
         no.put("username", "bqdjhv");
         Message<HashMap> message = new Message(Message.Type.LOGIN, no);
         System.out.println("SENDING HARCODED");
         clientService.writeObject(message);
         System.out.println("SENT HARDCODED");
 */
        //-----------------------------

        InputHandler inputHandler = new InputHandler(clientService.getInput(), this);
        inputHandlerPool.submit(inputHandler);
    }


    @Override
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @FXML
    void onTabClicked(Event event) {
        //TODO switch between tabs - map tab/textarea

        //Tab tab = tabPane.getSelectionModel().getSelectedItem();

    }

    @FXML
    void onSend(ActionEvent event) {
        clientService.sendUserText(inputTextArea);
    }

    @FXML
    void onSendKey(KeyEvent event) {

        // PARA PASSWORDS
        //TODO hashing MessageDigest - use instance SHA - reset - update - byte[] = .digest
        // new BigInteger = 1, byte[] - bigInt.toString(BASE) - while hash < 32 chars -> hash = "0" + hash

        if (event.isShiftDown() && event.getCode() == KeyCode.ENTER) {
            inputTextArea.setText(inputTextArea.getText() + "\n");
            inputTextArea.positionCaret(inputTextArea.getText().length());
            return;
        }

        if (event.getCode() == KeyCode.ENTER) {
            clientService.sendUserText(inputTextArea);
            event.consume(); //nullifies enter key effect (new line)
        }
    }


    public void add(String message) {
        //TODO get responses
    }
}
