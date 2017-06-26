package org.academiadecodigo.bootcamp8.freespeach.tests.client;

import org.academiadecodigo.bootcamp8.freespeach.shared.utils.Crypto;
import org.academiadecodigo.bootcamp8.freespeach.shared.utils.Stream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;

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

        BufferedInputStream bin = null;
        CipherInputStream cin = null;

        try {

            bin = new BufferedInputStream(socket.getInputStream());
            SecretKey key = (SecretKey) Stream.readObject(bin);
            System.out.println(key);

            Crypto crypto = new Crypto(Cipher.DECRYPT_MODE, key);
            cin = new CipherInputStream(socket.getInputStream(), crypto.getCipher());
            System.out.println(Stream.readObject(cin));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Stream.close(bin);
            Stream.close(cin);
        }

    }

}
