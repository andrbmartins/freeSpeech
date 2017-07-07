package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import org.academiadecodigo.bootcamp8.freespeech.client.service.Service;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;

import java.io.*;
import java.util.List;
import java.util.Set;

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
    void sendUserText(String textField);

    void sendPrivateText(String textArea, String tabID, Set<String> destinySet);

    void sendUserData(File file, String destiny, String origin);

    void sendBioRequest(MessageType type, String username);

    void changePassword(String[] passSet);

    void sendExit();

    void deleteAccount(String password);

    void sendReport(String userToReport);

    void updateBio(List<String> updatedBio);
}
