package org.academiadecodigo.bootcamp8.freespeech.server.model;


import org.academiadecodigo.bootcamp8.freespeech.server.utils.logger.Logger;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.logger.TypeEvent;
import org.academiadecodigo.bootcamp8.freespeech.shared.Queries;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> JPM Ramos
 */
public class ConnectionManager {

    private Connection connection;

    public Connection getConnection() {
        try {
            if (connection == null) {
                connection = DriverManager.getConnection(Values.URL_DBSERVER, Values.USER_DBSERVER, Values.PASSWORD_DBSERVER);
                Logger.getInstance().eventlogger(TypeEvent.DATABASE, Values.SERVER_DBCONNECT);
            }
        } catch (SQLException ex) {
            Logger.getInstance().eventlogger(TypeEvent.DATABASE, Values.SERVER_DBDISCONNECT);
        }
        return connection;
    }

    public boolean insertUser(String username, String password) {

        boolean registered = true;
        PreparedStatement preparedStmt = null;

        try {

            preparedStmt = connection.prepareStatement(Queries.INSERT_USER);
            preparedStmt.setString(1, username);
            preparedStmt.setString(2, password);
            preparedStmt.execute();
            preparedStmt = connection.prepareStatement(Queries.INSERT_INTO_BIO);
            preparedStmt.setString(1, username);
            preparedStmt.executeUpdate();

        } catch (SQLException e) {

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

    public User findUser(String username) throws SQLException {

        User user = null;
        PreparedStatement preparedStmt = connection.prepareStatement(Queries.SELECT_USER);
        preparedStmt.setString(1, username);
        System.out.println("before result ");
        ResultSet resultSet = preparedStmt.executeQuery();

        if (resultSet.next()) {

            String usernameValue = resultSet.getString("user_name");
            String passwordValue = resultSet.getString("user_password");
            user = new User(usernameValue, passwordValue);
            System.out.println(user.toString());
        }

        preparedStmt.close();

        return user;
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

            e1.printStackTrace();
            passChanged = false;

        } finally {

            try {
                if (preparedStmt != null) {
                    preparedStmt.close();
                }
            } catch (SQLException e) {
                Logger.getInstance().eventlogger(TypeEvent.DATABASE, "Failed on password change");
            }
        }

        return passChanged;
    }

    public boolean deleteAccount(String username) {

        boolean deleted = true;
        PreparedStatement preparedStmt = null;

        try {

            preparedStmt = connection.prepareStatement(Queries.DELETE_USER);
            preparedStmt.setString(1, username);
            preparedStmt.execute();

        } catch (SQLException e1) {
            deleted = false;
        } finally {

            try {
                if (preparedStmt != null) {
                    preparedStmt.close();
                }
            } catch (SQLException e) {
                Logger.getInstance().eventlogger(TypeEvent.DATABASE, "Failed on account delete");
            }

        }

        return deleted;
    }

    public int count() throws SQLException {

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(Queries.COUNT_USERS);

        return resultSet.next() ? resultSet.getInt(1) : 0;

    }

    public List<String> getUserBio(String username) throws SQLException {

        PreparedStatement preparedStmt = connection.prepareStatement(Queries.SHOW_BIO);
        preparedStmt.setString(1, username);
        ResultSet resultSet = preparedStmt.executeQuery();

        if (resultSet.next()) {

            List<String> userbio = new LinkedList<String>();
            userbio.add(resultSet.getString("user_name"));
            userbio.add(resultSet.getString("email"));
            userbio.add(resultSet.getString("date_birth"));
            userbio.add(resultSet.getString("bio"));
            return userbio;

        }

        preparedStmt.close();
        return new LinkedList<>();
    }

    public boolean updateBio(String username, String email, String dateBirth, String bio) {

        PreparedStatement preparedStmt = null;
        boolean updated = true;
        System.out.println("here?");

        try {

            preparedStmt = connection.prepareStatement(Queries.UPDATE_BIO);
            preparedStmt.setString(1, email);
            preparedStmt.setString(2, dateBirth);
            preparedStmt.setString(3, bio);
            preparedStmt.setString(4, username);
            preparedStmt.execute();

        } catch (SQLException e) {
            updated = false;
            Logger.getInstance().eventlogger(TypeEvent.DATABASE, "Failure on bio update");
        } finally {

            try {
                preparedStmt.close();
            } catch (SQLException e) {
                Logger.getInstance().eventlogger(TypeEvent.DATABASE, "Failure on close SQL statement");
            }

        }

        System.out.println(updated);
        return updated;
    }

    public void close() {

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            Logger.getInstance().eventlogger(TypeEvent.DATABASE, "Unable to close database connections");
        }

    }

}

