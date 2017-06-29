package org.academiadecodigo.bootcamp8.freespeech.tests;

import org.academiadecodigo.bootcamp8.freespeech.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Crypto;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

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

        try {

            Crypto crypto = Crypto.getInstance();

            clientSocket = socket.accept();
            System.out.println("Client connected");

            Stream.writeObject(clientSocket.getOutputStream(), crypto.getKey());

            Message<String> message = new Message<>(MessageType.DATA, "Hello serial");
            Stream.writeObject(clientSocket.getOutputStream(), crypto.encryptObject(message));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Stream.close(clientSocket);
            Stream.close(socket);
        }

    }

}
