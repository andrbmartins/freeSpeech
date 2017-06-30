package org.academiadecodigo.bootcamp8.freespeech.client.service;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;

import java.util.Map;


/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

//TODO documentation

public interface Service {

    String getName();

    void writeObject(MessageType messageType, Sendable message);
}
