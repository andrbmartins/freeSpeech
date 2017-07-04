package org.academiadecodigo.bootcamp8.freespeech.client.service.cryptography;

import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class FreeSpeechCryptographyService implements CryptographyService {

    /**
     * @see CryptographyService#connect(String, int)
     * @param server - the host.
     * @param port - the port.
     */
    @Override
    public void connect(String server, int port) {

        try {
                Socket clientSocket = new Socket(server, port);
                Session.getInstance().setUserSocket(clientSocket);
                exchangeKeys();

        } catch (IOException e) {
            //TODO - unable to connect message
        }
    }

    /**
     * Receives server's public key and sends client's public key to server.
     */
    private void exchangeKeys() {

        Key foreignKey = (Key) Stream.read(Session.getInput());
        Session.getCrypto().setForeignKey(foreignKey);
        Stream.write(Session.getOutput(), Session.getCrypto().getPublicKey());
    }

    @Override
    public String getName() {
        return CryptographyService.class.getSimpleName();
    }
}
