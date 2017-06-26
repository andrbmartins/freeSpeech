package org.academiadecodigo.bootcamp8.freespeach.server.communication;

import org.academiadecodigo.bootcamp8.freespeach.shared.message.Sendable;

import java.io.*;
import java.net.Socket;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */
public class CommunicationService implements Communication {
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;


    @Override
    public void openInputChannel(Socket socket) {
        try {
            objectInputStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void openOutputChannel(Socket socket) {
        try {
            objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(Sendable message) {

    }

    @Override
    public Sendable retrieveMessage() {
        return null;
    }
}




    private void buildBufferStreams() throws IOException {


    }

    public void write(String username, String msg) {
        try {
            objectOutputStream.writeBytes(username + " wrote: " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}