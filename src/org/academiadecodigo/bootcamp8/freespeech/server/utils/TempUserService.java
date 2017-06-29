package org.academiadecodigo.bootcamp8.freespeech.server.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves / Fábio Fernandes
 */

public class TempUserService implements UserService {

    private static TempUserService instance = new TempUserService();
    private List<User> registeredUsers;

    public TempUserService() {
        registeredUsers = new LinkedList<>();
    }

    @Override
    public boolean authenticate(String username, String pass) {
        User u = getUser(username);
        return (u != null && u.getPassword().equals(pass));
    }

    @Override
    public void addUser(User user) {

        if (getUser(user.getUsername()) == null) {
           registeredUsers.add(user);
        }

    }

    @Override
    public void removeUser(String username) {
        if (getUser(username) == null) {
            return;
        }
        registeredUsers.remove(getUser(username));

    }

    @Override
    public User getUser(String username) {
        for (User u : registeredUsers) {
            if (username.equals(u.getUsername())) {
                return u;
            }
        }
        return null;
    }


    @Override
    public int count() {
        return registeredUsers.size();
    }


    public static TempUserService getInstance(){

        return instance;
    }

}
