package org.academiadecodigo.bootcamp8.client.controller;

import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.client.service.ClientService;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public interface Controller extends Initializable {

    void setClientService(ClientService clientService);

    void init();

}
