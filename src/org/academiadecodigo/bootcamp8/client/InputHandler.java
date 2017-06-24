package org.academiadecodigo.bootcamp8.client;

import org.academiadecodigo.bootcamp8.client.controller.Controller;

import java.io.ObjectInputStream;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class InputHandler implements Runnable {

    private ObjectInputStream input;
    private Controller controller;

    public InputHandler(ObjectInputStream input, Controller controller) {
        this.input = input;
        this.controller = controller;
    }

    @Override
    public void run() {
        //TODO - get textField stuffs
    }
}
