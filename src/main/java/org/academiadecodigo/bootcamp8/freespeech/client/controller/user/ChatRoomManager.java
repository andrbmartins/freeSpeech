package org.academiadecodigo.bootcamp8.freespeech.client.controller.user;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeech.client.controller.user.ClientController;
import org.academiadecodigo.bootcamp8.freespeech.client.controller.user.Room;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.SessionContainer;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Parser;

import java.util.*;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class ChatRoomManager {

    private Map<String, Room> roomMap;
    private TabPane tabPane;
    private List<String> randomNames;
    private ClientController clientController;

    public ChatRoomManager(ClientController clientController, TabPane tabPane) {
        this.clientController = clientController;
        this.tabPane = tabPane;
        roomMap = new HashMap<>();
        roomMap.put(getSelectedTab().getId(), new Room(getSelectedTab(), (TextArea) getSelectedTab().getContent()));
        randomNames = generateNames();
    }

    /**
     * Returns currently selected tab.
     *
     * @return - the tab.
     */
    public Tab getSelectedTab() {
        return tabPane.getSelectionModel().getSelectedItem();
    }

    /**
     * Adds select user to selected private room.
     */
    public void addToChat(String name) {

        String currentTabId = getSelectedTab().getId();

        if (getSelectedTab().getId().equals("lobbyTab") || roomMap.get(currentTabId).hasUser(name)) {
            return;
        }

        roomMap.get(currentTabId).addUser(name);
    }

    /**
     * Shuffles tab names list.
     *
     * @return - the shuffled list.
     */
    private List<String> generateNames() {

        List<String> list = Parser.arrayToList(Values.RANDOM_NAMES_ARRAY);
        Collections.shuffle(list);

        return list;
    }

    public Room getSelectedRoom() {
        return roomMap.get(getSelectedTab().getId());
    }

    /**
     * Creates a new tab for a private chat with the specified user.
     *
     * @param user - the user name.
     */
    public void createNewTab(String user) {

        Room room = new Room(randomNames.remove(0), ((Tab) tabPane.getTabs().toArray()[0]).getOnSelectionChanged(), user);

        addClosingTabHandler(room);

        tabPane.getTabs().add(room.getTab());

        roomMap.put(room.getId(),room);
    }

    /**
     * Defines closing behavior for the specified tab.
     *
     * @param room - the tab.
     */
    private void addClosingTabHandler(Room room) {

        if (roomMap.size() != 1) {
            room.getTab().setOnClosed(tabPane.getTabs().get(1).getOnClosed());
            return;
        }

        EventHandler<Event> event = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                Tab closedTab = (Tab) event.getSource();
                String leaveText = "< has left the building! >";

                Room room1 = roomMap.remove(closedTab.getId());
                room1.removeUser(SessionContainer.getInstance().getUsername());

                roomMap.remove(room1);
                clientController.sendMessage(leaveText, room1.getId(), room1.getUsersSet());
            }
        };

        room.getTab().setOnClosed(event);
    }

    public void printPrivateMessage(String tabId, String text, Set<String> usersSet) {

        if(!roomMap.containsKey(tabId)){
            createNewTab(tabId,usersSet);
        }

        roomMap.get(tabId).printPrivateMessage(text,usersSet);
    }

    private void createNewTab(String tabId, Set<String> usersSet) {

        Room room = new Room(tabId,randomName(),((Tab) tabPane.getTabs().toArray()[0]).getOnSelectionChanged(),usersSet);
        addClosingTabHandler(room);
        tabPane.getTabs().add(room.getTab());
        roomMap.put(room.getId(),room);
    }

    private String randomName() {

        if(randomNames.isEmpty()){
            randomNames = generateNames();
        }

        return randomNames.remove(0);
    }

    public void printMessage(String tabId, String text) {
        roomMap.get(tabId).appendText(text);
    }

}
