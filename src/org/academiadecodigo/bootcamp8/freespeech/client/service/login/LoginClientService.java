package org.academiadecodigo.bootcamp8.freespeech.client.service.login;

import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.SealedSendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> JPM Ramos
 * <Code Cadet> PedroMAlves
 */

public class LoginClientService implements LoginService {

    private Socket clientSocket;

    @Override
    public void makeConnection(String server, int port) {

        try {
            clientSocket = new Socket(server, port);
            Session.getInstance().setUserSocket(clientSocket);
            exchangeKeys();

        } catch (IOException e) {
            //TODO - unable to connect message
        }
    }

    private void exchangeKeys() {
        Key foreignKey = (Key) Stream.read(Session.getInput());
        Session.getCrypto().setForeignKey(foreignKey);
        Stream.write(Session.getOutput(), Session.getCrypto().getPublicKey());
    }

    @Override
    public String getName() {
        return LoginService.class.getSimpleName();
    }

}
