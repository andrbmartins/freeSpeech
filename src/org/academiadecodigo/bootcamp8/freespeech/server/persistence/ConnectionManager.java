package org.academiadecodigo.bootcamp8.freespeech.server.persistence;

import org.academiadecodigo.bootcamp8.freespeech.server.utils.User;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

import java.sql.*;

/**
 * Created by codecadet on 28/06/17.
 */
public class ConnectionManager  {

    private Connection connection;

    public ConnectionManager() {


        try {
            //String url = "jdbc:mysql://localhost:3306/freespeech";
            //connection = DriverManager.getConnection(url, "root", "root");
            connection = DriverManager.getConnection(Values.URL_DBSERVER, Values.USER_DBSERVER, Values.PASSWORD_DBSERVER);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(connection.toString());

    }

    public Connection getConnection() {
        return connection;
    }

    public void insertUser(String username, String password, String email) throws SQLException {

        Statement statement = connection.createStatement();

        String query = "INSERT INTO users (user_id, password , email) VALUES ('" + username + "', '" + password + "' , '" + email + "' )";

        // execute the query
         statement.executeUpdate(query);
        statement.close();
    }

    public boolean authenticateUser(String username, String password) throws SQLException {

        Statement statement = connection.createStatement();

        // create a query
        String query = "SELECT * FROM users WHERE users.user_id = '" + username + "' AND users.password = '" + password + "' ";

        // execute the query
        ResultSet resultSet = statement.executeQuery(query);

        if (resultSet.first())
            return true;
        else
            return false;

    }


    public User findUser(String username) throws SQLException {

        User user = null;
        Statement statement = connection.createStatement();

        // create a query
        String query = "SELECT * FROM users WHERE users.user_id = '" + username + "' ";

        // execute the query
        ResultSet resultSet = statement.executeQuery(query);

        if(resultSet.next()) {

            String usernameValue = resultSet.getString("username");
            String passwordValue = resultSet.getString("password");
            String emailValue = resultSet.getString("email");

            //user = new User(usernameValue, passwordValue, emailValue);
        }

        return user ;
    }


    public int count() throws SQLException {

        Statement statement = connection.createStatement();

        // create a query
        String query = "SELECT COUNT(*) FROM users";

        // execute the query
        ResultSet resultSet = statement.executeQuery(query);
        if(resultSet.next())
            return resultSet.getInt(1);
        else
            return 0;
    }

}
