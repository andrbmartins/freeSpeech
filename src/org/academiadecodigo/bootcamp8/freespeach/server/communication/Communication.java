package org.academiadecodigo.bootcamp8.freespeach.server.communication;

import org.academiadecodigo.bootcamp8.freespeach.shared.message.Sendable;

import java.net.Socket;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Fábio Fernandes
 */
public interface Communication {

    void openStreams(Socket socket);

    // TODO use correct interface Sendable<TYPE>
    void sendMessage(Sendable message);

    // TODO use correct interface Sendable<TYPE>
    Sendable retrieveMessage();

}
