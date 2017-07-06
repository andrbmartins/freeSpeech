package org.academiadecodigo.bootcamp8.freespeech.client.service.connection;

import org.academiadecodigo.bootcamp8.freespeech.client.utils.SessionContainer;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class FreeSpeechConnectionService implements ConnectionService {

    /**
     * @see ConnectionService#connect(String, int)
     * @param server
     * @param port
     * @return
     */
    @Override
    public boolean connect(String server, int port) {

        try {
            Socket clientSocket = new Socket(server, port);
            SessionContainer.getInstance().setUserSocket(clientSocket);
            exchangeKeys();
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Receives server's public key and sends client's public key to server.
     */
    private void exchangeKeys() {

        SessionContainer sessionContainer = SessionContainer.getInstance();

        Key foreignKey = (Key) Stream.read(sessionContainer.getInput());
        sessionContainer.getCrypto().setForeignKey(foreignKey);
        Stream.write(sessionContainer.getOutput(), sessionContainer.getCrypto().getPublicKey());
    }

    @Override
    public String getName() {
        return ConnectionService.class.getSimpleName();
    }
}
