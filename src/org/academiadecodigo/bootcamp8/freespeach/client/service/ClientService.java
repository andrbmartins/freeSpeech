package org.academiadecodigo.bootcamp8.freespeach.client.service;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Sendable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public interface ClientService {

    /**
     * Sends a Message instance containing the specified element's text.
     *
     * @param textField - the specified element
     */
    void sendUserText(TextArea textField);

    /**
     * Sends an object to the server containing the specified element.
     *
     * @param message - the specified element.
     */
    void writeObject(Sendable message);

    Message readObject();

    void closeClientSocket();

    InputStream getInput() throws IOException;
}
