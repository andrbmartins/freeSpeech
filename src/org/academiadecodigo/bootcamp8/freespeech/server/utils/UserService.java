package org.academiadecodigo.bootcamp8.freespeech.server.utils;

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
}
