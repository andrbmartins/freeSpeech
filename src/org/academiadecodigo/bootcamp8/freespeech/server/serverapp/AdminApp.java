package org.academiadecodigo.bootcamp8.freespeech.server.serverapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.academiadecodigo.bootcamp8.freespeech.server.serverapp.controller.AdminController;
import org.academiadecodigo.bootcamp8.freespeech.server.serverapp.service.DataBaseReader;
import org.academiadecodigo.bootcamp8.freespeech.server.serverapp.service.JdbcConnectionManager;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */
public class AdminApp extends Application {
    private JdbcConnectionManager connectionManager;
    private final static String ADMIN_VIEW = "view/admin_app.fxml";



    public static void main(String[] args) {
        launch(args);
    }



    @Override
    public void start(Stage primaryStage) throws Exception {
        connectionManager = new JdbcConnectionManager();
        DataBaseReader reader = new DataBaseReader(connectionManager);


        FXMLLoader loader = new FXMLLoader(getClass().getResource(ADMIN_VIEW));
        AdminController controller = loader.getController();
        controller.setReader(reader);


        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();


    }


    @Override
    public void stop() throws Exception {
        connectionManager.close();
        super.stop();
    }
}