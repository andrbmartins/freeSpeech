package org.academiadecodigo.bootcamp8.freespeach.tests.server;

import org.academiadecodigo.bootcamp8.freespeach.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeach.shared.utils.Stream;

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

            clientSocket = socket.accept();
            System.out.println("Client connected");

            Message<String> message = new Message<>(Message.Type.DATA, "Hello in serial");
            System.out.println(message);

            Stream.writeObject(clientSocket.getOutputStream(), message);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
