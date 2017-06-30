package org.academiadecodigo.bootcamp8.freespeech.client.service.login;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeech.client.service.freespeech.ClientService;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.SealedSendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Crypto;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;


/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá, PedroMAlves
 */

public class LoginClientService implements LoginService {

    private Socket clientSocket;
    private boolean connectionServer;


//TODO password in passwordField

    public void makeConnection(String server, int port) {
        try {

            InetAddress address = InetAddress.getByName(server);
            System.out.println(address);

            if (!connectionServer) {
                clientSocket = new Socket(server, port);
                Session.getInstance().setUserSocket(clientSocket);
            } else {
                System.out.println("Client already connected");
            }

        } catch (IOException e) {
            connectionServer = false;
            return;
        }
        connectionServer = true;

    }

    @Override
    public void closeClientSocket() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean getConnectionServer() {
        return connectionServer;
    }


    public void writeObject(Sendable message) {
        Stream.writeObject(Session.getInstance().getOutputStream(), message);
    }

    @Override
    public String getName() {
        return LoginService.class.getSimpleName();
    }

    @Override
    public void writeObject(MessageType messageType, Sendable message) {
        SealedSendable sealedMessage = getCrypto().encryptObject(messageType, message, getCrypto().getSymmetricKey());

        Stream.writeObject(Session.getInstance().getOutputStream(), sealedMessage);
    }

    private Crypto getCrypto() {
        return Session.getInstance().getCryptographer();
    }

    @Override
    public Message readObject() {
        Object serverMessage = Stream.readObject(Session.getInstance().getInputStream());

        return (Message) serverMessage;
    }

}
