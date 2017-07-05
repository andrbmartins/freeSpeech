package org.academiadecodigo.bootcamp8.freespeech.client.service.login;

import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.*;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.security.Key;
import java.util.Map;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> JPM Ramos
 * <Code Cadet> PedroMAlves
 */

public class LoginClientService implements LoginService {

    @Override
    public String getName() {
        return LoginService.class.getSimpleName();
    }

    /**
     * @param messageType    - the type.
     * @param messageContent - the content.
     * @see LoginService#sendMessage(MessageType, Map)
     */
    @Override
    public void sendMessage(MessageType messageType, Map<String, String> messageContent) {

        Message<Map> message = new Message<>(messageContent);
        SealedSendable sealed;
        sealed = Session.getCrypto().encrypt(messageType, message, Session.getCrypto().getForeignKey());
        Stream.write(Session.getOutput(), sealed);
    }

    /**
     * @return - the massage.
     * @see LoginService#readMessage()
     */
    @Override
    public Sendable<String> readMessage() {

        SealedSendable serverRsp = Stream.readSendable(Session.getInput());
        return serverRsp.getContent(Session.getCrypto().getPrivateKey());
    }

    /**
     * @see LoginService#receiveSymKey()
     */
    @Override
    public void receiveSymKey() {

        SealedSendable sealed = Stream.readSendable(Session.getInput());
        Sendable<Key> key = sealed.getContent(Session.getCrypto().getPrivateKey());
        Session.getCrypto().setSymKey(key.getContent());

    }

    /**
     * @see LoginService#exit()
     */
    @Override
    public void exit() {

        Message<String> message = new Message<>("");
        SealedSendable sealed;
        sealed = Session.getCrypto().encrypt(MessageType.EXIT, message, Session.getCrypto().getForeignKey());
        Stream.write(Session.getOutput(), sealed);
    }
}
