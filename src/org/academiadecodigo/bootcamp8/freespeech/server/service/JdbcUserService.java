package org.academiadecodigo.bootcamp8.freespeech.server.service;

import org.academiadecodigo.bootcamp8.freespeech.server.model.ConnectionManager;
import org.academiadecodigo.bootcamp8.freespeech.server.model.User;
import org.academiadecodigo.bootcamp8.freespeech.server.model.logger.TypeEvent;

import java.sql.SQLException;
import java.util.List;


/**
 * Created by Jramos on 29-06-2017.
 */
public class JdbcUserService implements UserService {
    private ConnectionManager connectionManager;

    public JdbcUserService() {
        //TODO this is the problem !
        System.out.println("new service");
        this.connectionManager = new ConnectionManager();
    }


   @Override
    public boolean authenticate(String username, String password) {
       User user = getUser(username);
       return user != null && user.getPassword().equals(password);
   }


    @Override
    public boolean addUser(User user) {
        return connectionManager.insertUser(user.getUsername(),user.getPassword());
    }

    @Override
    public boolean deleteAccount(String username, String password) {
        return (authenticate(username, password)) && connectionManager.deleteAccount(username);
    }


    @Override
    public User getUser(String username) {
        User user= null;
        try {
            user = connectionManager.findUser(username);
        } catch (SQLException e) {
            //TODO log  this stacktrace since it is sql exception
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public boolean changePassword(String username, String oldPass, String newPass) {
        if (authenticate(username, oldPass)) {
            return connectionManager.changePass(username, newPass);
        }
        return false;
    }

    @Override
    public int count() {

        int recordCount = 0;
        try {
            recordCount = connectionManager.count();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recordCount;
    }

    @Override
    public void eventlogger(TypeEvent typeEvent, String log_message ) {
        connectionManager.eventlogger(typeEvent, log_message);
    }

    @Override
    public List<String> getUserBio(String username)  {

        List<String> message = null;
        try {
            message = connectionManager.getUserBio(username);
            
        } catch (SQLException e) {
            //TODO eventlogger here
            e.printStackTrace();
        }
        return message;
    }

}
