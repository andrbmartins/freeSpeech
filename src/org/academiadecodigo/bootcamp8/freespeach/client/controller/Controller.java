package org.academiadecodigo.bootcamp8.freespeach.client.controller;

import javafx.fxml.Initializable;
import org.academiadecodigo.bootcamp8.freespeach.client.service.ClientService;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public interface Controller extends Initializable {

    void setClientService(ClientService clientService);

    void init();

}
