package org.academiadecodigo.bootcamp8.freespeech.server.utils;

import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves / Fábio Fernandes
 */
public interface UserService {

    boolean authenticate(String username, String pass);

    void addUser(User user);

    void removeUser(String username);

    User getUser(String username);

    int count();

    void eventlogger(Values.TypeEvent typeEvent, String log_message);
}
