package org.academiadecodigo.bootcamp8.freespeach.tests.client;

import org.academiadecodigo.bootcamp8.freespeach.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeach.shared.utils.Crypto;
import org.academiadecodigo.bootcamp8.freespeach.shared.utils.Stream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
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

            Message<String> message = new Message<>(MessageType.DATA, "Hello in serial");

            /*Key key = (Key) Stream.readObject(socket.getInputStream());
            System.out.println(key);
            Crypto crypto = new Crypto(Cipher.ENCRYPT_MODE, key);

            System.out.println(message);
            Stream.writeObject(socket.getOutputStream(), crypto.getCipher(), message);*/

            Stream.writeObject(socket.getOutputStream(), message);

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
