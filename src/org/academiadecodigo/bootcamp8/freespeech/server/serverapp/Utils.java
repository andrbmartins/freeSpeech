package org.academiadecodigo.bootcamp8.freespeech.server.serverapp;

/**
 * Created by Prashanta on 30/06/17.
 */
public class Utils {
    public static final String SERVER_ERROR = "SELECT timestamp, msg FROM server_log WHERE type='server_log'";

    public static final String USER_CONNECTION = "SELECT timestamp, info FROM server_log WHERE type='user_connection'";

    public static final String CLEAR_TABLE = "DELETE FROM server_log";

    public static final String CLEARED = "Table cleared successfully";

    public static final String NOT_CLEARED = "Unable to clear table. Try again";

    public static final String FILE_PATH = "/freeSpeechServerLog.txt";
}
