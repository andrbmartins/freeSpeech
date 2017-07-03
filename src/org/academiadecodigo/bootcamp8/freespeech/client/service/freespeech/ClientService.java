package org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeech.client.service.Service;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;

import javax.crypto.SealedObject;
import java.io.*;
import java.net.Socket;
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
    void sendUserText(TextArea textField);

    void sendPrivateText(TextArea textArea, String tabID, Set<String> destinySet);

    void sendUserData(File file, String destiny, String origin);

    void sendListRequest();
}
