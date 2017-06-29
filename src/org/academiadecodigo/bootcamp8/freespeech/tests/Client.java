package org.academiadecodigo.bootcamp8.freespeech.tests;

import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Crypto;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import javax.crypto.SealedObject;
import java.io.*;
import java.net.Socket;
import java.security.Key;

/**
 * @author by André Martins <Code Cadet>
 *         Tests (25/06/2017)
 *         <Academia de Código_>
 */
public class Client {

    Socket socket;

    public static void main(String[] args) {

        Client client = new Client();
        System.out.println("Connected to server");
        client.init();

    }

    private Client() {

        try {
            socket = new Socket("localhost", 12345);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void init() {

        try {

            Crypto crypto = Crypto.getInstance();

            Key key = (Key) Stream.readObject(socket.getInputStream());

            SealedObject sealedObject = (SealedObject) Stream.readObject(socket.getInputStream());
            System.out.println(sealedObject);
            Object object = crypto.decryptObject(sealedObject, key);
            System.out.println(object);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Stream.close(socket);
        }

    }

}
