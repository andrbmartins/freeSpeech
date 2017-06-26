package org.academiadecodigo.bootcamp8.freespeach.shared.utils;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by André Martins <Code Cadet>
 *         Project freeSpeach (26/06/17)
 *         <Academia de Código_>
 */
public class Stream {

    public static void writeObject(OutputStream out, Object message) {

        try {

           ObjectOutputStream bout = new ObjectOutputStream(out);
            bout.writeObject(message);
            bout.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Object readObject(InputStream in) {
        Object object = null;

        try {

            ObjectInputStream bin = new ObjectInputStream(in);
            List<Byte> list = new ArrayList<>();

            object = bin.readObject();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client abruplty disconnected");
        }
        return object;

    }



    public static void close(Closeable stream) {

        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
