package org.academiadecodigo.bootcamp8.freespeech.client.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech.ServerResponseHandler;
import org.academiadecodigo.bootcamp8.freespeech.client.service.RegistryService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech.ClientService;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.*;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 * <Code Cadet> PedroMAlves
 */

//TODO documentation
public class ClientController implements Controller {

    @FXML
    private TabPane tabPane;
    @FXML
    private GridPane topBar;
    @FXML
    private TextArea lobbyTextArea;
    @FXML
    private TextArea inputTextArea;
    @FXML
    private ListView onlineUsersList;
    @FXML
    private Button exitButton;
    @FXML
    private VBox bioArea;
    @FXML
    private TextField nameBio;
    @FXML
    private TextField emailBio;
    @FXML
    private TextField dateBirthBio;
    @FXML
    private TextField dateRegistrationBio;


    private Stage stage;
    private ClientService clientService;
    private Map<Tab, TextArea> rooms;
    private TextArea currentRoom; //TODO can i not use this?
    private double[] position;

    public ClientController() {
        rooms = new HashMap<>();
        position = new double[2];
        clientService = RegistryService.getInstance().get(ClientService.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        rooms.put(getSelectedTab(), lobbyTextArea);
        setDraggableTopBar();
        focusUserInput();
        currentRoom = lobbyTextArea;
        new Thread(new ServerResponseHandler(clientService, this)).start();
        clientService.sendListRequest();
    }

    private Tab getSelectedTab() {
        return tabPane.getSelectionModel().getSelectedItem();
    }

    private void focusUserInput() {
        Platform.runLater(new Runnable() {
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

    @FXML
    void onSend(ActionEvent event) {
        clientService.sendUserText(inputTextArea);
    }

    @FXML
    void onSendKey(KeyEvent event) {

        if (event.isShiftDown() && event.getCode() == KeyCode.ENTER) {
            inputTextArea.appendText("\n");
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
        return rooms.get(getSelectedTab());
    }

    @Override
    public void setStage(Stage stage) {

        this.stage = stage;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        this.stage.setMinWidth(Values.CLIENT_WIDTH);
        this.stage.setMaxWidth(screen.getWidth());

        this.stage.setMinHeight(Values.CLIENT_HEIGHT);
        this.stage.setMaxHeight(screen.getHeight());
    }

    public void processUsersList(Sendable message) {

        Platform.runLater(new Runnable() {
            public void run() {
                List<String> list = (LinkedList<String>) message.getContent(List.class);
                ObservableList<String> observableList = FXCollections.observableList(list);
                onlineUsersList.setItems(observableList);
            }
        });
    }

    @FXML
    void getUserBio(MouseEvent event) {

        Object user = onlineUsersList.getSelectionModel().selectedItemProperty().get();

        if (user == null) {
            return;
        }

        clientService.sendBioRequest((String) user);
    }

    @FXML
    void editUserInfo(ActionEvent event) {
        //TODO to complete process
        EditBioDialog bio = new EditBioDialog();
        //TODO use the method by jp to retrieve bio to set on dialog text

        Optional<String[]> result = bio.showAndWait();
        if (result.isPresent() && result.get()[0].equals(DialogText.DELETE_ACCOUNT)) {
            confirmDelete();
        }
    }

    private void confirmDelete() {
        DeleteAccountDialog delete = new DeleteAccountDialog();
        Optional<String> password = delete.showAndWait();
        if (password.isPresent()) {
            clientService.deleteAccount(password.get());
        }
    }

    @FXML
    void changePassword(ActionEvent event) {
        ChangePassDialog change = new ChangePassDialog();

        while (true) {

            Optional<String[]> result = change.showAndWait();

            if (!result.isPresent()) {
                return;
            }
            if (areFieldsValid(result.get())) {
                clientService.changePassword(result.get());
                return;
            }
            userPromptExternal(Alert.AlertType.ERROR, DialogText.INVALID_FIELDS, DialogText.INVALID_FORM);
        }
    }

    private boolean areFieldsValid(String[] results) {
        for (String s : results) {
            if (s.isEmpty()) {
                return false;
            }
        }
        return results[1].equals(results[2]);
    }

    @FXML
    void onExit(ActionEvent event) {
        clientService.sendExit();
        Navigation.getInstance().close();
    }

    public void userPromptExternal(Alert.AlertType alertType, String title, String content) {
        Platform.runLater(new Runnable() {
            public void run() {
                Optional<ButtonType> r = userPrompt1(alertType, title, content);
                //stage.close();
                //Navigation.getInstance().close();
            }
        });

    }

    private Optional<ButtonType> userPrompt1(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();
    }


    @FXML
    void startPrivateChat(MouseEvent event) {

    }

    public void ShowUserBio(Sendable message) {

        List<String> list = (LinkedList<String>) message.getContent(List.class);

        if (list.isEmpty()) {
            return;
        }

        nameBio.setText(list.get(0));
        emailBio.setText(list.get(1));
        dateBirthBio.setText(list.get(2));
        dateRegistrationBio.setText(list.get(3));

    }

    @FXML
    void onReport(ActionEvent event) {
        String userToReport = onlineUsersList.getSelectionModel().getSelectedItem().toString();
        clientService.sendReport(userToReport);

    }
}