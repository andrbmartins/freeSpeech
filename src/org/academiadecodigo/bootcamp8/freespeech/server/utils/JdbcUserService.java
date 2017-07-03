package org.academiadecodigo.bootcamp8.freespeech.server.utils;

import org.academiadecodigo.bootcamp8.freespeech.server.persistence.ConnectionManager;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

import java.sql.SQLException;


/**
 * Created by Jramos on 29-06-2017.
 */
public class JdbcUserService implements UserService {

    //ConnectionManager connectionManager;

    private static JdbcUserService instance = new JdbcUserService();
    private ConnectionManager connectionManager;


    public JdbcUserService() {
        this.connectionManager = new ConnectionManager();
    }


   @Override
    public boolean authenticate(String username, String password) {

        System.out.println("Inside JDBC service");
        try {
            if (connectionManager.authenticateUser(username,password))
                return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean addUser(User user) {
        return connectionManager.insertUser(user.getUsername(),user.getPassword());
    }

    @Override
    public void removeUser(String username) {

    }

    @Override
    public User getUser(String username) {
        System.out.println(username);
        return findByname(username);
    }


    public User findByname(String username) {
        User user= null;
        System.out.println(username);
        try {
            user = connectionManager.findUser(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public int count() {

        int recordcount = 0;
        try {
            recordcount = connectionManager.count();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recordcount;


    }

    @Override
    public void eventlogger(Values.TypeEvent typeEvent, String log_message ) {
        connectionManager.eventlogger(typeEvent, log_message);
    }


    /// public String getname(){
   //     return UserService.class.getSimpleName();
   // }

   public static JdbcUserService getInstance(){ return instance; }
}
