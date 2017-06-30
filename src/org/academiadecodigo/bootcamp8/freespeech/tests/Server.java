package org.academiadecodigo.bootcamp8.freespeech.tests;

import org.academiadecodigo.bootcamp8.freespeech.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Crypto;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import javax.crypto.SealedObject;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;

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

        try {

            Crypto crypto = Crypto.getInstance();
            crypto.setSymmetricKey();

            clientSocket = socket.accept();
            System.out.println("Client connected");

            // Get the client public key to encrypt the secret key
            Object object = Stream.readObject(clientSocket.getInputStream());
            System.out.println(object);
            crypto.setForeignPublicKey((Key) object);

            // Encrypt and send the symmetric key
            SealedObject sealedObject = crypto.encryptObject(crypto.getSymmetricKey(), crypto.getForeignPublicKey());
            Stream.writeObject(clientSocket.getOutputStream(), sealedObject);

            // Encrypt and send message
            sealedObject = crypto.encryptObject("Hello", crypto.getSymmetricKey());
            Stream.writeObject(clientSocket.getOutputStream(), sealedObject);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Stream.close(clientSocket);
            Stream.close(socket);
        }

    }

}
