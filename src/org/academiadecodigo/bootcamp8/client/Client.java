package org.academiadecodigo.bootcamp8.client;

import javafx.application.Application;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.client.utils.Navigation;
import org.academiadecodigo.bootcamp8.client.utils.Values;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class Client extends Application {

    private final String HOST = "localhost";
    private final int SERVER_PORT = 8080;
    private InetAddress address;
    private Socket clientSocket;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        try {
            address = InetAddress.getByName(HOST);
            clientSocket = new Socket(address, SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Navigation.getInstance().setStage(primaryStage);
        Navigation.getInstance().loadScreen(Values.START_SCREEN);
        Navigation.getInstance().fetchController(Values.START_SCREEN).setClientSocket(clientSocket);
    }
}