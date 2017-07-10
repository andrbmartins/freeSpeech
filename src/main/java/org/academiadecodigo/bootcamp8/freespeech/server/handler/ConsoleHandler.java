package org.academiadecodigo.bootcamp8.freespeech.server.handler;

import org.academiadecodigo.bootcamp8.freespeech.server.Server;

import java.util.Scanner;

/**
 * @author by André Martins <Code Cadet>
 *         Project freeSpeech (03/07/17)
 *         <Academia de Código_>
 */
public class ConsoleHandler implements Runnable {

    private Server server;
    private Scanner in;

    public ConsoleHandler(Server server) {
        this.server = server;
        in = new Scanner(System.in);
    }

    @Override
    public void run() {

        String cmd;
        // While cmd is different than "stop"
        while (!(cmd = readCommand()).equals(Command.STOP.getString())) {
            processCommand(cmd);
        }

        // Stop the server
        in.close();
        server.stop();

    }

    /**
     * Read the user commands from the terminal
     *
     * @return Returns the input
     */
    private String readCommand() {
        return in.nextLine();
    }

    /**
     * Handles the command given by the user and calls the respective method for that command
     *
     * @param cmd Command given by the user
     */
    private void processCommand(String cmd) {

        switch (Command.getEnum(cmd)) {
            case RUNTIME:
                server.runtime();
                break;
            case USERS:
                server.printLoggedUsers();
                break;
            default:
                System.out.println("Invalid command");
        }

    }

    /**
     * Enum representing the type of commands that a User can give
     */
    private enum Command {

        STOP("stop"),
        RUNTIME("runtime"),
        USERS("users"),
        INVALID("");

        private final String string;

        Command(String string) {
            this.string = string;
        }

        /**
         * Returns the string associated the command
         *
         * @return Returns the string associated the command
         */
        public String getString() {
            return string;
        }

        /**
         * Returns the Enum command associated with the string
         *
         * @param string String associated with the Enum Command
         * @return Returns the string
         */
        public static Command getEnum(String string) {

            for (Command c : Command.values()) {
                if (c.getString().equals(string)) {
                    return c;
                }
            }

            return INVALID;

        }

    }

}
