package org.academiadecodigo.bootcamp8.freespeech.server.persistence;

import org.academiadecodigo.bootcamp8.freespeech.server.utils.User;
import org.academiadecodigo.bootcamp8.freespeech.shared.Querys;
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

    public void insertUser(String username, String password) throws SQLException {    // Needs test

        //if(findUserByName(username))
        //    return;
        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = connection.prepareStatement(Querys.INSERT_USER);
            preparedStmt.setString(1,  username);
            preparedStmt.setString(2, password);
            preparedStmt.execute();
            preparedStmt.close();
        } catch (SQLException e) {
            //e.printStackTrace();
            // Send message de erro para os logs
            System.out.println("Erro ao inserir user");

        }
        preparedStmt.close();
    }

    public boolean authenticateUser(String username, String password) throws SQLException {  // Needs test


        System.out.println("Vou autenticar este user  " + username + password);
        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = connection.prepareStatement(Querys.AUTHENTICATE_USER);
            preparedStmt.setString(1, username);
            preparedStmt.setString(2, password);
            ResultSet resultSet = preparedStmt.executeQuery();
            if (!resultSet.next()) {
                preparedStmt.close();
                // Insert in log - Login Failed
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        preparedStmt.close();

        // Insert in log - Login Sucess
        return true;


    }

    public User findUser(String username) throws SQLException {     // Needs test

        User user = null;

        PreparedStatement preparedStmt = connection.prepareStatement(Querys.SELECT_USER);
        preparedStmt.setString(1, username);

        ResultSet resultSet = preparedStmt.executeQuery();

        if(resultSet.next()) {
            String usernameValue = resultSet.getString("user_name");
            String passwordValue = resultSet.getString("user_password");
            user = new User(usernameValue, passwordValue);
        }
        return user ;
    }

   /* public boolean findUserByName(String username) throws SQLException {         // Needs test

        PreparedStatement preparedStmt = connection.prepareStatement(Querys.SELECT_USER);
        preparedStmt.setString(1, username);

        ResultSet resultSet = preparedStmt.executeQuery();

        if (resultSet.next())
            return true;
        else
            return false;

    }*/


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
