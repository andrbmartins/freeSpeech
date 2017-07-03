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

    boolean addUser(User user);

    User getUser(String username);

    int count();

    void eventLogger(Values.TypeEvent typeEvent, String log_message);

    boolean changePassword(String user, String oldPass, String newPass);

    List<String> getUserBio(String username);

    boolean deleteAccount(String clientName, String pass);
}
