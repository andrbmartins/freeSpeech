package org.academiadecodigo.bootcamp8.tests.utils;

import org.academiadecodigo.bootcamp8.message.Sendable;

import java.io.*;
import java.util.List;

/**
 * @author by André Martins <Code Cadet>
 *         Tests (25/06/2017)
 *         <Academia de Código_>
 */
public final class Converter {

    public static byte[] toBytes(Object message) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        byte[] bytes = null;

        try {

            out = new ObjectOutputStream(bos);
            out.writeObject(message);
            out.flush();
            bytes = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }

        }

        return bytes;

    }

    public static Object toObject(byte[] bytes) {

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        Object object = null;

        try {

            in = new ObjectInputStream(bis);
            object = in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {

            try {
                bis.close();
            } catch (IOException ex) {
                // ignore close exception
            }

        }

        return object;

    }

    public static byte[] toPrimitiveByteArray(List<Byte> list) {

        byte[] bytes = new byte[list.size()];

        for (int i = 0; i < bytes.length; i++) {

            bytes[i] = list.get(i);

        }

        return bytes;

    }

}
