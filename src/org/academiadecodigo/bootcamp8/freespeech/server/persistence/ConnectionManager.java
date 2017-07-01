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
            e.printStackTrace();
        }
        System.out.println(connection.toString());

    }

    public Connection getConnection() {
        return connection;
    }

    public void insertUser(String username, String password) throws SQLException {    // TESTED OK
        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = connection.prepareStatement(Querys.INSERT_USER);
            preparedStmt.setString(1,  username);
            preparedStmt.setString(2, password);
            ResultSet resultSet = preparedStmt.executeQuery();
            /*if (!resultSet.next()) {
                preparedStmt.close();
                // Insert in log - Register Failed
                eventlogger(Values.TypeEvent.REGISTER, Values.CLIENT_REGISTER_FAILED + "--" + username );
                return ;
            }*/

            //eventlogger(Values.TypeEvent.CLIENT, Values.CLIENT_REGISTED + "--" + username );
            //preparedStmt.close();

        } catch (SQLException e) {
            //e.printStackTrace();
            // Send message de erro para os logs

            eventlogger(Values.TypeEvent.CLIENT, Values.CLIENT_REGISTED + "--" + username );
            //System.out.println("Erro ao inserir user");

        }
        preparedStmt.close();
    }

    public boolean authenticateUser(String username, String password) throws SQLException {  // TESTED OK


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
