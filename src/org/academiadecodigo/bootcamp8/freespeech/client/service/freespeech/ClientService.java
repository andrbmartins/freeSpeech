package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeech.client.service.Service;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

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

    /**
     * Sends an object to the server containing the specified element.
     *
     * @param message - the specified element.
     */
    void writeObject(Sendable message);

    //Message readObject();

    InputStream getInput() throws IOException;

    void setSocket(Socket socket);

    void sendUserData(File file);

    void sendListRequest();
}
