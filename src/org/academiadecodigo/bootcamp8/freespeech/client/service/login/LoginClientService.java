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
import java.security.Key;
import java.util.Map;


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

                Key foreignKey = (Key) Stream.readObject(Session.getInstance().getInputStream());
                crypto.setForeignPublicKey(foreignKey);
                Stream.writeObject(Session.getInstance().getOutputStream(), Session.getInstance().getCryptographer().getNativePublicKey());


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
    public void writeObject(MessageType messageType, SealedSendable message) {

        Stream.writeObject(Session.getInstance().getOutputStream(), message);
    }

    private Crypto getCrypto() {
        return Session.getInstance().getCryptographer();
    }

    @Override
    public Sendable readObject() {

        SealedSendable sealedSendable = (SealedSendable) Stream.readObject(Session.getInstance().getInputStream());
        return getCrypto().decryptObject(sealedSendable, getCrypto().getSymmetricKey());

        //Object serverMessage = Stream.readObject(Session.getInstance().getInputStream());

        //return (Message) serverMessage;

    }

}
