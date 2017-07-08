package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import org.academiadecodigo.bootcamp8.freespeech.client.controller.user.Room;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.SessionContainer;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Parser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    public ChatRoomManager(TabPane tabPane) {
        roomMap = new HashMap<>();
        this.tabPane = tabPane;

        roomMap.put(getSelectedTab().getId(),new Room(getSelectedTab(),(TextArea)getSelectedTab().getContent()));
        randomNames = shuffleNames();
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
     * @return - the shuffled list.
     */
    private List<String> shuffleNames() {

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
    private void createNewTab(String user) {

        Room room = new Room(randomNames.remove(0),((Tab) tabPane.getTabs().toArray()[0]).getOnSelectionChanged(), user);

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        Date date = new Date();

        String id = SessionContainer.getInstance().getUsername() +
                "_" + user + dateFormat.format(date);

        Tab tab = new Tab("label " + id);
        tab.setId(id);
        tab.setTooltip(new Tooltip());
        setTabName(tab);
        tab.setText(randomNames.remove(0));

        addClosingTabHandler(tab);

        TextArea textArea = new TextArea();
        textArea.appendText("");
        textArea.setWrapText(true);
        textArea.setEditable(false);

        tab.setContent(textArea);

        tab.setOnSelectionChanged(((Tab) tabPane.getTabs().toArray()[0]).getOnSelectionChanged());

        //TODO
        tabId.put(id, tab);
        rooms.put(tab, textArea);

        HashSet<String> set = new HashSet<>();
        set.add(user);
        set.add(SessionContainer.getInstance().getUsername());
        tab.getTooltip().setText(Parser.setToString(set));
        usersPerTab.put(id, set);

        tabPane.getTabs().add(tab);
    }
}
