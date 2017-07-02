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
            connection = DriverManager.getConnection(Values.URL_DBSERVER, Values.USER_DBSERVER, Values.PASSWORD_DBSERVER);
            eventlogger(Values.TypeEvent.DATABASE, Values.SERVER_DBCONNECT);
            } catch (SQLException e) {
            eventlogger(Values.TypeEvent.DATABASE, Values.SERVER_DBDISCONNECT);
            System.out.println(Values.SERVER_DBDISCONNECT);
            //e.printStackTrace();
        }
        System.out.println(connection.toString());

    }

    //public Connection getConnection() {
    //return connection;
    //}

    public boolean insertUser(String username, String password) {    // TESTED OK
        boolean registered = true;
        PreparedStatement preparedStmt = null;
        try {
            //System.out.println(password.length());
            preparedStmt = connection.prepareStatement(Querys.INSERT_USER);
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

    public boolean authenticateUser(String username, String password) throws SQLException {  // TESTED OK

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

    public void eventlogger(Values.TypeEvent type_event, String message){

        PreparedStatement preparedStmt = null;
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

}
