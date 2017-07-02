package org.academiadecodigo.bootcamp8.freespeech.server.utils;

import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

import java.util.List;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves / Fábio Fernandes
 */
public interface UserService {

    boolean authenticate(String username, String pass);

    void addUser(User user);

    //TODO check if this is needed
    void removeUser(String username);

    User getUser(String username);

    int count();

    void eventlogger(Values.TypeEvent typeEvent, String log_message);

    List<String> getUserBio(String username);
}
