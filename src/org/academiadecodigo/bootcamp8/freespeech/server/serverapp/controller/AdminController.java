package org.academiadecodigo.bootcamp8.freespeech.server.serverapp.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.freespeech.server.serverapp.Utils;
import org.academiadecodigo.bootcamp8.freespeech.server.serverapp.service.DataBaseReader;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.academiadecodigo.bootcamp8.freespeech.server.serverapp.service.WriteToFile;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */
public class AdminController implements Initializable {
    @FXML
    private TextField customQuery;

    @FXML
    private TextArea display;

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
        String query = customQuery.getText();
        if (query.isEmpty()) {
            return;
        }
        if (!isQueryAllowed(query)) {
            userPrompt(Alert.AlertType.ERROR, Utils.VALIDATING_QUERY, Utils.INVALID_QUERY);
            customQuery.setText("");
            return;
        }

        String result = reader.executeQuery(query);
        if (isError(result)) {
            userPrompt(Alert.AlertType.ERROR, Utils.VALIDATING_QUERY, result);
            return;
        }
        display.appendText(result + "\n");
        customQuery.setText("");
    }


    @FXML
    void getServerInfo(ActionEvent event) {
        display.appendText(reader.executeQuery(Utils.SERVER_INFO) + "\n");
    }

    @FXML
    void getUserConnection(ActionEvent event) {
        display.appendText(reader.executeQuery(Utils.USER_CONNECTION) + "\n");

    }

    @FXML
    void registeredUsers(ActionEvent event) {
        display.appendText(reader.executeQuery(Utils.USERS_TABLE) + "\n");
    }

    @FXML
    void refresh(ActionEvent event) {
        display.setText("");
    }

    @FXML
    void save(ActionEvent event) {
        writer.save(display.getText());
        userPrompt(Alert.AlertType.INFORMATION, Utils.SAVING_FILE, Utils.FILE_SAVED);
    }

    @FXML
    void saveAs(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        File file;
        if ((file = chooser.showSaveDialog(stage)) == null) {
            return;
        }
        writer.setSavingFile(file);
        writer.save(display.getText());
        userPrompt(Alert.AlertType.INFORMATION, Utils.SAVING_FILE, Utils.FILE_SAVED);
    }

    @FXML
    void clearTable(ActionEvent event) {
        if (userPrompt(Alert.AlertType.CONFIRMATION, Utils.CONFIRM, Utils.CONFIRM_QUESTION).get() == ButtonType.OK) {
            String result = reader.clearTable() ? Utils.CLEARED : Utils.NOT_CLEARED;
            userPrompt(Alert.AlertType.INFORMATION, Utils.CLEARING_LOG, result);
        }
    }

    private Optional<ButtonType> userPrompt(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    private boolean isQueryAllowed(String query) {
        String lowerCase = query.toLowerCase();
        if (customQuery.getText().contains("delete") || customQuery.getText().contains("insert")
                || customQuery.getText().contains("update")) {
            return false;
        }
        return true;
    }

    private boolean isError(String result) {
        return result.startsWith("Error: ");
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
