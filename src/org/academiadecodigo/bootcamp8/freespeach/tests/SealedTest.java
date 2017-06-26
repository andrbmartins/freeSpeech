package org.academiadecodigo.bootcamp8.freespeach.tests;

import org.academiadecodigo.bootcamp8.freespeach.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeach.shared.utils.Crypto;
import javax.crypto.SealedObject;

/**
 * @author by André Martins <Code Cadet>
 *         freeSpeach (26/06/2017)
 *         <Academia de Código_>
 */
public class SealedTest {

    public static void main(String[] args) {

        Crypto crypto = Crypto.getInstance();

        // the object so we will encrypt
        Message<String> so = new Message<>(Message.Type.DATA, "Hello serial");
        System.out.println(so);

        // encryptedObject this encrypted object send this object to some one or through network
        // no one can get the real object if they do not know the  key
        SealedObject encryptedObject = crypto.encryptObject(so);
        System.out.println(encryptedObject);

        // use encryptedObject and get the real object that is nothing but decryption
        Object etcObject = crypto.decryptObject(encryptedObject);
        System.out.println(etcObject);

    }

}
