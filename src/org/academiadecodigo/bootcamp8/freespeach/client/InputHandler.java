package org.academiadecodigo.bootcamp8.freespeach.client;

import org.academiadecodigo.bootcamp8.freespeach.client.controller.Controller;
import org.academiadecodigo.bootcamp8.freespeach.message.Message;

import java.io.IOException;
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

        do {
            try {
                Message message = (Message) input.readObject();
                String content = (String) message.getContent();

                //TODO text area etc

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();

            }
        } while (true); //TODO
    }
}
