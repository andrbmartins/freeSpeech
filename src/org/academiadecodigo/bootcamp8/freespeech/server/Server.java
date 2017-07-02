package org.academiadecodigo.bootcamp8.freespeech.server;

import org.academiadecodigo.bootcamp8.freespeech.server.utils.UserService;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.MessageType;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.SealedSendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Crypto;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
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
    private ServerSocket serverSocket;
    private Key symKey;
    private UserService userService;
    private ExecutorService cachedPool;
    private CopyOnWriteArrayList<ClientHandler> loggedUsers;



    public Server(int port) {
        this.port = port;
        loggedUsers = new CopyOnWriteArrayList<>();
    }

    public Server() {
        port = Values.SERVER_PORT;
        loggedUsers = new CopyOnWriteArrayList<>();
    }

    /**
     * Initializates de serverSocket, the threadPool and the UserService
     *
     * @throws IOException
     */

    public void init() throws IOException {

        //TODO log server init
        Crypto crypto = new Crypto();
        crypto.generateSymKey();
        symKey = crypto.getSymKey();

        System.out.println("SERVER SYM KEY " + symKey);

        serverSocket = new ServerSocket(port);
        cachedPool = Executors.newCachedThreadPool();

    }

    /**
     * It waits for new connects and, for each new connection, it gives a thread each will be dedicated to that
     * connections
     *
     * @throws IOException
     */

    public void start() throws IOException {
        userService.eventlogger(Values.TypeEvent.SERVER, Values.SERVER_START);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("LOGGED");
            //TODO log new client
            cachedPool.submit(new ClientHandler(this, clientSocket, symKey));
            userService.eventlogger(Values.TypeEvent.CLIENT, Values.CONNECT_CLIENT + "--" + clientSocket.toString());
        }
    }

    /**
     * closes de ServerSocket...
     */
    public void closeServerSocket() {
        if (serverSocket != null) {
            try {
                //TODO log server off
                serverSocket.close();
                userService.eventlogger(Values.TypeEvent.SERVER, Values.SERVER_STOP);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public UserService getUserService() {
        return userService;
    }

    /**
     * add a new log in user to the list of active clientHandlers
     *
     * @param client
     */
    public void addActiveUser(ClientHandler client) {
        loggedUsers.add(client);
    }

    /**
     * removes clientHandles from the list of active clientHandlers.
     * This method is called went a connections is closed, intentionaly or not.
     *
     * @param client
     */
    public void logOutUser(ClientHandler client) {
        loggedUsers.remove(client);
        //TODO kill this thread
    }

    /**
     * Iterates the list os ClientHandlers, calling the write method of each of the clientHandlers.
     * This method is called by a ClientHandler when it receives a new input from the client.
     *
     * @param sendable
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
     * @param msg
     */
    public void write(SealedSendable msg) {

        HashMap<String, String> content;
        //TODO check casts
        content = (HashMap<String, String>) msg.getContent(symKey).getContent(HashMap.class);
        String destiny = content.get(Values.DESTINY);

        for (ClientHandler c : loggedUsers) {
            if (c.getName().equals(destiny)) {
                c.write(msg);
                break;
            }
        }
    }

    /**
     * It returns a list of usernames of the active online users at the time.
     *
     * @return
     */
    public List<String> getUsersOnlineList() {

        //TODO think of a better idea
        List<String> usersList = new LinkedList<>();

        for (ClientHandler c : loggedUsers) {
            usersList.add(c.getName());
        }

        return usersList;
    }



    public void setUserService(UserService userService) {
        this.userService = userService;
    }

}
