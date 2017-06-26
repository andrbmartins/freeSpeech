package org.academiadecodigo.bootcamp8.freespeach.server.communication;

import org.academiadecodigo.bootcamp8.freespeach.shared.message.Sendable;

import java.net.Socket;

/**
 * Created by codecadet on 26/06/17.
 */
public interface Communication {

    void openInputChannel(Socket socket);

    void openOutputChannel(Socket socket);

    void sendMessage(Sendable message);

    Sendable retrieveMessage();

}
