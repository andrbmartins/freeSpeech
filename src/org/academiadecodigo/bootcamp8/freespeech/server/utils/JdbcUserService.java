package org.academiadecodigo.bootcamp8.freespeech.server.utils;

import org.academiadecodigo.bootcamp8.freespeech.server.persistence.ConnectionManager;

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
    public void addUser(User user) {
        try {
            connectionManager.insertUser(user.getUsername(),user.getPassword(), user.getEmail());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeUser(String username) {

    }

    @Override
    public User getUser(String username) {
        return null;
    }


    public User findByname(String username) {
        User user= null;

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

   /// public String getname(){
   //     return UserService.class.getSimpleName();
   // }
   public static JdbcUserService getInstance(){ return instance; }
}
