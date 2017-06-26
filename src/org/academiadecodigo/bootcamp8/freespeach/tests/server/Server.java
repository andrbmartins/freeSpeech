package org.academiadecodigo.bootcamp8.freespeach.tests.server;

import org.academiadecodigo.bootcamp8.freespeach.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeach.shared.utils.Crypto;
import org.academiadecodigo.bootcamp8.freespeach.shared.utils.Stream;

import javax.crypto.Cipher;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author by André Martins <Code Cadet>
 *         Tests (25/06/2017)
 *         <Academia de Código_>
 */
public class Server {

    private ServerSocket socket;
    private Socket clientSocket;

    public static void main(String[] args) {

        Server server = new Server();
        server.init();

    }

    private Server() {

        try {
            socket = new ServerSocket(12345);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void init() {

        Crypto crypto = new Crypto(Cipher.DECRYPT_MODE);

        try {

            clientSocket = socket.accept();
            System.out.println("Client connected");

            /*Stream.writeObject(clientSocket.getOutputStream(), crypto.getPublicKey());

            Object object = Stream.readObject(clientSocket.getInputStream(), crypto.getCipher());
            System.out.println(object);*/

            Sendable message = (Sendable) Stream.readObject(clientSocket.getInputStream());
            System.out.println(message.getType());
            System.out.println(message.getContent());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (socket != null) {
                try {
                    clientSocket.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
