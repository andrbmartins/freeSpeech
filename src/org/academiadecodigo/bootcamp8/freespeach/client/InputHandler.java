package org.academiadecodigo.bootcamp8.freespeach.client;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeach.shared.utils.Stream;

import java.io.InputStream;

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
        while (!room.isDisabled()) {
            String text = ((Message<String>) Stream.readObject(input)).getContent();
            room.appendText((room.getText().isEmpty() ? "" : "\n") + text);
        }
    }
}
