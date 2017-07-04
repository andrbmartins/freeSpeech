package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeech.client.service.Service;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;

import java.io.*;
import java.util.List;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public interface ClientService extends Service {

    /**
     * Sends a Message instance containing the specified element's text.
     *
     * @param textField - the specified element
     */
    void sendUserText(TextArea textField);

    void sendBioRequest(MessageType type, String username);

    void sendUserData(File file);

    void changePassword(String[] passSet);

    void sendExit();

    void deleteAccount(String password);

    void sendReport(String userToReport);

    void updateBio(List<String> updatedBio);
}
