package org.academiadecodigo.bootcamp8.freespeech.tests;

import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Converter;

/**
 * @author by André Martins <Code Cadet>
 *         Project freeSpeech (30/06/17)
 *         <Academia de Código_>
 */
public class SafeCastTest {

    public static void main(String[] args) {

        Object object = new String("Hello");

        String str = Converter.safeCast(String.class, object);

        System.out.println(str.length());

    }

}
