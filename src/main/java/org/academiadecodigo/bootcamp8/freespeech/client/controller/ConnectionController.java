package org.academiadecodigo.bootcamp8.freespeech.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.academiadecodigo.bootcamp8.freespeech.client.service.RegistryService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.connection.ConnectionService;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Navigation;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class ConnectionController implements Controller {

    private ConnectionService connectionService;
    private Stage stage;
    private double[] position;

    @FXML
    private GridPane gridpane;
    @FXML
    private Label connectingLabel;
    @FXML
    private GridPane connectionButtons;


    public ConnectionController() {
        position = new double[2];
        connectionService = RegistryService.getInstance().get(ConnectionService.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDraggable();
    }

    /**
     * Adds mouse event listeners to allow for stage to be dragged.
     */
    private void setDraggable() {
        gridpane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                position[0] = event.getSceneX();
                position[1] = event.getSceneY();
            }
        });

        gridpane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - position[0]);
                stage.setY(event.getScreenY() - position[1]);
            }
        });
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;

        this.stage.setMinHeight(Values.LOGIN_HEIGHT);
        this.stage.setMaxHeight(Values.LOGIN_HEIGHT);

        this.stage.setMinWidth(Values.LOGIN_WIDTH);
        this.stage.setMaxWidth(Values.LOGIN_WIDTH);

        waitForWindowToShow();
    }

    /**
     * Adds an event handler to be executed when the stage shows on screen.
     */
    private void waitForWindowToShow() {
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                createBackgroundThread();
            }
        });
    }

    /**
     * Instantiates a thread responsible for establishing a connection to the server.
     */
    private void createBackgroundThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connectToServer();
            }
        }).start();
    }

    /**
     * Attempts to connect to the server.
     */
    private void connectToServer() {

        boolean success = connectionService.connect(Values.HOST, Values.SERVER_PORT);

        if (!success) {
            connectionFailed();
            return;
        }

        loadLoginScreen();
    }

    private void loadLoginScreen() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Navigation.getInstance().loadScreen(Values.LOGIN_SCENE);
            }
        });
    }

    /**
     * Notifies user that connection failed and provides him with the option to retry or quit.
     */
    private void connectionFailed() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                connectingLabel.setText("Unable to connect to server.");
                connectionButtons.setVisible(true);
            }
        });
    }

    /**
     * @see ConnectionController#connectToServer()
     */
    @FXML
    void onReconnect(ActionEvent event) {
        connectionButtons.setVisible(false);
        connectingLabel.setText("Connecting to server. Please stand by.");
        createBackgroundThread();
    }

    /**
     * Quits the application.
     */
    @FXML
    void onClose(ActionEvent event) {
        Navigation.getInstance().close();
    }

}