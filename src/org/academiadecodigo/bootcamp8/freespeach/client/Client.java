package org.academiadecodigo.bootcamp8.freespeach.client;

import javafx.application.Application;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.freespeach.client.service.ClientService;
import org.academiadecodigo.bootcamp8.freespeach.client.service.LoginClientService;
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

        Navigation.getInstance().setStage(primaryStage);

        ClientService clientService = new LoginClientService();
        Navigation.getInstance().setClientService(clientService);

        String css = new File(Values.STYLESHEET).toURI().toString();
        Navigation.getInstance().setCss(css);
        primaryStage.setTitle(Values.TITLE);

        //TODO login
        Navigation.getInstance().loadScreen(Values.LOGIN_SCENE);

    }
}