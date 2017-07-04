package org.academiadecodigo.bootcamp8.freespeech.server;

import org.academiadecodigo.bootcamp8.freespeech.server.model.ConnectionManager;
import org.academiadecodigo.bootcamp8.freespeech.server.model.User;
import org.academiadecodigo.bootcamp8.freespeech.server.service.JdbcUserService;
import org.academiadecodigo.bootcamp8.freespeech.server.service.UserService;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.logger.Logger;

import java.io.IOException;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */
public class ServerStart {

    public static void main(String[] args) {

        Server server;
        ConnectionManager connectionManager = new ConnectionManager();
        UserService userService = new JdbcUserService(connectionManager);
        Logger.setConnection(connectionManager);

        if (args.length < 1 || Integer.parseInt(args[0]) < 1025) {
            System.out.println("Client app is configured to connect to port 4040.");
            server = new Server(userService);
        } else {
            server = new Server(Integer.parseInt(args[0]), userService);
        }

        try {

            server.init();
            server.start();

        } catch (IOException e) {
            e.printStackTrace(); //TODO log?
        } finally {
            server.stop();
        }
    }
}