package org.academiadecodigo.bootcamp8.freespeech.client.controller.user;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.stage.StageStyle;
import org.academiadecodigo.bootcamp8.freespeech.client.controller.Controller;
import org.academiadecodigo.bootcamp8.freespeech.client.service.RegistryService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech.ClientService;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.*;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.DeleteAccountDialog;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.SessionContainer;
import org.academiadecodigo.bootcamp8.freespeech.client.dialog.ChangePassDialog;
import org.academiadecodigo.bootcamp8.freespeech.client.dialog.DialogText;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Parser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 * <Code Cadet> PedroMAlves
 */

public class ClientController implements Controller {

    @FXML
    private GridPane userButtons;
    @FXML
    private GridPane contactButtons;
    @FXML
    private Label username;
    @FXML
    private TabPane tabPane;
    @FXML
    private GridPane topBar;
    @FXML
    private TextArea lobbyTextArea;
    @FXML
    private TextArea inputTextArea;
    @FXML
    private ListView<String> onlineUsersList;
    @FXML
    private GridPane bioArea;
    @FXML
    private TextField nameBio;
    @FXML
    private TextField emailBio;
    @FXML
    private TextField searchBar;
    @FXML
    private Button clearSearchBar;
    @FXML
    private Button addToChatButton;
    @FXML
    private TextField dateBirthBio;
    @FXML
    private TextArea userBio;
    @FXML
    private Button privateChatButton;
    @FXML
    private Button updateProfile;
    @FXML
    private Button removeAccount;

    private Stage stage;
    private ClientService clientService;
    private ListView<String> originalOnlineUsersList;
    private ChatRoomManager chatRoomManager;
    private double[] stagePosition;

    public ClientController() {
        stagePosition = new double[2];
        clientService = RegistryService.getInstance().get(ClientService.class);
        originalOnlineUsersList = new ListView<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        username.setText(SessionContainer.getInstance().getUsername());
        chatRoomManager = new ChatRoomManager(this, tabPane);

        setDraggableTopBar();
        focusUserInput();

        new Thread(new ServerResponseHandler(this)).start();
    }

