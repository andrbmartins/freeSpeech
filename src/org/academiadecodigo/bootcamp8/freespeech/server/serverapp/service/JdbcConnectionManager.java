package org.academiadecodigo.bootcamp8.freespeech.server.serverapp.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */
public class JdbcConnectionManager {
    private Connection connection = null;

    /**
     * Creates connection to specified database
     * @return the open connection
     */
    public Connection getConnection() {

        try {
            if (connection == null) {
                connection = DriverManager.getConnection("jdbc:mysql://localhost/freespeech", "root", "babaji");
            }
        } catch (SQLException ex) {
            System.out.println("Connection to database failed : " + ex.getMessage());
        }
        return connection;
    }

    /**
     * Closes the connection to the database
     */
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.out.println("Error while closing database connection: " + ex.getMessage());
        }
    }
}
