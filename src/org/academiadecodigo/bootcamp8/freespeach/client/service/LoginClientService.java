package org.academiadecodigo.bootcamp8.freespeach.client.service;

import javafx.scene.control.TextArea;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeach.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeach.shared.utils.Stream;

import javax.crypto.SealedObject;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class LoginClientService implements ClientService{

    private Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean connectionServer;

    public LoginClientService() {
       /* try {
            clientSocket = new Socket(Values.HOST, Values.SERVER_PORT);

        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //setupStreams();
    }

    public void setupStreams() {
        try {
            output = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));

            //THIS WAS SEPARATED - DOES IT NEED TO?
            input = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("o " + output);
        System.out.println("i " + input);
    }

    public void close() {
        try {
            clientSocket.close();
            connectionServer = false;
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Disconnect from server not succeeded ");
        }
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    /*public ObjectInputStream getInput() throws IOException {
      return input;

    }*/



    /*public void writeObject(Message message){
        try {
            output.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    @Override
    public void sendUserText(TextArea textField) {

    }

    @Override
    public void writeObject(Sendable message) {

    }

    public Message readObject() {
        Object serverMessage = null;
        try {
            serverMessage = input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (Message) serverMessage;
    }

    @Override
    public void closeClientSocket() {

    }*/


    public void makeConnection(String server, int port){
        try {
            //clientSocket = new Socket(Values.HOST, Values.SERVER_PORT);

            InetAddress address = InetAddress.getByName(server);
            System.out.println(address);
            if(!connectionServer)
                clientSocket = new Socket(server, port);
            else{
                System.out.println("Client already connected");
            }

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Connection to server not successful");
            connectionServer = false;
            return;
        }

        System.out.println("Connection to server successful");
        connectionServer = true;
    }


    public boolean getConnectionServer(){
        return connectionServer;
    }

    @Override
    public void sendUserText(TextArea textField) {

        if (textField.getText().isEmpty()) {
            return;
        }

        Message<String> message = new Message<>(MessageType.DATA, textField.getText());
        writeObject(message);
        System.out.println("SENT: " + message);
        textField.clear();
        textField.requestFocus();
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
    public InputStream getInput() throws IOException {
        return clientSocket.getInputStream();
    }

    /**
     * @see ClientService#writeObject(Sendable)
     * @param message
     */
    @Override
    public void writeObject(Sendable message) {
        try {
            //TODO util Stream class
            //output.writeObject(message);
            Stream.writeObject(clientSocket.getOutputStream(), message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message readObject() {
        Object serverMessage = null;
        System.out.println("Dentro do ReadObject");
        try {
            //TODO util Stream class
            //serverMessage = input.readObject();
            System.out.println("Client socket" + clientSocket.toString());
            //SealedObject object = (SealedObject) Stream.readObject(clientSocket.getInputStream());
            serverMessage = Stream.readObject(clientSocket.getInputStream());
            System.out.println("Read response from server" + serverMessage.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("saindo do readobject");
        return (Message) serverMessage;
    }

}