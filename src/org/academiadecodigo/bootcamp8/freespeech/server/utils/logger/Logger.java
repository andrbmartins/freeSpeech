package org.academiadecodigo.bootcamp8.freespeech.server.utils.logger;

import org.academiadecodigo.bootcamp8.freespeech.server.model.ConnectionManager;
import org.academiadecodigo.bootcamp8.freespeech.shared.Queries;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author by André Martins <Code Cadet>
 *         Project freeSpeech (03/07/17)
 *         <Academia de Código_>
 */
public class Logger {

    private static Logger instance = null;
    private static ConnectionManager connection;

    private Logger() {
    }

    public static Logger getInstance() {

        if (instance == null) {

            synchronized (Logger.class) {

                if (instance == null) {
                    instance = new Logger();
                }
            }

        }

        return instance;
    }

    public void eventlogger(TypeEvent type_event, String message) {

        PreparedStatement preparedStmt = null;

        try {

            preparedStmt = connection.getConnection().prepareStatement(Queries.LOG);
            preparedStmt.setString(1, type_event.toString());
            preparedStmt.setString(2, message);
            preparedStmt.execute();
            preparedStmt.close();

        } catch (SQLException e) {
            Logger.getInstance().eventlogger(TypeEvent.DATABASE, e.getMessage());
        }

    }

    public static void setConnection(ConnectionManager connection) {
        Logger.connection = connection;
    }
}
