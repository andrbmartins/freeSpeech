package org.academiadecodigo.bootcamp8.freespeech.client.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.*;
import javafx.event.Event;
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

import org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech.ServerResponseHandler;
import org.academiadecodigo.bootcamp8.freespeech.client.service.RegistryService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech.ClientService;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.*;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.SessionContainer;
import org.academiadecodigo.bootcamp8.freespeech.dialog.ChangePassDialog;
import org.academiadecodigo.bootcamp8.freespeech.dialog.DeleteAccountDialog;
import org.academiadecodigo.bootcamp8.freespeech.dialog.DialogText;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Parser;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 * <Code Cadet> PedroMAlves
 */

//TODO documentation
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
    private Map<Tab, TextArea> rooms;
    private Map<String, Tab> tabId;
    private Map<String, Set<String>> usersPerTab;
    private double[] stagePosition;

    public ClientController() {
        rooms = new HashMap<>();
        tabId = new HashMap<>();
        usersPerTab = new HashMap<>();
        stagePosition = new double[2];
        clientService = RegistryService.getInstance().get(ClientService.class);
        originalOnlineUsersList = new ListView<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //TODO just testing, don't delete yet
        username.setText(SessionContainer.getInstance().getUsername());
        username.setStyle("-fx-text-fill: #000000;");

        rooms.put(getSelectedTab(), lobbyTextArea);
        tabId.put(getSelectedTab().getText(), getSelectedTab());

        setDraggableTopBar();
        focusUserInput();

        new Thread(new ServerResponseHandler(this)).start();
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
     * Gets name of selected user and creates new private chat (new tab)
     * @param event
     */
    @FXML
    void onActionPrivateChat(ActionEvent event) {

        String name = onlineUsersList.getSelectionModel().getSelectedItem();
        String clientName = SessionContainer.getInstance().getUsername();

        if (!clientName.equals(name)) {
            createNewTab(name);

        }
        addToChatButton.setDisable(false);
        addToChatButton.setVisible(true);
    }

    /**
     * Adds new users to the current private chat
     * @param event
     */
    @FXML
    void onAddToChatAction(ActionEvent event) {

        String name;

        if((name = onlineUsersList.getSelectionModel().getSelectedItem()) == null){
            return;
        }

        String currentTabId = getSelectedTab().getId();
        String userSelected = onlineUsersList.getSelectionModel().getSelectedItem();

        if (getSelectedTab().getText().equals("Lobby") || usersPerTab.get(currentTabId).contains(userSelected)){
            return;
        }

        Set<String> userSet = usersPerTab.get(currentTabId);

        if (!userSet.contains(name)) {
            userSet.add(name);
            usersPerTab.replace(currentTabId, userSet);
        }

    }

    /**
     * Sends message for currently active tab
     * @param event
     */
    @FXML
    void onSend(ActionEvent event) {

        if (getSelectedTab().getText().equals("Lobby")) {
            clientService.sendUserText(inputTextArea.getText());
            inputTextArea.clear();
        } else {
            String tabID = getSelectedTab().getId();
            clientService.sendPrivateText(inputTextArea.getText(), tabID, usersPerTab.get(tabID));
            inputTextArea.clear();
        }

    }

    /**
     * Sends message on enter key
     * @param event
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
     * Opens dialog to select file to send on button pressed if a file is selected and file is less than 50MB
     * @param event
     */
    @FXML
    void onFile(ActionEvent event) {
        final int MAX_FILE_SIZE = 52428800; //50 MB
        File file = new FileChooser().showOpenDialog(stage);

        if (file == null) {
            return;
        }

        if (file.length() > MAX_FILE_SIZE) {
            userPrompt1(Alert.AlertType.INFORMATION, DialogText.FILE_TRANSFER, DialogText.FILE_TOO_BIG);
            return;
        }

        String destiny = onlineUsersList.getSelectionModel().getSelectedItem();
        clientService.sendUserData(file, destiny, SessionContainer.getInstance().getUsername());
    }

    /*
    public TextArea getCurrentRoom() {
        return rooms.get(getSelectedTab());
    }
*/

    //TODO
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
     * Updates logged users list
     * @param message
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
     * Processes request for the bio of other users. Returns if an empty field or the own name were selected
     * @param event
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
     * Opens change password dialog. Validates if fields aren't empty
     * @param event
     */
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
            userPrompt1(Alert.AlertType.ERROR, DialogText.INVALID_FIELDS, DialogText.INVALID_FORM);
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


    public void userPromptQuit(Alert.AlertType alertType, String title, String content) {
        Platform.runLater(new Runnable() {
            public void run() {
                Optional<ButtonType> r = userPrompt1(alertType, title, content);
                if (r.isPresent()) {
                    Navigation.getInstance().close();
                }
            }
        });

    }

    public void userPromptExternal(Alert.AlertType alertType, String title, String content) {
        Platform.runLater(new Runnable() {
            public void run() {
                Optional<ButtonType> r = userPrompt1(alertType, title, content);
            }
        });
    }

    //TODO css for dialogs
    private Optional<ButtonType> userPrompt1(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    @FXML
    void editUserInfo(ActionEvent event) {
        clientService.sendBioRequest(MessageType.BIO, SessionContainer.getInstance().getUsername());
    }


    public void showOwnBio(Sendable<List<String>> ownBio) {

        bioArea.setVisible(true);
        userButtons.setVisible(true);
        contactButtons.setVisible(false);
        emailBio.setEditable(true);
        userBio.setEditable(true);
        dateBirthBio.setEditable(true);

        List<String> list = ownBio.getContent();
        // TODO resolve unchecked casts

        setBioInfo(list);
    }

    public void showUserBio(Sendable<List<String>> message) {

        bioArea.setVisible(true);
        userButtons.setVisible(false);
        contactButtons.setVisible(true);
        emailBio.setEditable(false);
        userBio.setEditable(false);
        dateBirthBio.setEditable(false);

        List<String> list = message.getContent();

        setBioInfo(list);
    }

    private void setBioInfo(List<String> list) {

        nameBio.setText(list.get(0));
        emailBio.setText(list.get(1));
        dateBirthBio.setText(list.get(2));
        userBio.setText(list.get(3));
    }

    @FXML
    void onReport(ActionEvent event) {
        String userToReport = onlineUsersList.getSelectionModel().getSelectedItem();
        clientService.sendReport(userToReport);
    }

    @FXML
    void onRemoveAccount(ActionEvent event) {
        DeleteAccountDialog delete = new DeleteAccountDialog();
        Optional<String> password = delete.showAndWait();
        if (password.isPresent()) {
            clientService.deleteAccount(password.get());
        }

    }


    @FXML
    void onUpdateProfile(ActionEvent event) {

        List<String> updatedBio = new LinkedList<>();
        updatedBio.add(SessionContainer.getInstance().getUsername());
        updatedBio.add(emailBio.getText());
        updatedBio.add(dateBirthBio.getText());
        updatedBio.add(userBio.getText());

        clientService.updateBio(updatedBio);

    }


    private void createNewTab(String user) {

        //gets current time in the that I want
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date();

        String id = SessionContainer.getInstance().getUsername() +
                "_" + user + dateFormat.format(date);

        Tab tab = new Tab("label " + id);
        tab.setId(id);
        tab.setTooltip(new Tooltip());

        addClosingTabHandler(tab);

        TextArea textArea = new TextArea();
        textArea.appendText("");
        tab.setContent(textArea);

        //add the onAction event to the tab
        tab.setOnSelectionChanged(((Tab) tabPane.getTabs().toArray()[0]).getOnSelectionChanged());

        tabId.put(id, tab);
        rooms.put(tab, textArea);
        //creating the data to update usersPerTab and tooltip
        HashSet<String> set = new HashSet<>();
        set.add(user);
        set.add(SessionContainer.getInstance().getUsername());
        tab.getTooltip().setText(Parser.setToString(set));
        usersPerTab.put(id, set);

        tabPane.getTabs().add(tab);
    }

    private void addClosingTabHandler(Tab tab) {

        if (tabPane.getTabs().size() != 1) {
            tab.setOnClosed(tabPane.getTabs().get(1).getOnClosed());
            return;
        }

        EventHandler<Event> event = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                Tab tab1 = (Tab) event.getSource();
                String leaveText = "< " + SessionContainer.getInstance().getUsername() + " has left the building! >";

                //removes the tab from the various maps.
                Set<String> destinySet = usersPerTab.remove(tab1.getId());
                destinySet.remove(SessionContainer.getInstance().getUsername());

                rooms.remove(tab1);
                tabId.remove(tab1.getId());
                //

                clientService.sendPrivateText(leaveText, tab1.getId(), destinySet);
            }


        };

        tab.setOnClosed(event);
    }

    public void createReceivedTab(Set<String> users, String id) {

        Tab tab = new Tab("label " + id);
        tab.setId(id);
        tab.setTooltip(new Tooltip(Parser.setToString(users)));

        TextArea textArea = new TextArea();
        textArea.appendText("");
        textArea.setEditable(false);
        tab.setContent(textArea);

        //add the onAction event to the tab
        tab.setOnSelectionChanged(((Tab) tabPane.getTabs().toArray()[0]).getOnSelectionChanged());

        tabId.put(id, tab);
        rooms.put(tab, textArea);
        usersPerTab.put(id, users);

        addClosingTabHandler(tab);

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                synchronized (this) {
                    tabPane.getTabs().add(tab);
                    notifyAll();
                }
            }
        };

        Platform.runLater(runnable);

        while (!tabPane.getTabs().contains(tab)) {

            try {
                synchronized (runnable) {
                    runnable.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public TextArea getDestinyRoom(String tabId) {
        Tab tab = this.tabId.get(tabId);
        return (tab != null) ? rooms.get(tab) : null;
    }

    public void updateUsersSet(String tabId, Set<String> destinySet) {
        usersPerTab.replace(tabId, destinySet);
    }


    public File filePopUPWindow() {

        FileChooser fileChooser = new FileChooser();

        return fileChooser.showSaveDialog(stage);

    }


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

    @FXML
    void clearSearchBar(ActionEvent event) {

        searchBar.clear();
        onlineUsersList.setItems(originalOnlineUsersList.getItems());

    }

    @FXML
    void closeProfile(ActionEvent event) {
        onlineUsersList.getSelectionModel().clearSelection();
        bioArea.setVisible(false);
        contactButtons.setVisible(false);

    }

    public void updateTooltipText(String tabId, Set<String> destinySet) {
        this.tabId.get(tabId).getTooltip().setText(Parser.setToString(destinySet));
    }
}