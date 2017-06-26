package org.academiadecodigo.bootcamp8.freespeach.server.communication;

import org.academiadecodigo.bootcamp8.freespeach.shared.message.Sendable;

import java.net.Socket;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Fábio Fernandes
 */
public interface Communication {

    void openInputChannel(Socket socket);

    void openOutputChannel(Socket socket);

    void sendMessage(Sendable message);

    Sendable retrieveMessage();

}
