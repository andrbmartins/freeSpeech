package org.academiadecodigo.bootcamp8.freespeech.shared.utils;

import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by codecadet on 04/07/17.
 */
public class Parser {

    public static byte[] byteListToArray(List<Byte> byteList) {

        byte[] bytes = new byte[byteList.size()];

        for (int i = 0; i < bytes.length; i++){
            bytes[i] = byteList.get(i);
        }

        return bytes;
    }

    public static Set<String> stringToSet(String destinyString) {

        HashSet<String> set = new HashSet<>();

        for(String s : destinyString.split(Values.SEPARATOR_CHARACTER)){
            set.add(s);
        }

        return set;
    }

    public static byte[] listToByteArray(List<Byte> byteList) {
        byte[] bytes = new byte[byteList.size()];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = byteList.get(i);
        }

        return bytes;
    }

}
