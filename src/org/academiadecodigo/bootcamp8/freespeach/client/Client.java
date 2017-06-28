package org.academiadecodigo.bootcamp8.freespeach.client;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.academiadecodigo.bootcamp8.freespeach.client.service.ClientService;
import org.academiadecodigo.bootcamp8.freespeach.client.service.HashService;
import org.academiadecodigo.bootcamp8.freespeach.client.service.TempClientService;
import org.academiadecodigo.bootcamp8.freespeach.client.utils.Navigation;
import org.academiadecodigo.bootcamp8.freespeach.shared.Values;

import java.io.File;


/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class Client extends Application {

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //TODO TEST
        System.out.println(HashService.getHash("OLÁ SOU A CAROLINA"));
        System.out.println(HashService.getHash("OLÁ SOU A CAROLINA"));


        Navigation.getInstance().setStage(primaryStage);

        ClientService clientService = new TempClientService();
        Navigation.getInstance().setClientService(clientService);

        primaryStage.initStyle(StageStyle.UNDECORATED);
        String css = new File(Values.STYLESHEET).toURI().toString();
        Navigation.getInstance().setCss(css);

        //TODO login
        Navigation.getInstance().loadScreen(Values.USER_SCENE);
    }
}