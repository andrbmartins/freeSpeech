package org.academiadecodigo.bootcamp8.freespeech.server;

import org.academiadecodigo.bootcamp8.freespeech.server.handler.ClientHandler;
import org.academiadecodigo.bootcamp8.freespeech.server.handler.ConsoleHandler;
import org.academiadecodigo.bootcamp8.freespeech.server.service.UserService;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.logger.Logger;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.logger.TypeEvent;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.SealedSendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Crypto;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Parser;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves / Fábio Fernandes
 */

public class Server {

    private int port;
    private ServerSocket socket;
    private Key symKey;
    private CopyOnWriteArrayList<ClientHandler> loggedUsers;
    private UserService userService;


    public Server(int port, UserService userService) {

        this.port = port;
        loggedUsers = new CopyOnWriteArrayList<>();
        this.userService = userService;
    }

    public Server(UserService userService) {

        port = Values.SERVER_PORT;
        loggedUsers = new CopyOnWriteArrayList<>();
        this.userService = userService;
    }

    /**
     * Prepare the server for execution
     * Initializes the global symmetric key
     * Start the input console
     * Instantiate the socket in the specified port
     *
     * @throws IOException if there is a failure with the socket
     */
    public void init() throws IOException {

        generateSymKey();
        startConsole();
        socket = new ServerSocket(port);

    }

    /**
     * Generate the global symmetric key
     */
    private void generateSymKey() {

        Crypto crypto = new Crypto();
        crypto.generateSymKey();
        symKey = crypto.getSymKey();
        System.out.println("Generated symKey, ready to accept client connections");

    }

    /**
     * Initialize the input console for the server
     */
    private void startConsole() {

        Thread thread = new Thread(new ConsoleHandler(this));
        thread.setName("ConsoleHandler");
        thread.start();

    }

    /**
     * It waits for new connects and, for each new connection, creates a thread dedicated to that client
     *
     * @throws IOException on failure when accepting a new client
     */
    public void start() throws IOException {

        Logger.getInstance().eventlogger(TypeEvent.SERVER, Values.SERVER_START);

        ExecutorService cachedPool = Executors.newCachedThreadPool();

        while (true) {

            Socket clientSocket = socket.accept();
            cachedPool.submit(new ClientHandler(this, clientSocket, symKey, userService));
            Logger.getInstance().eventlogger(TypeEvent.CLIENT, Values.CONNECT_CLIENT + "-" + clientSocket);

        }

    }

    /**
     * Add a new log in user to the list of active clientHandlers
     *
     * @param client to add to the list
     */
    public void addUser(ClientHandler client) {
        loggedUsers.add(client);
        updateList();
    }

    /**
     * Removes clientHandles from the list of active clientHandlers
     * This method is called went a connections is closed, intentionally or not
     *
     * @param client to remove
     */
    public void removeUser(ClientHandler client) {
        loggedUsers.remove(client);
        updateList();
    }

    /**
     * Sends updated list of users online to every user online
     */
    private void updateList() {

        Message<List> message = new Message<>(getUsersOnlineList());

        for (ClientHandler c : loggedUsers) {
            System.out.println(c.getClientName());
            c.sendUsersList(message);
        }

    }

    /**
     * Iterates the list os ClientHandlers, calling the write method of each of the clientHandlers.
     * This method is called by a ClientHandler when it receives a new input from the client.
     *
     * @param sendable to write to all
     */
    public void writeToAll(SealedSendable sendable) {

        for (ClientHandler c : loggedUsers) {
            c.write(sendable);
        }
    }

    /**
     * Iterates the list of ClientHandlers, searching for the client that the Sendable msg is destined to.
     * When it finds it, it calls the write method of that client, sending the message.
     * The Sendable has to respect the following structure: The type must be PRIVATE_TEXT or PRIVATE_DATA;
     * and the content must be an HashMap with 2 String: an Values.Destiny_User field and a text field.
     *
     * @param msg to write
     */
    public void write(SealedSendable msg) {

        //TODO check casts
        HashMap<String, String> content;
        content = (HashMap<String, String>) msg.getContent(symKey).getContent(HashMap.class);
        String destinyString = content.get(Values.DESTINY);
        Set<String> destinySet = Parser.stringToSet(destinyString);

        System.out.println("SERVER DESTINY SET: " + destinySet.toString());
        System.out.println("loggedUsers size is " + loggedUsers.size());

        for (ClientHandler c : loggedUsers) {

            System.out.println("checking ig user " + c.getClientName() + "will recieve message");

            if (destinySet.contains(c.getClientName())) {
                System.out.println("user " + c.getClientName() + " WILL recieve a message");
                c.write(msg);
            }
        }

    }

    /**
     * It returns a list of usernames of the active online users at the time.
     *
     * @return the list of online users
     */
    private List<String> getUsersOnlineList() {

        List<String> usersList = new LinkedList<>();

        for (ClientHandler c : loggedUsers) {
            usersList.add(c.getClientName());
        }

        usersList.sort(null);

        return usersList;
    }

    /**
     * Stop the server ...
     */
    public void stop() {

        //TODO gracefull shutdown

        if (socket != null) {
            try {

                socket.close();
                Logger.getInstance().eventlogger(TypeEvent.SERVER, Values.SERVER_STOP);

            } catch (IOException e) {
                Logger.getInstance().eventlogger(TypeEvent.SERVER, e.getMessage());
            }
        }

    }

    /**
     * Get the server uptime, convert it to hh:mm:ss and returns it
     *
     * @return a string describing the up time
     */
    public String runtime() {

        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();

        long millis = runtime.getUptime();
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public void sendFile(SealedSendable msg) {

        HashMap<String, List<Byte>> content;
        content = (HashMap<String, List<Byte>>) msg.getContent(symKey).getContent(HashMap.class);
        String destiny = new String(Parser.byteListToArray(content.get(Values.DESTINY)));

        for (ClientHandler c : loggedUsers) {

            if (c.getClientName().equals(destiny)) {
                c.write(msg);
                break;
            }
        }

    }

}
