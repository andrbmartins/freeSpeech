package org.academiadecodigo.bootcamp8.client.controller;

import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.client.Client;
import org.academiadecodigo.bootcamp8.client.InputHandler;
import org.academiadecodigo.bootcamp8.client.model.ClientService;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public interface Controller extends Initializable {

    //TODO login/register - messageToSend

    //private Stage stage;
    //private ClientService clientService;

    @Override
    void initialize(URL location, ResourceBundle resources);

    void listen();

    /*private void listen() throws IOException, ClassNotFoundException {

        while (true) {

            System.out.println(clientService.getInput().readObject());

        }
    }*/

    void setStage(Stage stage); /* {
        this.stage = stage;
    }*/

    public ObjectOutputStream getOutput(); /* {
        return clientService.getOutput();
    }*/

    public void setClientService(ClientService clientService);/* {
        this.clientService = clientService;
    }*/

    public void init();/* {
        try {
            System.out.println(clientService);
            clientService.setupStreams();
            new Thread(new InputHandler(clientService.getInput(), this)).start();
            //TODO - invoke stuff
            listen();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("CLOSING");
            clientService.close();
        }
    }*/
}
