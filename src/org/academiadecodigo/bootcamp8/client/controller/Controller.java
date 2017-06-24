package org.academiadecodigo.bootcamp8.client.controller;

import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.client.InputHandler;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public abstract class Controller implements Initializable {

    //TODO login/register - messageToSend

    private Stage stage;
    private Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            setupStreams();
            new Thread(new InputHandler(input, this)).start();
            //TODO - invoke stuff
            listen();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void listen() {

        while (true) {

            //TODO

        }
    }

    private void setupStreams() throws IOException {

        input = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        output = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
    }

    private void close() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

}
