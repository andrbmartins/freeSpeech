package org.academiadecodigo.bootcamp8.freespeech.server.model.logger;


import org.academiadecodigo.bootcamp8.freespeech.shared.Queries;
import org.academiadecodigo.bootcamp8.freespeech.shared.Values;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author by André Martins <Code Cadet>
 *         Project freeSpeech (03/07/17)
 *         <Academia de Código_>
 */
public class Logger {


    private Connection connection;

    public Logger(Connection connection) {

       this.connection = connection;
    }



    //TODO make this event logger a utilitary class but keep method here

    public void eventlogger(TypeEvent type_event, String message) {

        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = connection.prepareStatement(Queries.LOG);
            preparedStmt.setString(1, type_event.toString());
            preparedStmt.setString(2, message);
            preparedStmt.execute();
            preparedStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }






}
