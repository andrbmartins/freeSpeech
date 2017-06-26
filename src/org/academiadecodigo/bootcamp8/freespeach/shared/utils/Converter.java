package org.academiadecodigo.bootcamp8.freespeach.shared.utils;

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
        ObjectOutputStream bout = null;
        byte[] bytes = null;

        try {

            bout = new ObjectOutputStream(bos);
            bout.writeObject(message);
            bout.flush();
            bytes = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Stream.close(bos);
            Stream.close(bout);
        }

        return bytes;

    }

    public static Object toObject(byte[] bytes) {

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream bins = null;
        Object object = null;

        try {

            bins = new ObjectInputStream(bais);
            object = bins.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Stream.close(bais);
            Stream.close(bins);
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
