package org.academiadecodigo.bootcamp8.freespeech.serverapp;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */
public class Utils {

    public static final String SERVER_INFO = "SELECT date_event, message_event FROM log WHERE type_event='SERVER'";
    public static final String USER_CONNECTION = "SELECT date_event, message_event FROM log WHERE type_event='CLIENT'";
    public static final String USERS_TABLE = "SELECT COUNT(*) FROM user";
    public static final String DB_LOG = "SELECT date_event, message_event FROM log WHERE type_event='DATABASE'";
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
    public static final String INVALID_QUERY = "Your query is not valid. Only SELECT is allowed. Access to admin table blocked.";
    public static final String INVALID_LOG = "Invalid Login";
    public static final String ENTER_VALID = "Please enter valid password";
    public static final String LOGIN_QUERY = "SELECT admin_type FROM admin WHERE pass_word = ?";
    public static final String GRANTED = "Access granted";
    public static final String ROOT_ACCESS = "Root access granted";
    public static final String ADMIN_ACCESS = "Admin level access granted. Some functionalities are not available";

    public enum AdminLevel {
        ROOT,
        ADMIN,
        INVALID
    }

}
