package org.academiadecodigo.bootcamp8.freespeech.client.controller.user;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
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
     * @param name The name of the user to add to the chat room
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

    /**
     * Return to room currently showing on the screen.
     *
     * @return The room selected
     */
    public Room getSelectedRoom() {
        return roomMap.get(getSelectedTab().getId());
    }

    /**
     * Creates a new room for a private chat with the specified user.
     * This method is called when the client wants to create a private chat room with the user.
     *
     * @param user - the user name.
     */
    public void createNewRoom(String user) {

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

    /**
     * Prints the message text on the Room with id tabid and updates the set of users in that room.
     *
     * @param tabId Id of the room
     * @param text Message to print
     * @param usersSet Set of users in that room
     */
    public void printPrivateMessage(String tabId, String text, Set<String> usersSet) {

        if(!roomMap.containsKey(tabId)){
            createNewRoom(tabId,usersSet);
        }

        roomMap.get(tabId).printPrivateMessage(text,usersSet);
    }

    /**
     * Creates a new room for a private chat with the specified set of users.
     * This method is called when the client receives a message from a private chat room that was created by other user.
     *
     * @param tabId
     * @param usersSet
     */
    private void createNewRoom(String tabId, Set<String> usersSet) {

        Room room = new Room(tabId,randomName(),((Tab) tabPane.getTabs().toArray()[0]).getOnSelectionChanged(),usersSet);
        addClosingTabHandler(room);
        tabPane.getTabs().add(room.getTab());
        roomMap.put(room.getId(),room);
    }

    /**
     * return a random room that will appear on the tab of that room
     *
     * @return
     */
    private String randomName() {

        if(randomNames.isEmpty()){
            randomNames = generateNames();
        }

        return randomNames.remove(0);
    }

    /**
     * Prints the message text in the room with id tabId.
     *
     * @param tabId Id of the room.
     * @param text Message to print.
     */
    public void printMessage(String tabId, String text) {
        roomMap.get(tabId).appendText(text);
    }

}
