package org.academiadecodigo.bootcamp8.freespeech.server.persistence;
// TODO persistence package??? Wht don't call them model???

import org.academiadecodigo.bootcamp8.freespeech.server.utils.User;
import org.academiadecodigo.bootcamp8.freespeech.shared.Queries;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

import java.sql.*;
import java.util.*;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> JPM Ramos
 */
public class ConnectionManager  {

    private Connection connection;

    public ConnectionManager() {
        try {
            connection = DriverManager.getConnection(Values.URL_DBSERVER, Values.USER_DBSERVER, Values.PASSWORD_DBSERVER);
            eventlogger(Values.TypeEvent.DATABASE, Values.SERVER_DBCONNECT);

        } catch (SQLException e) {
            eventlogger(Values.TypeEvent.DATABASE, Values.SERVER_DBDISCONNECT);
        }

    }


    public boolean insertUser(String username, String password) {    // TESTED OK
        boolean registered = true;
        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = connection.prepareStatement(Queries.INSERT_USER);
            preparedStmt.setString(1,  username);
            preparedStmt.setString(2, password);
            preparedStmt.execute();
            eventlogger(Values.TypeEvent.CLIENT, Values.CLIENT_REGISTED + "--" + username );


        } catch (SQLException e) {

            eventlogger(Values.TypeEvent.CLIENT, Values.CLIENT_REGISTER_FAILED + " -- " + username );
            System.out.println(Values.CLIENT_REGISTER_FAILED);
            registered = false;

        } finally {
            try {
                if (preparedStmt != null) {
                    preparedStmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return registered;
    }


    public User findUser(String username) throws SQLException {     // Needs test
        User user = null;

        PreparedStatement preparedStmt = connection.prepareStatement(Queries.SELECT_USER);
        preparedStmt.setString(1, username);
        System.out.println("before result ");
        ResultSet resultSet = preparedStmt.executeQuery();

        if(resultSet.next()) {

            String usernameValue = resultSet.getString("user_name");
            String passwordValue = resultSet.getString("user_password");
            user = new User(usernameValue, passwordValue);
            System.out.println(user.toString());
        }
        preparedStmt.close();
        return user ;
    }

    public boolean changePass(String username, String newPass) {
        boolean passChanged = true;
        PreparedStatement preparedStmt = null;

        try {
            preparedStmt = connection.prepareStatement(Queries.ALTER_PASSWORD);
            preparedStmt.setString(1, newPass);
            preparedStmt.setString(2, username);
            preparedStmt.execute();

        } catch (SQLException e1) {
            eventlogger(Values.TypeEvent.CLIENT, Values.CLIENT_PASSORD + "--" + username);
            //e1.printStackTrace();
            passChanged = false;
        } finally {
            try {
                if (preparedStmt != null) {
                    preparedStmt.close();
                }
            } catch (SQLException e) {
                //TODO event logger
                e.printStackTrace();
            }
        }
        return passChanged;
    }


    public int count() throws SQLException {
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(Queries.COUNT_USERS);
        if(resultSet.next())
            return resultSet.getInt(1);
        else
            return 0;
    }

    //TODO make this event logger a utilitary class but keep method here
    public void eventlogger(Values.TypeEvent type_event, String message){

        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = connection.prepareStatement(Queries.LOG);
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

        PreparedStatement preparedStmt = connection.prepareStatement(Queries.SHOW_BIO);
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
