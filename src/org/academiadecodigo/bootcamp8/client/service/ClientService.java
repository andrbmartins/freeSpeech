package org.academiadecodigo.bootcamp8.client.service;

import org.academiadecodigo.bootcamp8.client.utils.Values;
import org.academiadecodigo.bootcamp8.message.Message;

import java.io.*;
import java.net.Socket;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public class ClientService {

    private Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean connectionServer;

    public ClientService() {
       /* try {
            clientSocket = new Socket(Values.HOST, Values.SERVER_PORT);

        } catch (IOException e) {
            e.printStackTrace();
        }
        setupStreams();*/
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

    public ObjectInputStream getInput() {
        return input;
    }

    public void writeObject(Message message){
        try {
            output.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();

        }

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


    public void makeConnection(String server, int port){
        try {
            //clientSocket = new Socket(Values.HOST, Values.SERVER_PORT);
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
        //setupStreams();
        System.out.println("Connection to server successful");
        connectionServer = true;
    }


    public boolean getConnectionServer(){
        return connectionServer;
    }
}