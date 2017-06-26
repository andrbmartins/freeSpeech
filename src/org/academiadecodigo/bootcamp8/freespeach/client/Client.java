package org.academiadecodigo.bootcamp8.freespeach.client;

import javafx.application.Application;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.freespeach.client.service.LoginClientService;
import org.academiadecodigo.bootcamp8.freespeach.client.service.TempClientService;
import org.academiadecodigo.bootcamp8.freespeach.client.utils.Navigation;
import org.academiadecodigo.bootcamp8.freespeach.shared.Values;

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
        LoginClientService cs = new LoginClientService();

        Navigation.getInstance().setClientService(cs);
        Navigation.getInstance().setStage(primaryStage);
        Navigation.getInstance().loadScreen(Values.LOGIN_SCENE);
    }
}