package org.academiadecodigo.bootcamp8.freespeach.client;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeach.client.controller.ClientController;
import org.academiadecodigo.bootcamp8.freespeach.client.controller.Controller;

import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class InputHandler implements Runnable {

    private InputStream input;
    private TextArea room;

    public InputHandler(InputStream input, TextArea room) {
        this.input = input;
        this.room = room;
    }

    @Override
    public void run() {
        //TODO - get textField stuffs
    }
}
