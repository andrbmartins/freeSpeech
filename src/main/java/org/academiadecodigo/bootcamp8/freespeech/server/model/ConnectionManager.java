package org.academiadecodigo.bootcamp8.freespeech.server.model;

import org.academiadecodigo.bootcamp8.freespeech.server.utils.logger.Logger;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.logger.LoggerMessages;
import org.academiadecodigo.bootcamp8.freespeech.server.utils.logger.TypeEvent;
import org.academiadecodigo.bootcamp8.freespeech.shared.Queries;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> JPM Ramos
 */

//TODO changed password to work at home

public class ConnectionManager {
    private Connection connection;

    public Connection getConnection() {
        try {
            if (connection == null) {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/freespeech", "root", "1234" /*""*/);
                Logger.getInstance().eventlogger(TypeEvent.DATABASE, LoggerMessages.DB_CONNECT);
            }
        } catch (SQLException ex) {
            System.err.println(LoggerMessages.DB_DISCONNECT + ex.getMessage());
            System.exit(1);
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
            Logger.getInstance().eventlogger(TypeEvent.DATABASE, e.getMessage());
            registered = false;

        } finally {

            try {
                if (preparedStmt != null) {
                    preparedStmt.close();
                }
            } catch (SQLException e) {
                Logger.getInstance().eventlogger(TypeEvent.DATABASE, e.getMessage());
            }
        }

        return registered;
    }

    public User findUser(String username) throws SQLException {

        User user = null;
        PreparedStatement preparedStmt = connection.prepareStatement(Queries.SELECT_USER);
        preparedStmt.setString(1, username);
        ResultSet resultSet = preparedStmt.executeQuery();

        if (resultSet.next()) {

            String usernameValue = resultSet.getString("user_name");
            String passwordValue = resultSet.getString("user_password");
            user = new User(usernameValue, passwordValue);
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

            Logger.getInstance().eventlogger(TypeEvent.DATABASE, e1.getMessage());
            passChanged = false;

        } finally {

            try {
                if (preparedStmt != null) {
                    preparedStmt.close();
                }
            } catch (SQLException e) {
                Logger.getInstance().eventlogger(TypeEvent.DATABASE, e.getMessage());
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

            Logger.getInstance().eventlogger(TypeEvent.DATABASE, e1.getMessage());
            deleted = false;

        } finally {

            try {
                if (preparedStmt != null) {
                    preparedStmt.close();
                }
            } catch (SQLException e) {
                Logger.getInstance().eventlogger(TypeEvent.DATABASE, e.getMessage());
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
            List<String> userbio = new LinkedList<String>();

        if (resultSet.next()) {
            userbio.add(resultSet.getString("user_name"));
            userbio.add(resultSet.getString("email"));
            userbio.add(resultSet.getString("date_birth"));
            userbio.add(resultSet.getString("bio"));
        }

        preparedStmt.close();
        return userbio;
    }

    public boolean updateBio(String username, String email, String dateBirth, String bio) {

        PreparedStatement preparedStmt = null;
        boolean updated = true;

        try {

            preparedStmt = connection.prepareStatement(Queries.UPDATE_BIO);
            preparedStmt.setString(1, email);
            preparedStmt.setString(2, dateBirth);
            preparedStmt.setString(3, bio);
            preparedStmt.setString(4, username);
            preparedStmt.execute();

        } catch (SQLException e) {
            Logger.getInstance().eventlogger(TypeEvent.DATABASE, e.getMessage());
            updated = false;
        } finally {

            try {
                if (preparedStmt != null) {
                    preparedStmt.close();
                }
            } catch (SQLException e) {
                Logger.getInstance().eventlogger(TypeEvent.DATABASE, e.getMessage());
            }

        }

        return updated;
    }



    public boolean reportUser(String username , String user_reported){

        PreparedStatement preparedStmt = null;
        boolean reported = true;

        try {

            preparedStmt = connection.prepareStatement(Queries.REPORT_USER);
            preparedStmt.setString(1, username);
            preparedStmt.setString(2, user_reported);
            preparedStmt.execute();

        } catch (SQLException e) {
            Logger.getInstance().eventlogger(TypeEvent.DATABASE, e.getMessage());
            reported = false;
        } finally {

            try {
                if (preparedStmt != null) {
                    preparedStmt.close();
                }
            } catch (SQLException e) {
                Logger.getInstance().eventlogger(TypeEvent.DATABASE, e.getMessage());
            }

        }

        return reported;
    }


    public int verifyReport(String username, String user_reported) {

        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        try {

            preparedStmt = connection.prepareStatement(Queries.REPORTED_USER);
            preparedStmt.setString(1, username);
            preparedStmt.setString(2, user_reported);
            resultSet =  preparedStmt.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 0;

        } catch (SQLException e) {
            Logger.getInstance().eventlogger(TypeEvent.DATABASE, e.getMessage());
        } finally {

            try {
                if (preparedStmt != null) {
                    preparedStmt.close();
                }
            } catch (SQLException e) {
                Logger.getInstance().eventlogger(TypeEvent.DATABASE, e.getMessage());
            }

        }
        return 0;
    }


    public void close() {

        try {
            if (connection != null) {
                Logger.getInstance().eventlogger(TypeEvent.DATABASE, LoggerMessages.DB_TERMINATE);
                connection.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

    }


}

