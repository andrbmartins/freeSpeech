package org.academiadecodigo.bootcamp8.freespeech.client.service.login;

import org.academiadecodigo.bootcamp8.freespeech.client.service.Service;
import org.academiadecodigo.bootcamp8.freespeech.shared.communication.MapKey;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;

import java.util.Map;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> JPM Ramos
 * <Code Cadet> PedroMAlves
 */

public interface LoginService extends Service {

    /**
     * Sends a message with the specified type and content to the server.
     *
     * @param messageType    - the type.
     * @param messageContent - the content.
     */
    void sendMessage(MessageType messageType, Map<MapKey, String> messageContent);

    /**
     * Receives a message from the server.
     *
     * @return - the message.
     */
    Sendable<String> readMessage();

    /**
     * Receives the symmetric key.
     */
    void receiveSymKey();

    /**
     * Closes the application.
     */
    void exit();
}
