package org.academiadecodigo.bootcamp8.freespeech.client;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.academiadecodigo.bootcamp8.freespeech.client.service.RegistryService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech.FreeSpeechClientService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.login.LoginClientService;
import org.academiadecodigo.bootcamp8.freespeech.client.service.login.LoginService;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Navigation;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

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

        addServicesToRegister();

        Navigation.getInstance().setStage(primaryStage);
        defineStyle(primaryStage);
        Navigation.getInstance().loadScreen(Values.LOGIN_SCENE);
    }

    private void defineStyle(Stage stage) {

        String css = new File(Values.STYLESHEET).toURI().toString();
        stage.initStyle(StageStyle.UNDECORATED);
        Navigation.getInstance().setCss(css);
        stage.setTitle(Values.TITLE);
    }

    private void addServicesToRegister() {

        LoginService login = new LoginClientService();
        FreeSpeechClientService freeSpeech = new FreeSpeechClientService();

        RegistryService.getInstance().addService(login);
        RegistryService.getInstance().addService(freeSpeech);
    }
}