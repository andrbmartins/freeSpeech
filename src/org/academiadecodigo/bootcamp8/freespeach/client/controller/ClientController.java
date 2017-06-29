package org.academiadecodigo.bootcamp8.freespeach.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.academiadecodigo.bootcamp8.freespeach.client.InputHandler;
import org.academiadecodigo.bootcamp8.freespeach.client.service.ClientService;
import org.academiadecodigo.bootcamp8.freespeach.client.service.Services;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class ClientController implements Controller {

    @FXML private TabPane tabPane;
    @FXML private GridPane topBar;
    @FXML private TextArea lobbyTextArea;
    @FXML private TextArea privateTextArea;
    @FXML private TextArea inputTextArea;

    private Stage stage;
    private ClientService clientService;
    private List<TextArea> rooms;
    private ExecutorService inputHandlerPool;
    private TextArea currentRoom;
    private double[] position;

    public ClientController() {
        inputHandlerPool = Executors.newCachedThreadPool();
        rooms = new LinkedList<>();
        rooms.add(lobbyTextArea);
        position = new double[2];
        clientService = Services.getLoginService();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rooms.remove(0); //lobbyTextArea is null until this point
        rooms.add(lobbyTextArea);
        rooms.add(privateTextArea); //TODO REMOVE THIS WHEN WHISPER IS IMPLEMENTED
        setDraggableTopBar();
        focusUserInput();
        currentRoom = lobbyTextArea;
    }

    private void focusUserInput() {
        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {
                        inputTextArea.requestFocus();
                    }
                });
    }

    private void setDraggableTopBar() {
        topBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                position[0] = event.getSceneX();
                position[1] = event.getSceneY();
            }
        });

        topBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - position[0]);
                stage.setY(event.getScreenY() - position[1]);
            }
        });
    }

    /**
     * Instantiates a new thread to handle server responses for the current room.
     */
    @Override
    public void init() {
        Runnable inputHandler;
        try {
            inputHandler = new InputHandler(clientService.getInput(), currentRoom);
            inputHandlerPool.submit(inputHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void onTabClicked(Event event) {
        int tab = tabPane.getSelectionModel().getSelectedIndex();
        currentRoom = rooms.get(tab);
    }

    @FXML
    void onSend(ActionEvent event) {
        clientService.sendUserText(inputTextArea);
    }

    @FXML
    void onSendKey(KeyEvent event) {
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

    @FXML
    void onFile(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
           clientService.sendUserData(file);
        }

    }

    public TextArea getCurrentRoom() {
        return currentRoom;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
