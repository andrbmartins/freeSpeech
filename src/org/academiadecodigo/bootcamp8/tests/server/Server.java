package org.academiadecodigo.bootcamp8.tests.server;

import org.academiadecodigo.bootcamp8.message.Message;
import org.academiadecodigo.bootcamp8.message.Sendable;
import org.academiadecodigo.bootcamp8.tests.utils.Converter;

import java.io.BufferedOutputStream;
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

        String str = "Hello in serial";
        Message<String> message = new Message<String>(Message.Type.DATA, str);
        System.out.println(message);

        server.write(message);

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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void write(Sendable message) {

        BufferedOutputStream out = null;

        try {

            out = new BufferedOutputStream(clientSocket.getOutputStream());
            out.write(Converter.toBytes(message));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
