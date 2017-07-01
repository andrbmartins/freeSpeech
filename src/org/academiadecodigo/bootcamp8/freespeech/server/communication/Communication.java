package org.academiadecodigo.bootcamp8.freespeech.server.communication;

import org.academiadecodigo.bootcamp8.freespeech.shared.message.SealedSendable;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Fábio Fernandes
 */
public interface Communication {

    void openStreams(Socket socket);

    void sendMessage(SealedSendable message);

    SealedSendable retrieveMessage();

    ObjectOutputStream getObjectOutputStream();
    ObjectInputStream getObjectInputStream();

}
