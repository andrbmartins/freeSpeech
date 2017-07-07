package org.academiadecodigo.bootcamp8.freespeech.shared.utils;

import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Parser {

    public static byte[] byteListToArray(List<Byte> byteList) {

        byte[] bytes = new byte[byteList.size()];

        for (int i = 0; i < bytes.length; i++){
            bytes[i] = byteList.get(i);
        }

        return bytes;
    }


    public static String setToString(Set<String> destinySet) {

        StringBuilder stringBuilder = new StringBuilder();

        for (String s : destinySet){
            stringBuilder.append(s);
            stringBuilder.append(Values.SEPARATOR_CHARACTER);
        }

        return stringBuilder.toString();
    }

    public static Set<String> stringToSet(String destinyString) {

        HashSet<String> set = new HashSet<>();

        Collections.addAll(set, destinyString.split(Values.SEPARATOR_CHARACTER));

        return set;
    }

    public static byte[] listToByteArray(List<Byte> byteList) {
        byte[] bytes = new byte[byteList.size()];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = byteList.get(i);
        }

        return bytes;
    }

    public static void byteListToFile(byte[] byteArray, File file) {

        try {

            FileOutputStream stream = new FileOutputStream(file);
            stream.write(byteArray);
            stream.flush();
            stream.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    /**
     * Converts a byte array into a byte list.
     *
     * @param buffer - the byte array.
     * @return the byte list.
     */
    public static List<Byte> byteArrayToList(byte[] buffer) {

        List<Byte> byteList = new ArrayList<>();

        for (byte b : buffer) {
            byteList.add(b);
        }
        return byteList;
    }

    /**
     * Converts a file into a byte array.
     *
     * @param file - the file.
     * @return the byte array.
     */
    public static byte[] fileToByteArray(File file) {

        byte[] buffer = null;
        FileInputStream fileInputStream = null;

        try {

            fileInputStream = new FileInputStream(file);
            buffer = new byte[(int) file.length()];
            fileInputStream.read(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Stream.close(fileInputStream);
        }

        return buffer;
    }

}