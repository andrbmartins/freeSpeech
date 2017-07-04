package org.academiadecodigo.bootcamp8.freespeech.serverapp.service;

import org.academiadecodigo.bootcamp8.freespeech.serverapp.Utils;

import java.sql.*;

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

    /**
     * Executes query and converts to string with all the info in formatted style
     *
     * @param query the query to be performed
     * @return the resulting string formatted
     */
    public String executeQuery(String query) {

        ResultSet resultSet;
        ResultSetMetaData metaData;
        StringBuilder builder = new StringBuilder();
        builder.append(query);

        try {

            statement = connection.getConnection().createStatement();
            resultSet = statement.executeQuery(query);
            metaData = resultSet.getMetaData();
            int columnsNumber = metaData.getColumnCount();

            while (resultSet.next()) {

                builder.append("\n");

                for (int i = 1; i <= columnsNumber; i++) {

                    if (i > 1) {
                        builder.append(" <||> ");
                    }

                    String columnValue = resultSet.getString(i);
                    builder.append(metaData.getColumnName(i));
                    builder.append(": ");
                    builder.append(columnValue);

                }
            }

            builder.append("\n");

        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        } finally {
            closeStatement();
        }

        return builder.toString().equals(query + "\n") ? (builder.append(Utils.EMPTY_TABLE)).toString() : builder.toString();
    }

    /**
     * Deletes all the data from set table
     *
     * @return true if data deleted or false if unable to delete
     */
    public boolean clearTable() {

        boolean deleteOk;

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

    public Utils.AdminLevel checkPassword(String password) {

        PreparedStatement statement = null;
        ResultSet resultSet;
        Utils.AdminLevel adminLevel = Utils.AdminLevel.INVALID;

        try {

            statement = connection.getConnection().prepareStatement(Utils.LOGIN_QUERY);
            statement.setString(1, password);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {

                if ((resultSet.getString("admin_type")).equals("ROOT")) {
                    adminLevel = Utils.AdminLevel.ROOT;
                } else {
                    adminLevel = Utils.AdminLevel.ADMIN;
                }
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {

            try {

                if (statement != null) {
                    statement.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return adminLevel;
    }

    /**
     * Closes the statement once all reading is completed
     */
    public void closeStatement() {

        try {

            if (statement != null) {
                statement.close();
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }
}

