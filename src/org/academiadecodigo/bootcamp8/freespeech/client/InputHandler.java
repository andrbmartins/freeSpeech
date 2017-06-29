package org.academiadecodigo.bootcamp8.freespeech.client;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Prints received messages to a textarea.
     */
    @Override
    public void run() {
        while (!room.isDisabled()) {
            String text = ((Message<String>) Stream.readObject(input)).getContent();

            text = wipeWhiteSpaces(text);
            room.appendText((room.getText().isEmpty() ? "" : "\n") + text);
        }
    }

    /**
     * Removes all whitespaces before and after the specified string.
     *
     * @param text - the specified string.
     * @return the resulting text.
     */
    private String wipeWhiteSpaces(String text) {

        //One or more characters and a colon
        //Every whitespace
        //Every word character, digit, whitespace, punctuation and symbol
        //A single character, punctuation or symbol

        Pattern pattern = Pattern.compile("(.+:)(\\s*)([\\w\\s\\p{P}\\p{S}çÇ]*)([\\w\\p{P}\\p{S}çÇ])");
        Matcher matcher = pattern.matcher(text);

        String result = "";
        while (matcher.find()) {
            result = result.concat(matcher.group(1) + " "); //username and colon
            result = result.concat(matcher.group(3));       //string content
            result = result.concat(matcher.group(4));       //last valid character
        }
        return result;
    }
}
