package org.academiadecodigo.bootcamp8.freespeach.tests.client;

import org.academiadecodigo.bootcamp8.freespeach.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeach.shared.utils.Crypto;
import org.academiadecodigo.bootcamp8.freespeach.shared.utils.Stream;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

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

            //Key key = (Key) Stream.readObject(socket.getInputStream());
            KeyPair key = null;
            try {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(4096);
                key = keyPairGenerator.generateKeyPair();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            Crypto crypto = new Crypto(Cipher.ENCRYPT_MODE, key.getPublic());
            System.out.println(crypto);

            Message<String> message = new Message<>(Message.Type.DATA, "Hello in serial");
            SealedObject object = null;
            try {
                object = new SealedObject(message, crypto.getCipher());
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }

            Stream.writeObject(socket.getOutputStream(), object);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
