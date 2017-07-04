package org.academiadecodigo.bootcamp8.freespeech.client.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    @FXML private TabPane tabPane;
    @FXML private GridPane topBar;
    @FXML private TextArea lobbyTextArea;
    @FXML private TextArea inputTextArea;
    @FXML private ListView onlineUsersList;
    @FXML
    private Button exitButton;
    @FXML
    private VBox bioArea;
    @FXML
    private TextField nameBio;
    @FXML
    private TextField emailBio;
    @FXML
    private TextArea Bio_Data;
    @FXML
    private Button addToChatButton;
    @FXML private TextField dateBirthBio;
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
    private Map<Tab, TextArea> rooms;
    private Map<String, Tab> tabId;
    private Map<String,Set<String>> usersPerTab;
    private double[] position;

    public ClientController() {
        rooms = new HashMap<>();
        tabId = new HashMap<>();
        usersPerTab = new HashMap<>();
        position = new double[2];
        clientService = RegistryService.getInstance().get(ClientService.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Tab tab = getSelectedTab();

        rooms.put(tab, lobbyTextArea);
        tabId.put(tab.getText(), tab);

        setDraggableTopBar();
        focusUserInput();
        //currentRoom = lobbyTextArea;
        new Thread(new ServerResponseHandler(clientService, this)).start();
    }

    public Tab getSelectedTab() {
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
    void onActionPrivateChat(ActionEvent event) {

        String name = (String) onlineUsersList.getSelectionModel().getSelectedItem().toString();
        String clientName = Session.getInstance().getUsername();

        if (!clientName.equals(name)) {
            createNewTab(name);

        }
        addToChatButton.setDisable(false);
        addToChatButton.setVisible(true);
    }

    @FXML
    void onAddToChatAction(ActionEvent event) {

        String name = onlineUsersList.getSelectionModel().getSelectedItem().toString();

        String tabId = tabPane.getSelectionModel().getSelectedItem().getId();
        Set<String > userSet =usersPerTab.get(tabId);

        if(!userSet.contains(name)){
            userSet.add(name);
            usersPerTab.replace(tabId,userSet);
        }

    }

    @FXML
    void onSend(ActionEvent event) {

        System.out.println("parent: " + getSelectedTab());
        System.out.println("parent ID: " + getSelectedTab().getText());
        if (getSelectedTab().getText().equals("Lobby")) {
            System.out.println("mensagem da tab Lobby --" + getSelectedTab().getText());
            clientService.sendUserText(inputTextArea);
        } else {
            // TODO empty else
        }
    }

    @FXML
    void onSendKey(KeyEvent event) {

        if (event.isShiftDown() && event.getCode() == KeyCode.ENTER) {
            inputTextArea.appendText("\n");
            return;
        }
        if (event.getCode() == KeyCode.ENTER) {

            System.out.println("parent: " + getSelectedTab());
            System.out.println("parent ID: " + getSelectedTab().getText());

            if (getSelectedTab().getText().equals("Lobby")) {
                System.out.println("mensagem da tab Lobby --" + getSelectedTab().getText());
                clientService.sendUserText(inputTextArea);

            } else {
                String tabID = getSelectedTab().getId();
                clientService.sendPrivateText(inputTextArea,tabID,usersPerTab.get(tabID));
            }

            //clientService.sendUserText(inputTextArea);
            event.consume(); //nullifies enter key effect (new line)
        }
    }

    @FXML
    void onFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);

        String destiny = onlineUsersList.getSelectionModel().getSelectedItem().toString();

        if (file != null && destiny != null) {
            clientService.sendUserData(file, destiny, Session.getInstance().getUsername());
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

        clientService.sendBioRequest(MessageType.BIO, (String) user);
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

    @FXML
    void editUserInfo(ActionEvent event) {
        clientService.sendBioRequest(MessageType.OWN_BIO, Session.getUsername());
    }


    public void setOwnBio(Sendable ownBio) {
        privateChatButton.setVisible(false);
        removeAccount.setVisible(true);
        updateProfile.setVisible(true);

        List<String> list = (LinkedList<String>) ownBio.getContent(List.class);

        if (list.isEmpty()) {
            System.out.println("inside the list.isEmpty");
            //set fields editable
            return;
        }
        setBioInfo(list);
    }

    public void showUserBio(Sendable message) {
        privateChatButton.setVisible(true);
        removeAccount.setVisible(false);
        updateProfile.setVisible(false);

        List<String> list = (LinkedList<String>) message.getContent(List.class);

        if (list.isEmpty()) {
            return;
        }
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
        String userToReport = onlineUsersList.getSelectionModel().getSelectedItem().toString();
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
        updatedBio.add(Session.getUsername());
        updatedBio.add(emailBio.getText());
        updatedBio.add(dateBirthBio.getText());
        updatedBio.add(userBio.getText());

        clientService.updateBio(updatedBio);

    }


    private void createNewTab(String user) {

        //gets current time in the that I want
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date();

        String id = Session.getInstance().getUsername() +
                "_" + user + dateFormat.format(date);

        Tab tab = new Tab("label " + id);
        tab.setId(id);

        TextArea textArea = new TextArea();
        textArea.appendText("");
        tab.setContent(textArea);

        //add the onAction event to the tab
        tab.setOnSelectionChanged(((Tab) tabPane.getTabs().toArray()[0]).getOnSelectionChanged());

        tabId.put(id, tab);
        rooms.put(tab, textArea);
        //creating the data to update usersPerTab
        HashSet<String> set = new HashSet<>();
        set.add(user);
        set.add(Session.getInstance().getUsername());
        usersPerTab.put(id,set);

        tabPane.getTabs().add(tab);
    }

    public void createReceivedTab(Set<String> users, String id) {

        Tab tab = new Tab("label " + id);
        tab.setId(id);

        TextArea textArea = new TextArea();
        textArea.appendText("");
        textArea.setEditable(false);
        tab.setContent(textArea);

        //add the onAction event to the tab
        tab.setOnSelectionChanged(((Tab) tabPane.getTabs().toArray()[0]).getOnSelectionChanged());

        tabId.put(id, tab);
        rooms.put(tab, textArea);
        usersPerTab.put(id,users);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                tabPane.getTabs().add(tab);
            }
        };

        Platform.runLater(runnable);

        while (!tabPane.getTabs().contains(tab)){}

    }

    public TextArea getDestinyRoom(String tabId) {
        Tab tab = this.tabId.get(tabId);
        return (tab != null)? rooms.get(tab) : null;
    }

    public void updateUsersSet(String tabId, Set<String> destinySet) {
        usersPerTab.replace(tabId,destinySet);
    }


    public File filePopUPWindow() {

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(stage);

        return file;

    }
}