package org.academiadecodigo.bootcamp8.freespeach.client.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.academiadecodigo.bootcamp8.freespeach.client.controller.Controller;
import org.academiadecodigo.bootcamp8.freespeach.client.service.ClientService;
import org.academiadecodigo.bootcamp8.freespeach.shared.Values;

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

    //TODO TEST
    public static final String USERNAME = "test-user";

    private ClientService clientService;

    private Navigation() {
    }

    /**
     * Instantiates a singleton instance of this class and returns it.
     *
     * @return the instance.
     */
    public static Navigation getInstance() {

        synchronized (Navigation.class) {
            if (instance == null) {
                instance = new Navigation();
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
            controllers.get(view).setClientService(clientService);
            controllers.get(view).init();

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

    /**
     * Returns the controller corresponding to the specified element.
     *
     * @param view - the element.
     * @return the controller.
     */
    public Controller fetchController(String view) {
        return controllers.get(view);
    }

    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
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