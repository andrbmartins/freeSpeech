package org.academiadecodigo.bootcamp8.freespeech.client.service;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;


/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

//TODO documentation

public interface Service {

    String getName();

    Message readObject();

    void sendUserText(TextArea textArea);

    void writeObject(Sendable message);

}
