package org.academiadecodigo.bootcamp8.freespeech.client.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.academiadecodigo.bootcamp8.freespeech.client.service.RegistryService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.cryptography.CryptographyService;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Navigation;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Filipe on 02/07/2017.
 */
public class CryptographyController implements Controller {

    private CryptographyService cryptographyService;
    private Stage stage;
    private double[] position;

    @FXML
    private GridPane gridpane;

    public CryptographyController() {
        position = new double[2];
        cryptographyService = RegistryService.getInstance().get(CryptographyService.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDraggable();
    }

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

        //TODO observer design pattern

        waitForWindowToShow();
    }

    private void waitForWindowToShow() {
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                createBackgroundThread();
            }
        });
    }

    private void createBackgroundThread() {
        //TODO need concurrency tests.
        new Thread(new Runnable() {
            @Override
            public void run() {
                connectToServer();
            }
        }).start();
    }

    private synchronized void connectToServer() {
        cryptographyService.connect(Values.HOST, Values.SERVER_PORT);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Navigation.getInstance().loadScreen(Values.LOGIN_SCENE);
            }
        });
    }

}