package org.academiadecodigo.bootcamp8.freespeech.serverapp.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.freespeech.serverapp.Utils;
import org.academiadecodigo.bootcamp8.freespeech.serverapp.service.DataBaseReader;
import javafx.fxml.FXML;
import org.academiadecodigo.bootcamp8.freespeech.serverapp.service.PasswordDialog;
import org.academiadecodigo.bootcamp8.freespeech.serverapp.service.WriteToFile;
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
    private Button deleteLog;

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

    /**
     * Enforces user password before allowing usage of main view. If cancel button is pressed closes app.
     * Validates password introduced to check admin level
     */
    public void validateUser() {

        Utils.AdminLevel adminLevel;

        if ((adminLevel = login()) == null) {
            stage.close();
            return;
        }

        validadeResult(adminLevel);
    }

    /**
     * Shows password dialog and sets admin level
     *
     * @return true is password is introduced. false if cancel button is pressed
     */
    private Utils.AdminLevel login() {

        PasswordDialog pd = new PasswordDialog();
        Optional<String> result = pd.showAndWait();

        if (result.isPresent()) {
            return reader.checkPassword(result.get());
        }

        return null;
    }

    /**
     * Perfoms changes to interface based on adminLevel or prompts user for new password
     *
     * @param adminLevel of enum type
     */
    private void validadeResult(Utils.AdminLevel adminLevel) {

        switch (adminLevel) {
            case ROOT:
                userPrompt(Alert.AlertType.INFORMATION, Utils.GRANTED, Utils.ROOT_ACCESS);
                break;
            case ADMIN:
                deleteLog.setVisible(false);
                userPrompt(Alert.AlertType.INFORMATION, Utils.GRANTED, Utils.ADMIN_ACCESS);
                break;
            case INVALID:
                userPrompt(Alert.AlertType.ERROR, Utils.INVALID_LOG, Utils.ENTER_VALID);
                validateUser();
                break;
        }
    }

    /**
     * Closes app smoothly on button click
     *
     * @param event
     */
    @FXML
    void exit(ActionEvent event) {
        stage.close();
    }

    /**
     * Get inserted query and processes it after validating the query
     *
     * @param event
     */
    @FXML
    void getCustomQuery(ActionEvent event) {

        String query = customQuery.getText();

        if (query.isEmpty()) {
            return;
        }

        if (isQueryAllowed(query)) {

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

    /**
     * Executes default query for all server related entries in log table
     *
     * @param event
     */
    @FXML
    void getServerInfo(ActionEvent event) {
        display.appendText(reader.executeQuery(Utils.SERVER_INFO) + "\n");
    }

    /**
     * Executes default query for all non server related entries in log table
     *
     * @param event
     */
    @FXML
    void getUserConnection(ActionEvent event) {
        display.appendText(reader.executeQuery(Utils.USER_CONNECTION) + "\n");
    }

    /**
     * Executes default query for number of registered users
     *
     * @param event
     */
    @FXML
    void registeredUsers(ActionEvent event) {
        display.appendText(reader.executeQuery(Utils.USERS_TABLE) + "\n");
    }

    /**
     * Executes default query for database related log table entries
     *
     * @param event
     */
    @FXML
    void getDataBaseLog(ActionEvent event) {
        display.appendText(reader.executeQuery(Utils.DB_LOG) + "\n");
    }

    /**
     * Clears "screen" textfield
     *
     * @param event
     */
    @FXML
    void refresh(ActionEvent event) {
        display.setText("");
    }

    /**
     * Writes to default file or previously user chosen file the info currently on "screen" texfield
     *
     * @param event
     */
    @FXML
    void save(ActionEvent event) {
        writer.save(display.getText());
        userPrompt(Alert.AlertType.INFORMATION, Utils.SAVING_FILE, Utils.FILE_SAVED);
    }

    /**
     * Writes to user chosen file the info currently on "screen" texfield
     *
     * @param event
     */
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

    /**
     * Clears all data in log table. Only root level can do this. It prompts user for confirmation
     *
     * @param event
     */
    @FXML
    void clearTable(ActionEvent event) {

        if (userPrompt(Alert.AlertType.CONFIRMATION, Utils.CONFIRM, Utils.CONFIRM_QUESTION).get() == ButtonType.OK) {
            String result = reader.clearTable() ? Utils.CLEARED : Utils.NOT_CLEARED;
            userPrompt(Alert.AlertType.INFORMATION, Utils.CLEARING_LOG, result);
        }

    }

    /**
     * Launches a prompt
     *
     * @param alertType type of prompt
     * @param title     text to be set as title
     * @param content   the actual message for user
     * @return the dialog as an Optional<ButtonType></>
     */
    private Optional<ButtonType> userPrompt(Alert.AlertType alertType, String title, String content) {

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();

    }

    /**
     * validates the query to see if it is allowed
     *
     * @param query
     * @return
     */
    private boolean isQueryAllowed(String query) {
        String lowerCase = query.toLowerCase();
        return (lowerCase.contains("delete") || lowerCase.contains("insert")
                || lowerCase.contains("update") || lowerCase.contains("admin"));
    }

    /**
     * Checks if message from server is an SQL exception and launches a prompt with the info
     *
     * @param result
     * @return
     */
    private boolean isError(String result) {
        return result.startsWith("Error: ");
    }

    /**
     * Method to be able to move the frame due to Undecorated window
     */
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

    /**
     * Sets database reader to query database
     *
     * @param reader
     */
    public void setReader(DataBaseReader reader) {
        this.reader = reader;
    }

    /**
     * Sets stage field
     *
     * @param stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets file writer to be used to save data to file
     *
     * @param writer
     */
    public void setWriter(WriteToFile writer) {
        this.writer = writer;
    }

}
