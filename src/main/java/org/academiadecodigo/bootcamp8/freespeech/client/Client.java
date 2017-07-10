package org.academiadecodigo.bootcamp8.freespeech.client;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.academiadecodigo.bootcamp8.freespeech.client.service.RegistryService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.connection.ConnectionService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.connection.FreeSpeechConnectionService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech.ClientService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech.FreeSpeechService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.login.LoginClientService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.login.LoginService;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Navigation;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class Client extends Application {

    public static void main(String[] args) {

        if(args.length > 0) {
            Values.HOST = args[0];
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        addServicesToRegister();
        Navigation.getInstance().setStage(primaryStage);
        defineStyle(primaryStage);
        Navigation.getInstance().loadScreen(Values.CONNECTING_SCENE);
    }

    private void defineStyle(Stage stage) {
        String css = getClass().getResource(Values.STYLESHEET).toString();
        stage.initStyle(StageStyle.UNDECORATED);
        Navigation.getInstance().setCss(css);
    }

    private void addServicesToRegister() {

        ConnectionService connectionService = new FreeSpeechConnectionService();
        LoginService loginService = new LoginClientService();
        ClientService clientService = new FreeSpeechService();

        RegistryService.getInstance().addService(connectionService);
        RegistryService.getInstance().addService(loginService);
        RegistryService.getInstance().addService(clientService);
    }

}