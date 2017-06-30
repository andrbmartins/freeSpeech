package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;


import javafx.scene.control.ListView;

import java.io.OutputStream;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class OnlineUsersHandler {

    private ListView<String> onlineUsersList;
    private OutputStream outputStream;


    public OnlineUsersHandler(ListView<String> onlineUsersList, OutputStream outputStream) {
        this.onlineUsersList = onlineUsersList;
        this.outputStream = outputStream;
    }

}
