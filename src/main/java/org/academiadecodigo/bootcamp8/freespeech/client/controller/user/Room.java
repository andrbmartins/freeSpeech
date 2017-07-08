package org.academiadecodigo.bootcamp8.freespeech.client.controller.user;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.SessionContainer;

import java.util.List;
import java.util.Set;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class Room {

    private Tab tab;
    private TextArea textArea;
    private Set<String> usersList;

    public Room(Tab tab, TextArea textArea, Set<String> usersList) {
        this.tab = tab;
        this.textArea = textArea;
        this.usersList = usersList;
        tab.setContent(this.textArea);
    }

    public Room(Tab tab, Set<String> usersList) {
        this.tab = tab;
        this.usersList = usersList;


    }

    public Room(String id, String tabName, Set<String> usersList) {
        tab = new Tab(tabName);
        tab.setId(id);
        tab.setTooltip(new Tooltip());
        this.usersList = usersList;
    }

    public Room(Tab selectedTab, TextArea content) {
        tab = selectedTab;
        textArea = content;
        usersList = null;
    }

    public Room(String name, EventHandler<Event> onSelectionChanged, String user) {
        tab = new Tab(name);
        tab.setId(generateId(user));
        tab.setTooltip(new Tooltip());


    }

    public String getId(){
        return tab.getId();
    }

    public String getText(){
        return tab.getText();
    }

    public void updateUsersList(Set<String> newList){
        usersList = newList;
    }

    public boolean removeUser(String user){
        return usersList.remove(user);
    }

    public boolean addUser(String user){
        return usersList.add(user);
    }

    public void appendText(String text){
        textArea.appendText(text);
    }

    public Set<String> getUsersList() {
        return usersList;
    }

    public boolean hasUser(String name) {
        return usersList.contains(name);
    }

    private String generateId(String user){


    }

    /**
     * Defines closing behavior for the specified tab.
     *
     * @param tab - the tab.
     */
    private void addClosingTabHandler(Tab tab) {

        if (tabPane.getTabs().size() != 1) {
            tab.setOnClosed(tabPane.getTabs().get(1).getOnClosed());
            return;
        }

        EventHandler<Event> event = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                Tab closedTab = (Tab) event.getSource();
                String leaveText = "< has left the building! >";

                Set<String> destinySet = usersPerTab.remove(closedTab.getId());
                destinySet.remove(SessionContainer.getInstance().getUsername());

                rooms.remove(closedTab);
                tabId.remove(closedTab.getId());

                clientService.sendPrivateText(leaveText, closedTab.getId(), destinySet);
            }
        };

        tab.setOnClosed(event);
    }
}
