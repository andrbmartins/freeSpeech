package org.academiadecodigo.bootcamp8.client;

import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.client.controller.Controller;
import org.academiadecodigo.bootcamp8.message.Message;

import javax.xml.soap.Text;
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