    /**
     * Puts cursor focus on user's input area.
     */
    private void focusUserInput() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                inputTextArea.requestFocus();
            }
        });
    }

    /**
     * Allows user to freely move the stage.
     */
    private void setDraggableTopBar() {

        topBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stagePosition[0] = event.getSceneX();
                stagePosition[1] = event.getSceneY();
            }
        });

        topBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - stagePosition[0]);
                stage.setY(event.getScreenY() - stagePosition[1]);
            }
        });
    }

    /**
     * Creates a new tab.
     */
    @FXML
    void startPrivateChat(ActionEvent event) {
        chatRoomManager.createNewTab(nameBio.getText());
    }

    /**
     * Adds select user to selected private room.
     */
    @FXML
    void addToChat(ActionEvent event) {
        chatRoomManager.addToChat(nameBio.getText());
    }

    /**
     * Sends a message to the currently selected tab.
     */
    @FXML
    void onSend(ActionEvent event) {

        if (chatRoomManager.getSelectedTab().getText().equals("Lobby")) {
            clientService.sendUserText(inputTextArea.getText());
            inputTextArea.clear();
            return;
        }

        Room currentRoom = chatRoomManager.getSelectedRoom();
        clientService.sendPrivateText(inputTextArea.getText(), currentRoom.getId(), currentRoom.getUsersSet());
        inputTextArea.clear();
    }

    /**
     * @see ClientController#onSend(ActionEvent)
     */
    @FXML
    void onSendKey(KeyEvent event) {

        if (event.isShiftDown() && event.getCode() == KeyCode.ENTER) {
            inputTextArea.appendText("\n");
            return;
        }

        if (event.getCode() == KeyCode.ENTER) {
            onSend(null);
            event.consume(); //nullifies enter key effect (new line)
        }
    }

    /**
     * sends the message text to the users in the usersSet for their tab with id tabid
     * @param text
     * @param tabId
     * @param usersSet
     */
    public void sendMessage(String text, String tabId, Set<String> usersSet) {
        clientService.sendPrivateText(text, tabId, usersSet);
    }

    /**
     * Opens dialog to select a file to send and sends that same file.
     *
     * @param event
     */
    @FXML
    void onFile(ActionEvent event) {

        final int MAX_FILE_SIZE = 5242880; // 5 MB
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);

        if (file == null) {
            return;
        }

        if (file.length() > MAX_FILE_SIZE) {
            notificationPrompt(Alert.AlertType.ERROR, DialogText.FILE_TOO_BIG);
            return;
        }

        clientService.sendUserData(file, nameBio.getText(), SessionContainer.getInstance().getUsername());
    }

    /**
     * Sets the stage and centers it.
     */
    @Override
    public void setStage(Stage stage) {

        this.stage = stage;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        this.stage.setMinWidth(Values.CLIENT_WIDTH);
        this.stage.setMaxWidth(screen.getWidth());

        this.stage.setMinHeight(Values.CLIENT_HEIGHT);
        this.stage.setMaxHeight(screen.getHeight());

        this.stage.setX(screen.getWidth() / 2 - stage.getWidth() / 2);
        this.stage.setY(screen.getHeight() / 2 - stage.getHeight() / 2);
    }

    /**
     * Displays the received message as a view list.
     *
     * @param message - the message.
     */
    public void processUsersList(Sendable<List<String>> message) {

        Platform.runLater(new Runnable() {
            public void run() {
                List<String> list = message.getContent();
                ObservableList<String> observableList = FXCollections.observableList(list);
                onlineUsersList.setItems(observableList);
                originalOnlineUsersList.setItems(observableList);
            }
        });
    }

    /**
     * Displays the selected user's profile, except the current user's one.
     */
    @FXML
    void getUserBio(MouseEvent event) {

        Object user = onlineUsersList.getSelectionModel().selectedItemProperty().get();

        if (user == null || user.equals(SessionContainer.getInstance().getUsername())) {
            onlineUsersList.getSelectionModel().clearSelection();
            return;
        }

        clientService.sendBioRequest(MessageType.PROFILE, (String) user);
    }

    /**
     * Opens dialog to allow user to change its password.
     */
    @FXML
    void changePassword(ActionEvent event) {

        ChangePassDialog change = new ChangePassDialog();

        while (true) {

            Optional<String[]> result = change.showAndWait();

            if (!result.isPresent()) {
                return;
            }
            if (isInputValid(result.get())) {
                clientService.changePassword(result.get());
                return;
            }
            notificationPrompt(Alert.AlertType.ERROR, DialogText.INVALID_FORM);
        }
    }

    /**
     * Verifies if all fields are valid and match.
     *
     * @param results - the fields' content.
     * @return true if all fields are valid, otherwise returns false.
     */
    private boolean isInputValid(String[] results) {

        for (String s : results) {
            if (s.isEmpty()) {
                return false;
            }
        }
        return results[1].equals(results[2]);
    }

    /**
     * Quits the application.
     */
    @FXML
    void onExit(ActionEvent event) {
        clientService.sendExit();
        Navigation.getInstance().close();
    }

    /**
     * Notifies user about account deletion success and quits application if successful.
     *
     * @param content - the dialog text.
     */
    public void quitPrompt(String content) {

        Alert.AlertType alertType = Alert.AlertType.INFORMATION;

        Platform.runLater(new Runnable() {
            public void run() {
                Optional<ButtonType> r = notificationPrompt(alertType, content);

                if (r.isPresent()) {
                    Navigation.getInstance().close();
                }
            }
        });
    }

    /**
     * Displays information to the user.
     *
     * @param content - the dialog text.
     */
    public void infoPrompt(String content) {

        Alert.AlertType alertType = Alert.AlertType.INFORMATION;

        Platform.runLater(new Runnable() {
            public void run() {
                Optional<ButtonType> r = notificationPrompt(alertType, content);
            }
        });
    }

    /**
     * Displays a notification to the user.
     * @param alertType - the notification type.
     * @param text - the notification text.
     * @return the user option
     */
    private Optional<ButtonType> notificationPrompt(Alert.AlertType alertType, String text) {

        Alert alert = new Alert(alertType);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.initStyle(StageStyle.UNDECORATED);
        alert.getDialogPane().getScene().getStylesheets().clear();
        alert.getDialogPane().getScene().getStylesheets().add(Values.STYLESHEET);

        alert.setHeaderText(null);
        alert.setContentText(text);

        return alert.showAndWait();
    }

    /**
     * @see ClientService#sendBioRequest(MessageType, String)
     */
    @FXML
    void editUserProfile(ActionEvent event) {
        clientService.sendBioRequest(MessageType.BIO, SessionContainer.getInstance().getUsername());
    }

    /**
     * Displays the users' account management menu.
     *
     * @param ownBio - the users' profile.
     * @param isUser - true if current user, false otherwise.
     */
    public void showProfile(Sendable<List<String>> ownBio, boolean isUser) {

        toggleBio(isUser);
        List<String> list = ownBio.getContent();
        displayProfile(list);
    }

    /**
     * Manages what commands are available according to current profile being shown.
     *
     * @param isUser - true if current user, false otherwise.
     */
    private void toggleBio(boolean isUser) {
        bioArea.setVisible(true);
        userButtons.setVisible(isUser);
        contactButtons.setVisible(!isUser);
        emailBio.setEditable(isUser);
        userBio.setEditable(isUser);
        userBio.setWrapText(true);
        dateBirthBio.setEditable(isUser);
    }

    /**
     * Sets values for profile fields.
     *
     * @param list - the values.
     */
    private void displayProfile(List<String> list) {
        nameBio.setText(list.get(0));
        emailBio.setText(list.get(1));
        dateBirthBio.setText(list.get(2));
        userBio.setText(list.get(3));
    }

    /**
     * @see ClientService#sendReport(String)
     */
    @FXML
    void onReport(ActionEvent event) {
        String userToReport = nameBio.getText();
        clientService.sendReport(userToReport);
    }

    /**
     * @see ClientService#deleteAccount(String)
     */
    @FXML
    void onRemoveAccount(ActionEvent event) {

        DeleteAccountDialog delete = new DeleteAccountDialog();

        Optional<String> password = delete.showAndWait();
        if (password.isPresent()) {
            clientService.deleteAccount(password.get());
        }

    }

    /**
     * @see ClientService#updateBio(List)
     */
    @FXML
    void onUpdateProfile(ActionEvent event) {

        List<String> updatedBio = new LinkedList<>();
        updatedBio.add(SessionContainer.getInstance().getUsername());
        updatedBio.add(emailBio.getText());
        updatedBio.add(dateBirthBio.getText());
        updatedBio.add(userBio.getText());

        clientService.updateBio(updatedBio);
    }

    /**
     * Filters online users list by the given input.
     */
    @FXML
    void search(KeyEvent event) {

        FilteredList<String> list = originalOnlineUsersList.getItems().filtered(new Predicate<String>() {
            @Override
            public boolean test(String o) {
                return o.contains(searchBar.getText());
            }
        });

        try {
            int code = java.awt.event.KeyEvent.VK_ENTER;
            new Robot().keyPress(code);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        onlineUsersList.setItems(list);

    }

    /**
     * Clears search bar.
     */
    @FXML
    void clearSearchBar(ActionEvent event) {
        searchBar.clear();
        onlineUsersList.setItems(originalOnlineUsersList.getItems());
    }

    /**
     * Closes user profile.
     */
    @FXML
    void closeProfile(ActionEvent event) {
        onlineUsersList.getSelectionModel().clearSelection();
        bioArea.setVisible(false);
        contactButtons.setVisible(false);
    }

    /**
     * Displays a notification to the user of a received file and allows him to save it.
     *
     * @param sender        - the origin.
     * @param byteList      - the file content.
     * @param fileExtension - the file extension.
     */
    public void saveFile(String sender, List<Byte> byteList, String fileExtension) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("You have received a file from " + sender);
                fileChooser.setInitialFileName("file." + fileExtension);
                File file = fileChooser.showSaveDialog(new Stage());

                try {

                    if (file != null && file.createNewFile()) {
                        Parser.byteListToFile(Parser.listToByteArray(byteList), file);
                    }

                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        };
        Platform.runLater(runnable);
    }

    /**
     * Prints a private message in the corresponding room.
     *
     * @param tabId         - the room identifier.
     * @param destinyString - the users in the room.
     * @param text          - the message content.
     */
    public void printPrivateChat(String tabId, String destinyString, String text) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatRoomManager.printPrivateMessage(tabId, text, Parser.stringToSet(destinyString));

            }
        });

    }

    /**
     * Prints a message in the public room.
     *
     * @param messageText - the message content.
     */
    public void printToLobby(String messageText) {
        chatRoomManager.printMessage("lobbyTab", messageText);
    }
}