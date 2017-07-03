package org.academiadecodigo.bootcamp8.freespeech.server.handler;

import org.academiadecodigo.bootcamp8.freespeech.server.Server;

import java.util.Scanner;

/**
 * @author by André Martins <Code Cadet>
 *         Project freeSpeech (03/07/17)
 *         <Academia de Código_>
 */
public class ConsoleHandler implements Runnable {

    private static final String STOP = "stop";

    private Server server;
    private Scanner in;

    public ConsoleHandler(Server server) {
        this.server = server;
        in = new Scanner(System.in);
    }

    @Override
    public void run() {

        String cmd;
        // While cmd if different than "stop"
        while (!(cmd = readCommand()).equals(STOP)) {
            processCommand(cmd);
        }

        // Stop the server
        in.close();
        server.stop();

    }

    private String readCommand() {
        System.out.print("freespeech$ ");
        return in.nextLine();
    }

    private void processCommand(String cmd) {

        switch (cmd) {
            case "runtime":
                System.out.println("Server uptime " + server.runtime());
                break;
            default:
                System.out.println("Invalid command");
        }

    }

}
