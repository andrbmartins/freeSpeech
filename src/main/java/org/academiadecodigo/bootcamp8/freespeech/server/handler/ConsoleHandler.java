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

    private String readCommand() {
        System.out.print("freespeech$ ");
        return in.nextLine();
    }

    private void processCommand(String cmd) {

        switch (Command.getEnum(cmd)) {
            case RUNTIME:
                System.out.println(server.runtime());
                break;
            default:
                System.out.println("Invalid command");
        }

    }

    private enum Command {

        STOP("stop"),
        RUNTIME("runtime"),
        INVALID("");

        private final String string;

        Command(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }

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
