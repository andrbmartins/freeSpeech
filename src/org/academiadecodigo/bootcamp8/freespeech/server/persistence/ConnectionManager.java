package org.academiadecodigo.bootcamp8.freespeech.server.persistence;

import org.academiadecodigo.bootcamp8.freespeech.server.utils.User;
import org.academiadecodigo.bootcamp8.freespeech.shared.Querys;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

import java.sql.*;
import java.util.*;

/**
 * Created by codecadet on 28/06/17.
 */
public class ConnectionManager  {

    private Connection connection;

    public ConnectionManager() {
        try {
            connection = DriverManager.getConnection(Values.URL_DBSERVER, Values.USER_DBSERVER, Values.PASSWORD_DBSERVER);
            eventlogger(Values.TypeEvent.DATABASE, Values.SERVER_DBCONNECT);
            } catch (SQLException e) {
            eventlogger(Values.TypeEvent.DATABASE, Values.SERVER_DBDISCONNECT);
            System.out.println(Values.SERVER_DBDISCONNECT);
            //e.printStackTrace();
        }
        System.out.println(connection.toString());

    }

    public void insertUser(String username, String password) throws SQLException {    // TESTED OK
        PreparedStatement preparedStmt = null;
        try {
            System.out.println(password.length());
            preparedStmt = connection.prepareStatement(Querys.INSERT_USER);
            preparedStmt.setString(1,  username);
            preparedStmt.setString(2, password);
            preparedStmt.execute();
            eventlogger(Values.TypeEvent.CLIENT, Values.CLIENT_REGISTED + "--" + username );
        } catch (SQLException e) {
            eventlogger(Values.TypeEvent.CLIENT, Values.CLIENT_REGISTER_FAILED + " -- " + username );
            System.out.println(Values.CLIENT_REGISTER_FAILED);
        }
        preparedStmt.close();
    }

    public boolean authenticateUser(String username, String password) throws SQLException {  // TESTED OK

        //System.out.println("Vou autenticar este user  " + username + password);
        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = connection.prepareStatement(Querys.AUTHENTICATE_USER);
            preparedStmt.setString(1, username);
            preparedStmt.setString(2, password);
            ResultSet resultSet = preparedStmt.executeQuery();
            if (!resultSet.next()) {
                preparedStmt.close();
                // Insert in log - Login Failed
                eventlogger(Values.TypeEvent.LOGIN, Values.CLIENT_LOGINFAILED + "--" + username );
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        preparedStmt.close();
        eventlogger(Values.TypeEvent.LOGIN, Values.CLIENT_LOGINOK + "--" + username );
        // Insert in log - Login Sucess
        return true;

    }

    public User findUser(String username) throws SQLException {     // Needs test

        User user = null;
        PreparedStatement preparedStmt = connection.prepareStatement(Querys.SELECT_USER);
        preparedStmt.setString(1, username);
        System.out.println("before result ");
        ResultSet resultSet = preparedStmt.executeQuery();

        if(resultSet.next()) {

            String usernameValue = resultSet.getString("user_name");
            String passwordValue = resultSet.getString("user_password");
            user = new User(usernameValue, passwordValue);
            System.out.println(user.toString());
        }
        //System.out.println(user.toString());
        return user ;
    }


    public int count() throws SQLException {

        Statement statement = connection.createStatement();

        //String query = "SELECT COUNT(*) FROM users";

        ResultSet resultSet = statement.executeQuery(Querys.COUNT_USERS);
        if(resultSet.next())
            return resultSet.getInt(1);
        else
            return 0;
    }

    public void eventlogger(Values.TypeEvent type_event, String message){

        PreparedStatement preparedStmt;
        try {
            preparedStmt = connection.prepareStatement(Querys.LOG);
            preparedStmt.setString(1,type_event.toString());
            preparedStmt.setString(2, message);
            preparedStmt.execute();
            preparedStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<String> getUserBio(String username) throws SQLException {

        System.out.println("vou executar a query para a bio com o valor de " + username);

        PreparedStatement preparedStmt = connection.prepareStatement(Querys.SHOW_BIO);
        preparedStmt.setString(1, username);
        System.out.println("before result ");
        ResultSet resultSet = preparedStmt.executeQuery();

        if(resultSet.next()) {
            List<String> userbio = new LinkedList<String>();
            userbio.add(resultSet.getString("user_name"));
            userbio.add(resultSet.getString("email"));
            userbio.add(resultSet.getString("date_birth"));
            //userbio.add(resultSet.getString("picture"));
            userbio.add(resultSet.getString("date_registration"));
            return userbio;
        }
       return null;
    }
}
