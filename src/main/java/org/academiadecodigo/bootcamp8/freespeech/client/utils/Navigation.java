package org.academiadecodigo.bootcamp8.freespeech.client.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.freespeech.client.controller.Controller;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

import java.io.IOException;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class Navigation {

    private static Navigation instance = null;

    private Stage stage;
    private String css;

    private Navigation() {
    }

    /**
     * Singleton instantiation.
     *
     * @return the instance.
     */
    public static Navigation getInstance() {

        if (instance == null) {
            synchronized (Navigation.class) {
                if (instance == null) {
                    instance = new Navigation();
                }
            }
        }
        return instance;
    }

    /**
     * Loads the interface corresponding to the specified element.
     *
     * @param view - the view to load.
     */
    public void loadScreen(String view) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(Values.VIEW_PATH + view + ".fxml"));
            Parent root = loader.load();

            ((Controller) loader.getController()).setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(css);
            setScene(scene);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void close() {
        stage.close();
    }

    private void setScene(Scene scene) {
        stage.setScene(scene);
        stage.show();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setCss(String css) {
        this.css = css;
    }

}