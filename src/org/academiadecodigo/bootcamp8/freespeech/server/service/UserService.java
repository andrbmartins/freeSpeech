package org.academiadecodigo.bootcamp8.freespeech.server.service;

import org.academiadecodigo.bootcamp8.freespeech.server.model.ConnectionManager;
import org.academiadecodigo.bootcamp8.freespeech.server.model.User;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.logger.TypeEvent;

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

    //void eventlogger(TypeEvent typeEvent, String log_message);

    boolean changePassword(String user, String oldPass, String newPass);

    List<String> getUserBio(String username);

    boolean deleteAccount(String clientName, String pass);

    boolean updateBio(List<String> updatedBio);
}
