package org.academiadecodigo.bootcamp8.freespeech.client.controller.user;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.SessionContainer;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Parser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class Room {

    private Tab tab;
    private TextArea textArea;
    private Set<String> usersSet;

    /**
     * Constructor used when the Client receives a private message for a room that he hasn't got yet
     * @param id Room ID
     * @param tabName Name for the Tab
     * @param onSelectionChanged Event handler for when the client selects the tab
     * @param usersSet Set of users in that room
     */
    public Room(String id, String tabName, EventHandler<Event> onSelectionChanged, Set<String> usersSet) {
        tab = new Tab(tabName);
        tab.setId(id);
        tab.setTooltip(new Tooltip());
        tab.setOnSelectionChanged(onSelectionChanged);

        textArea = new TextArea();
        textArea.appendText("");
        textArea.setWrapText(true);
        textArea.setEditable(false);

        tab.setContent(textArea);

        this.usersSet = usersSet;
        tab.getTooltip().setText(Parser.setToString(usersSet));

    }

    /**
     * Constructor used when creating the Lobby room
     * @param selectedTab Lobby Tab defined on the fxml
     * @param content Lobby TextArea defined on the fxml
     */
    public Room(Tab selectedTab, TextArea content) {
        tab = selectedTab;
        textArea = content;
        usersSet = null;
    }

    /**
     * Constructor used when the client wants to create a new private chat room with a certain user
     * @param name Name for the Tab
     * @param onSelectionChanged Event handler for when the client selects the tab
     * @param user Name of the user that the client wants to chat
     */
    public Room(String name, EventHandler<Event> onSelectionChanged, String user) {
        tab = new Tab(name);
        tab.setId(generateId(user));
        tab.setTooltip(new Tooltip());
        tab.setOnSelectionChanged(onSelectionChanged);

        textArea = new TextArea();
        textArea.appendText("");
        textArea.setWrapText(true);
        textArea.setEditable(false);

        tab.setContent(textArea);

        usersSet = new HashSet<>();
        usersSet.add(user);
        usersSet.add(SessionContainer.getInstance().getUsername());
        tab.getTooltip().setText(Parser.setToString(usersSet));



    }

    /**
     * Returns the Id of the room. It coincides with the id of the tab.
     *
     * @return Returns the id.
     */
    public String getId(){
        return tab.getId();
    }

    /**
     * Updates the set of users in the room
     *
     * @param newList The new set of users
     */
    private void updateUsersList(Set<String> newList){
        usersSet = newList;
    }

    /**
     * Removes the user from the room
     *
     * @param user User to remove from the room
     * @return returns if the operation was successfull
     */
    public boolean removeUser(String user){
        return usersSet.remove(user);
    }

    /**
     * Adds user to the room
     *
     * @param user User to add to the room
     * @return returns if the operation was successfull
     */
    public boolean addUser(String user){
        return usersSet.add(user);
    }

    /**
     * Prints the message text in the TextArea
     *
     * @param text Message to print
     */
    public void appendText(String text){
        textArea.appendText((textArea.getText().isEmpty() ? "" : "\n") + text);
    }

    /**
     * Return the set of users in the room.
     *
     * @return Returns the set of users in the room.
     */
    public Set<String> getUsersSet() {
        return Collections.unmodifiableSet(usersSet);
    }

    /**
     * Verifies if the certain user is on the the room.
     *
     * @param name Name of the user
     * @return Returns true if the user is in the room and false otherwise
     */
    public boolean hasUser(String name) {
        return usersSet.contains(name);
    }

    /**
     * Generates an Id for the room.
     * The ID has the following structure: <clientName>_<userName>_yyyyMMdd_HHmmssSSS
     *
     * @param user The name of the user
     * @return Returns the generated id
     */
    private String generateId(String user){
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        Date date = new Date();

        return SessionContainer.getInstance().getUsername() + "_" + user + "_" + dateFormat.format(date);
    }

    /**
     * Prints the message text and updates the set of users in the room.
     *
     * @param text Text to print.
     * @param usersSet Set of users to update
     */
    public void printPrivateMessage(String text, Set<String> usersSet) {

        updateUsersList(usersSet);
        updateTooltipText();
        textArea.appendText((textArea.getText().isEmpty() ? "" : "\n") + text);
    }

    /**
     * Updates the tooltip text
     */
    private void updateTooltipText() {
        tab.getTooltip().setText(Parser.setToString(usersSet));
    }

    /**
     * Returns the tab of the room.
     * @return Returns the tab of the room.
     */
    public Tab getTab() {
        return tab;
    }
}
