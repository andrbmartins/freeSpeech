package org.academiadecodigo.bootcamp8.freespeech.client.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.freespeech.client.controller.Controller;
import org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech.ClientService;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class Navigation {

    private static Navigation instance = null;

    private Stage stage;
    private LinkedList<Scene> scenes = new LinkedList<>();
    private Map<String, Controller> controllers = new HashMap<>();
    private String css;

    private Navigation() {
    }

    /**
     * Instantiates a singleton instance of this class and returns it.
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

            FXMLLoader loader = new FXMLLoader(getClass().getResource(Values.VIEW + "/" + view + ".fxml"));
            Parent root = loader.load();

            controllers.put(view, loader.getController());
            controllers.get(view).setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(css);
            scenes.push(scene);
            setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the scene on stage to the previous one.
     */
    public void back() {
        if (scenes.size() < 2) {
            return;
        }
        scenes.pop();
        setScene(scenes.peek());
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