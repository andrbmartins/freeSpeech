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
     * Sends a Message instance containing the specified text.
     *
     * @param text - the specified text.
     */
    void sendUserText(String text);

    /**
     * Sends a Message instance to the specified chat room.
     *
     * @param text       - the message content.
     * @param tabID      - the room identifier.
     * @param destinySet - the room participants.
     */
    void sendPrivateText(String text, String tabID, Set<String> destinySet);

    /**
     * Sends a Message instance to the specified user.
     *
     * @param file    - the content.
     * @param destiny - the destination.
     * @param origin  - the sender.
     */
    void sendUserData(File file, String destiny, String origin);

    /**
     * Asks the server to display a user profile of the specified type.
     *
     * @param type     - the type of profile.
     * @param username - the user.
     */
    void sendBioRequest(MessageType type, String username);

    /**
     * Sends a password change request to the server.
     *
     * @param passSet - the old and new password.
     */
    void changePassword(String[] passSet);

    /**
     * Informs the server that the user will quit the application.
     */
    void sendExit();

    /**
     * Sends the server a request do delete the account upon confirmation.
     *
     * @param password - the confirmation.
     */
    void deleteAccount(String password);

    /**
     * Sends the server a report request.
     *
     * @param userToReport - the reported user.
     */
    void sendReport(String userToReport);

    /**
     * Sends the server a request to update the user's profile.
     *
     * @param updatedBio - the profile components.
     */
    void updateBio(List<String> updatedBio);
}
