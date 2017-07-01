package org.academiadecodigo.bootcamp8.freespeech.server.communication;

import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;
import java.io.*;
import java.net.Socket;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */
public class CommunicationService implements Communication {
    //private OutputStream objectOutputStream;
    //private InputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;


    @Override
    public void openStreams(Socket socket) {
        try {
            //objectOutputStream = socket.getOutputStream();
            //objectInputStream = socket.getInputStream();
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
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
