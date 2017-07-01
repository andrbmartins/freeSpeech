package org.academiadecodigo.bootcamp8.freespeech.server.serverapp;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */
public class Utils {
    public static final String SERVER_INFO = "SELECT date_event, message_event FROM log WHERE type_event='SERVER'";

    public static final String USER_CONNECTION = "SELECT date_event, message_event FROM log WHERE type_event!='SERVER'";

    public static final String USERS_TABLE = "SELECT COUNT(*) FROM user" ;

    public static final String CLEAR_TABLE = "DELETE FROM log";

    public static final String CONFIRM = "Confirm delete of data";

    public static final String CONFIRM_QUESTION = "Are you sure you want to delete all data from 'log' table?";

    public static final String CLEARED = "'log' Table cleared successfully";

    public static final String NOT_CLEARED = "Unable to clear table. Try again";

    public static final String CLEARING_LOG = "Clearing 'log' table...";

    public static final String SAVING_FILE = "Saving File...";

    public static final String FILE_SAVED = "File saved successfully";

    public static final String DEFAULT_FILE = "/freeSpeechServerLog.txt";

    public static final String EMPTY_TABLE = "This query returned no results \n";

    public static final String VALIDATING_QUERY = "Query validator";

    public static final String INVALID_QUERY = "Your query is not valid. Only SELECT is allowed";


}
