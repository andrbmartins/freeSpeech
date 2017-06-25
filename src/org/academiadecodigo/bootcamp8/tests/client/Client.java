package org.academiadecodigo.bootcamp8.tests.client;

import org.academiadecodigo.bootcamp8.message.Sendable;
import org.academiadecodigo.bootcamp8.tests.utils.Converter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * @author by André Martins <Code Cadet>
 *         Tests (25/06/2017)
 *         <Academia de Código_>
 */
public class Client {

    Socket socket;

    public static void main(String[] args) {

        Client client = new Client();
        System.out.println("Connected to server");
        client.init();

    }

    private Client() {

        try {
            socket = new Socket("localhost", 12345);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void init() {

        BufferedInputStream in = null;

        try {

            in = new BufferedInputStream(socket.getInputStream());

            List<Byte> list = new LinkedList<>();

            byte b;
            while ((b = (byte) in.read()) != -1) {
                list.add(b);
            }

            byte[] bytes = Converter.toPrimitiveByteArray(list);
            Sendable message = Converter.toObject(bytes);

            System.out.println(message);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
