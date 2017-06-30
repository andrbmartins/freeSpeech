package org.academiadecodigo.bootcamp8.freespeech.shared.utils;

/**
 * @author by André Martins <Code Cadet>
 *         Project freeSpeech (30/06/17)
 *         <Academia de Código_>
 */
public class Converter {

    public static <T> T safeCast(Class<T> type, Object object) {

        return object == null ? null : type.cast(object);

    }

}
