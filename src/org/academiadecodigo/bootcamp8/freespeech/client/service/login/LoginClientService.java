package org.academiadecodigo.bootcamp8.freespeech.client.service.login;

import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.SealedSendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Crypto;
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

        Crypto crypto = Session.getInstance().getCryptographer();

        try {

            InetAddress address = InetAddress.getByName(server);
            System.out.println(address);

            if (!connectionServer) {
                clientSocket = new Socket(server, port);
                Session.getInstance().setUserSocket(clientSocket);

                Key foreignKey = (Key) Stream.read(Session.getInstance().getInputStream());
                crypto.setForeignKey(foreignKey);
                Stream.write(Session.getInstance().getOutputStream(), Session.getInstance().getCryptographer().getPublicKey());


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
        Stream.write(Session.getInstance().getOutputStream(), message);
    }

    @Override
    public String getName() {
        return LoginService.class.getSimpleName();
    }

    @Override
    public void writeObject(MessageType messageType, SealedSendable message) {

        Stream.write(Session.getInstance().getOutputStream(), message);
    }

    private Crypto getCrypto() {
        return Session.getInstance().getCryptographer();
    }

    @Override
    public Sendable readObject() {

        SealedSendable sealedSendable = (SealedSendable) Stream.read(Session.getInstance().getInputStream());
        return getCrypto().decrypt(sealedSendable, getCrypto().getSymKey());

        //Object serverMessage = Stream.read(Session.getInstance().getInputStream());

        //return (Message) serverMessage;

    }

}
