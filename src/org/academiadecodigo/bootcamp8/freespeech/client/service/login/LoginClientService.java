package org.academiadecodigo.bootcamp8.freespeech.client.service.login;

import org.academiadecodigo.bootcamp8.freespeech.client.utils.SessionContainer;
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

        SessionContainer sessionContainer = SessionContainer.getInstance();

        Sendable<Map> message = new Message<>(messageContent);
        SealedSendable sealed = sessionContainer.getCrypto().encrypt(
                messageType, message, sessionContainer.getCrypto().getForeignKey());
        Stream.write(sessionContainer.getOutput(), sealed);
    }

    /**
     * @return - the massage.
     * @see LoginService#readMessage()
     */
    @Override
    public Sendable<String> readMessage() {

        SessionContainer sessionContainer = SessionContainer.getInstance();

        SealedSendable serverRsp = Stream.readSendable(sessionContainer.getInput());
        return serverRsp.getContent(sessionContainer.getCrypto().getPrivateKey());
    }

    /**
     * @see LoginService#receiveSymKey()
     */
    @Override
    public void receiveSymKey() {

        SessionContainer sessionContainer = SessionContainer.getInstance();

        SealedSendable sealed = Stream.readSendable(sessionContainer.getInput());
        Sendable<Key> key = sealed.getContent(sessionContainer.getCrypto().getPrivateKey());
        sessionContainer.getCrypto().setSymKey(key.getContent());

    }

    /**
     * @see LoginService#exit()
     */
    @Override
    public void exit() {

        SessionContainer sessionContainer = SessionContainer.getInstance();

        Sendable<String> message = new Message<>("");
        SealedSendable sealed = sessionContainer.getCrypto().encrypt(
                MessageType.EXIT, message, sessionContainer.getCrypto().getForeignKey());
        Stream.write(sessionContainer.getOutput(), sealed);
    }
}
