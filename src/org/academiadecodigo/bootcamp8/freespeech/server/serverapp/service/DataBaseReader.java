package org.academiadecodigo.bootcamp8.freespeech.server.serverapp.service;

import org.academiadecodigo.bootcamp8.freespeech.server.serverapp.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */
public class DataBaseReader {
    private Statement statement;
    private JdbcConnectionManager connection;

    public DataBaseReader(JdbcConnectionManager manager) {
        connection = manager;
    }

    public ResultSet executeQuery(String query) {
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.getConnection().createStatement();
            resultSet = statement.executeQuery(query);

        } catch (SQLException e) {
            System.out.println("Unable to read from database " + e.getMessage());
        }
        return resultSet;
    }

    public boolean clearTable() {
        boolean deleteOk;
        Statement statement = null;

        try {
            statement = connection.getConnection().createStatement();
            statement.executeUpdate(Utils.CLEAR_TABLE);
            deleteOk = true;
        } catch (SQLException e) {
            deleteOk = false;
        } finally {
            closeStatement();
        }
        return deleteOk;
    }

    public void closeStatement() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

