package org.academiadecodigo.bootcamp8.freespeech.server;

import java.io.IOException;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */
public class ServerStart {

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Usage: ChatServer <port>");
            System.exit(1);
        }

        Server server = new Server(Integer.parseInt(args[0]));

        try {
            server.init();
            server.start();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            server.closeServerSocket();

        }

    }
}
