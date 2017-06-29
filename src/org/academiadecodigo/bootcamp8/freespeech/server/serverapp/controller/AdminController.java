package org.academiadecodigo.bootcamp8.freespeech.server.serverapp.controller;

import javafx.fxml.Initializable;
import org.academiadecodigo.bootcamp8.freespeech.server.serverapp.service.DataBaseReader;
import org.academiadecodigo.bootcamp8.freespeech.server.serverapp.service.JdbcConnectionManager;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */
public class AdminController implements Initializable {
    private DataBaseReader reader;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void doQuery() {
        //TODO construct query - execute query - close statement
    }

    public void setReader(DataBaseReader reader) {
        this.reader = reader;
    }
}
