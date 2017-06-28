package org.academiadecodigo.bootcamp8.freespeach.server.communication;

import org.academiadecodigo.bootcamp8.freespeach.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeach.shared.utils.Stream;
import java.io.*;
import java.net.Socket;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */
public class CommunicationService implements Communication {
    private OutputStream objectOutputStream;
    private InputStream objectInputStream;


    @Override
    public void openStreams(Socket socket) {
        try {
            objectOutputStream = socket.getOutputStream();
            objectInputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void sendMessage(Sendable message) {
        Stream.writeObject(objectOutputStream, message);

    }


    @Override
    public Sendable retrieveMessage() {
        return (Sendable) Stream.readObject(objectInputStream);
    }
}
