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
        System.out.println("entering read method of Stream Class");
        Object object = null;

        try {

            ObjectInputStream bin = new ObjectInputStream(in);
            System.out.println("just created buffered");
            List<Byte> list = new ArrayList<>();

            //byte b;
            /*while ((b = (byte) bin.read()) != -1) {

                list.add(b);
            }*/

            //object = Converter.toObject(Converter.toPrimitiveByteArray(list));
            object = bin.readObject();

            System.out.println(object);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("before returning read object");
        return object;

    }

    public static void writeObject(OutputStream out, Cipher cipher, Object message) {

        try {

            CipherOutputStream cout = new CipherOutputStream(out, cipher);
            cout.write(Converter.toBytes(message));
            cout.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object readObject(InputStream in, Cipher cipher) {

        Object object = null;

        try {

            CipherInputStream cin = new CipherInputStream(in, cipher);
            List<Byte> list = new ArrayList<>();

            byte b;
            while ((b = (byte) cin.read()) != -1) {
                list.add(b);
            }

            object = Converter.toObject(Converter.toPrimitiveByteArray(list));

        } catch (IOException e) {
            e.printStackTrace();
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
