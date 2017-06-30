package org.academiadecodigo.bootcamp8.freespeech.server.serverapp.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.freespeech.server.serverapp.Utils;
import org.academiadecodigo.bootcamp8.freespeech.server.serverapp.service.DataBaseReader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.academiadecodigo.bootcamp8.freespeech.server.serverapp.service.WriteToFile;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */
public class AdminController implements Initializable {
    @FXML
    private Button buttonCustomQ;

    @FXML
    private TextField customQuery;

    @FXML
    private TextArea display;

    @FXML
    private Button saveFile;

    @FXML
    private Button exit;

    @FXML
    private GridPane parentGrid;

    private WriteToFile writer;
    private DataBaseReader reader;
    private Stage stage;
    private double x;
    private double y;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDraggableMainFrame();
    }

    @FXML
    void exit(ActionEvent event) {
        stage.close();
    }

    @FXML
    void getCustomQuery(ActionEvent event) {
        if (!customQuery.getText().isEmpty()) {
            display.appendText(reader.executeQuery(customQuery.getText()));
            customQuery.setText("");
            reader.closeStatement();
        }
    }

    @FXML
    void getServerError(ActionEvent event) {
        reader.executeQuery(Utils.SERVER_ERROR);
        reader.closeStatement();
    }

    @FXML
    void getUserConnection(ActionEvent event) {
        reader.executeQuery(Utils.USER_CONNECTION);
        reader.closeStatement();
    }

    @FXML
    void refresh(ActionEvent event) {
        display.setText("");
    }

    @FXML
    void save(ActionEvent event) {
        writer.save(display.getText());
        fileSaved();
    }

    @FXML
    void saveAs(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        writer.setSavingFile(chooser.showSaveDialog(stage));
        writer.save(display.getText());
        fileSaved();
    }

    @FXML
    void clearTable(ActionEvent event) {
        String result = reader.clearTable() ? Utils.CLEARED : Utils.NOT_CLEARED;
        display.setText(result);
    }

    private void fileSaved() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("File Saved");
        alert.setHeaderText(null);
        alert.setContentText(Utils.FILE_SAVED);

        alert.showAndWait();
    }


    private void setDraggableMainFrame() {

        parentGrid.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                x = event.getSceneX();
                y = event.getSceneY();
            }
        });

        parentGrid.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - x);
                stage.setY(event.getScreenY() - y);
            }
        });
    }


    public void setReader(DataBaseReader reader) {
        this.reader = reader;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setWriter(WriteToFile writer) {
        this.writer = writer;
    }
}
