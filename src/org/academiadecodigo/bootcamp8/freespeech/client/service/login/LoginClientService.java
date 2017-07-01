package org.academiadecodigo.bootcamp8.freespeech.client.service.login;

import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.SealedSendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;


/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá, PedroMAlves
 */

public class LoginClientService implements LoginService {

    private Socket clientSocket;
    private boolean connectionServer;

    public void makeConnection(String server, int port) {

        try {

            InetAddress address = InetAddress.getByName(server);
            System.out.println(address);

            if (!connectionServer) {
                clientSocket = new Socket(server, port);
                Session.getInstance().setUserSocket(clientSocket);

                Key foreignKey = (Key) Stream.read(Session.getInput());
                Session.getCrypto().setForeignKey(foreignKey);
                Stream.write(Session.getOutput(), Session.getCrypto().getPublicKey());


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
        connectionServer = false;
    }

    @Override
    public boolean getConnectionServer() {
        return connectionServer;
    }

    @Override
    public String getName() {
        return LoginService.class.getSimpleName();
    }

    @Override
    public void writeObject(MessageType messageType, SealedSendable message) {

        Stream.write(Session.getOutput(), message);
    }


    @Override
    public Sendable readObject() {

        SealedSendable sealedSendable = Stream.readSendable(Session.getInput());
        return Session.getCrypto().decryptSendable(sealedSendable);
    }

}
