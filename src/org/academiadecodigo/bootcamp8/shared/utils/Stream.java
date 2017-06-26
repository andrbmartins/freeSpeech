package org.academiadecodigo.bootcamp8.shared.utils;

import javax.crypto.Cipher;
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

        BufferedOutputStream bout = null;
        CipherOutputStream cout = null;
        Crypto crypto = new Crypto(Cipher.ENCRYPT_MODE);

        try {

            bout = new BufferedOutputStream(out);
            bout.write(Converter.toBytes(crypto.getSecretKey()));
            bout.flush();

            cout = new CipherOutputStream(out, crypto.getCipher());
            cout.write(Converter.toBytes(message));
            cout.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Stream.close(bout);
            Stream.close(cout);
        }

    }

    public static Object readObject(InputStream in) {

        final int EOS = -1;

        Object object = null;

        try {

            List<Byte> list = new ArrayList<>();

            byte b;
            while ((b = (byte) in.read()) != EOS) {
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
