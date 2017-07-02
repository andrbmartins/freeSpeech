package org.academiadecodigo.bootcamp8.freespeech.client.service.login;

import org.academiadecodigo.bootcamp8.freespeech.client.controller.LoginController;
import org.academiadecodigo.bootcamp8.freespeech.client.utils.Session;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.*;
import org.academiadecodigo.bootcamp8.freespeech.shared.utils.Stream;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
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

    @Override
    public void sendMessage(MessageType messageType, Map<String, String> messageContent) {
        Message<Map> message = new Message<>(messageContent);
        //TODO this on service
        SealedSendable sealed = Session.getCrypto().encrypt(messageType,
                message, Session.getCrypto().getForeignKey());

        Stream.write(Session.getOutput(), sealed);
    }

    @Override
    public Sendable<String> readMessage() {
        SealedSendable serverRsp = Stream.readSendable(Session.getInput());

        return (Sendable<String>) Session.getCrypto().decryptWithPrivate(serverRsp);
    }

    @Override
    public void receiveSymKey() {
        SealedSendable s = Stream.readSendable(Session.getInput());

        Sendable<Key> key = (Sendable<Key>) Session.getCrypto().decryptWithPrivate(s);
        Session.getCrypto().setSymKey(key.<Key>getContent(Key.class));
    }

    @Override
    public void exit() {
        Message<String> message = new Message<>("");
        SealedSendable sealed = Session.getCrypto().encrypt(MessageType.EXIT,
                message, Session.getCrypto().getForeignKey());

        Stream.write(Session.getOutput(), sealed);
    }
}
