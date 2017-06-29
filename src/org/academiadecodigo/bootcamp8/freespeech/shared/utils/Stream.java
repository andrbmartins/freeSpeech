package org.academiadecodigo.bootcamp8.freespeech.shared.utils;

import java.io.*;

/**
 * @author by André Martins <Code Cadet>
 *         Project freeSpeach (26/06/17)
 *         <Academia de Código_>
 */
public final class Stream {

    /**
     * Write an object to stream, given an object stream
     * @param out destination object stream
     * @param message object to send
     */
    public static void writeObject(ObjectOutputStream out, Object message) {

        try {

            out.writeObject(message);
            out.flush();

        } catch (IOException e) {
            System.err.println("Error on trying to open stream.\n" + e.getMessage());
        }

    }

    /**
     * Write an object to stream
     * @param out destination stream
     * @param message object to send
     */
    public static void writeObject(OutputStream out, Object message) {

        try {

            ObjectOutputStream bout = new ObjectOutputStream(out);
            writeObject(bout, message);

        } catch (IOException e) {
            System.err.println("Error on trying to open stream.\n" + e.getMessage());
        }

    }

    /**
     * Read an object from the stream, given an object stream
     * @param in source object stream
     * @return the received object
     */
    public static Object readObject(ObjectInputStream in) {

        Object object = null;

        try {

            object = in.readObject();

        } catch (IOException e) {
            System.err.println("Error on trying to open object stream.\n" + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found.\n" + e.getMessage());
        }

        return object;

    }

    /**
     * Read an object from the stream
     * @param in source stream
     * @return the received object
     */
    public static Object readObject(InputStream in) {

        Object object = null;

        try {

            ObjectInputStream bin = new ObjectInputStream(in);
            object = readObject(bin);

        } catch (IOException e) {
            System.err.println("Error on trying to open object stream.\n" + e.getMessage());
        }

        return object;

    }

    /**
     * Close the stream
     * @param stream to close
     */
    public static void close(Closeable stream) {

        try {

            if (stream != null) {
                stream.close();
            }

        } catch (IOException e) {
            System.err.println("Error on trying to close stream.\n" + e.getMessage());
        }

    }

}
