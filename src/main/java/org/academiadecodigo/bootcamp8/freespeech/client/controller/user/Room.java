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

    public Room(Tab selectedTab, TextArea content) {
        tab = selectedTab;
        textArea = content;
        usersSet = null;
    }

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

    public String getId(){
        return tab.getId();
    }

    public String getText(){
        return tab.getText();
    }

    public void updateUsersList(Set<String> newList){
        usersSet = newList;
    }

    public boolean removeUser(String user){
        return usersSet.remove(user);
    }

    public boolean addUser(String user){
        return usersSet.add(user);
    }

    public void appendText(String text){
        textArea.appendText((textArea.getText().isEmpty() ? "" : "\n") + text);
    }

    public Set<String> getUsersSet() {
        return usersSet;
    }

    public boolean hasUser(String name) {
        return usersSet.contains(name);
    }

    private String generateId(String user){
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        Date date = new Date();

        return SessionContainer.getInstance().getUsername() + "_" + user + dateFormat.format(date);
    }

    public void printPrivateMessage(String text, Set<String> usersSet) {

        updateUsersList(usersSet);
        updateTooltipText();
        textArea.appendText((textArea.getText().isEmpty() ? "" : "\n") + text);
    }

    private void updateTooltipText() {
        tab.getTooltip().setText(Parser.setToString(usersSet));
    }

    public Tab getTab() {
        return tab;
    }
}
