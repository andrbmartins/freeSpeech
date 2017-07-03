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
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
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
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
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
    private MenuItem Bio_Menu;
    @FXML
    private VBox Bio;
    @FXML
    private ImageView Bio_Image;
    @FXML
    private TextArea Bio_Data;

    private Stage stage;
    private ClientService clientService;
    private Map<Tab, TextArea> rooms;
    private Map<String, Tab> tabNames;
    //private TextArea currentRoom; //TODO can i not use this?
    private Tab currentTab;
    private double[] position;

    public ClientController() {
        rooms = new HashMap<>();
        tabNames = new HashMap<>();
        position = new double[2];
        clientService = RegistryService.getInstance().get(ClientService.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Tab tab = getSelectedTab();

        rooms.put(tab, lobbyTextArea);
        tabNames.put(tab.getText(), tab);

        setDraggableTopBar();
        focusUserInput();
        currentTab = tab;
        //currentRoom = lobbyTextArea;
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
    void onTabClicked(Event event) {
        currentTab = getSelectedTab();
        //currentRoom = rooms.get(getSelectedTab());
    }

    @FXML
    void onActionPrivateChat(ActionEvent event) {
        String name = (String) onlineUsersList.getSelectionModel().getSelectedItem().toString();
        String clientName = Session.getInstance().getUsername();

        if (!clientName.equals(name)) {
            createNewTab(name);

        }
    }

    @FXML
    void onSend(ActionEvent event) {

        System.out.println("parent: " + currentTab);
        System.out.println("parent ID: " + currentTab.getText());
        if (currentTab.getText().equals("Lobby")) {
            System.out.println("mensagem da tab Lobby --" + currentTab.getText());
            clientService.sendUserText(inputTextArea);
        } else {
            System.out.println("mensagem da tab " + currentTab.getText());
            clientService.sendPrivateText(inputTextArea, currentTab.getText());
        }
    }

    @FXML
    void onSendKey(KeyEvent event) {
        if (event.isShiftDown() && event.getCode() == KeyCode.ENTER) {
            inputTextArea.appendText("\n");
            return;
        }
        if (event.getCode() == KeyCode.ENTER) {

            System.out.println("parent: " + currentTab);
            System.out.println("parent ID: " + currentTab.getText());

            if (currentTab.getText().equals("Lobby")) {
                System.out.println("mensagem da tab Lobby --" + currentTab.getText());
                clientService.sendUserText(inputTextArea);
            } else {
                System.out.println("mensagem da tab " + currentTab.getText());
                clientService.sendPrivateText(inputTextArea, currentTab.getText());
            }

            //clientService.sendUserText(inputTextArea);
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

    public Tab getCurrentRoom() {
        return currentTab;
        //return currentRoom;
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
    void ShowBio(ActionEvent event) {
        System.out.println("Show bio");
    }


    public void addMessageToTab(String user, String text) {

        System.out.println("CHAT PRIVADO PARA " + user);

        if (!tabNames.containsKey(user)) {

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    System.out.println("tab the user " + user + "ainda nao existe");
                    createNewTab(user);
                    //adds the text to the textArea of the tab that has as name the String user
                    rooms.get(tabNames.get(user)).appendText(text);
                }
            });

        } else {

            //adds the text to the textArea of the tab that has as name the String user
            rooms.get(tabNames.get(user)).appendText(text);
        }

    }

    private void createNewTab(String user) {

        Tab tab = new Tab(user);
        TextArea textArea = new TextArea();
        textArea.appendText("");
        tab.setContent(textArea);

        //add teh onAction event to the tab
        tab.setOnSelectionChanged(((Tab) tabPane.getTabs().toArray()[0]).getOnSelectionChanged());

        tabNames.put(user, tab);
        rooms.put(tab, textArea);

        tabPane.getTabs().add(tab);
    }

    public Tab getDestinyRoom(String user) {

        if (tabNames.get(user) == null) {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    createNewTab(user);
                }
            };

            Platform.runLater(runnable);
            int i = 0;
            while (tabNames.get(user) == null) {
                i++;
            }
            System.out.println(i);
        }

        return tabNames.get(user);
    }
}