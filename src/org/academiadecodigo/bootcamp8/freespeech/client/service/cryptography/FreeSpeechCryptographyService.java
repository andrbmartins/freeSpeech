package org.academiadecodigo.bootcamp8.freespeech.client.service.cryptography;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.freespeech.client.controller.CryptographyController;
import org.academiadecodigo.bootcamp8.freespeech.client.controller.LoginController;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Navigation;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Crypto;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;

/**
 * Created by Filipe on 02/07/2017.
 */
public class FreeSpeechCryptographyService implements CryptographyService {

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
