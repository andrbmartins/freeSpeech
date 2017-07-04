package org.academiadecodigo.bootcamp8.freespeech.shared;

/**
 * Developed @ <Academia de Código_>
 */

public class Values {

    public static final String HOST = "127.0.0.1";
    public static final int SERVER_PORT = 4040;

    public static final String VIEW_PATH = "../view";
    public static final String CONNECTING_SCENE = "connection";
    public static final String LOGIN_SCENE = "login";
    public static final String USER_SCENE = "user";

    public static final Long SERIAL_VERSION_UID = 12345678998432L;

    public static final String NAME_KEY = "username";
    public static final String PASSWORD_KEY = "password";
    public static final String NEW_PASSWORD = "newPassword";
    public static final String DESTINY = "to";

    public static final String LOGIN_OK = "Successfully logged in!";
    public static final String LOGIN_FAIL = "Unable to login.";
    public static final String REGISTER_OK = "Successfully registered.";
    public static final String REGISTER_FAIL = "Username unavailable";
    public static final String UNMATCHED_PASSWORD = "Passwords don't match.";
    public static final String PASS_NOT_CHANGED = "Incorrect password. Not able to change your password.";
    public static final String PASS_CHANGED = "Successfully updated your password";
    public static final String NAME_TOO_LONG = "Name can't be longer than 15 characters";

    public static final String ACC_DELETED = "Your account was deleted. We are sorry to see you go. Come back when" +
            " you get tired of Slack Police!!!";
    public static final String NOT_VALIDATED = "Unable to delete your account. Please check your password";

    public static final String INVALID_INPUT = "Required fields are empty.";

    public static final String STYLESHEET = "resources/stylesheet.css";

    public static final String URL_DBSERVER = "jdbc:mysql://localhost:3306/freespeech";
    public static final String USER_DBSERVER = "root";
    public static final String PASSWORD_DBSERVER = "";

    public static final String SERVER_START = "SERVER START";
    public static final String SERVER_STOP = "SERVER STOP";
    public static final String SERVER_DBCONNECT = "SERVER CONNECTED DATABASE";
    public static final String SERVER_DBDISCONNECT = "SERVER NOT CONNECTED TO DATABASE";
    public static final String CONNECT_CLIENT = "CLIENT CONNECTED";
    public static final String CLIENT_DISCONNECTED = "CLIENT DISCONNECTED";
    public static final String CLIENT_LOGINOK = "CLIENT LOGGED IN";
    public static final String CLIENT_LOGINFAILED= "CLIENT LOGIN FAILED";
    public static final String CLIENT_LOGOUT = "CLIENT LOGGED OUT";
    public static final String CLIENT_REGISTED = "CLIENT REGISTERED";
    public static final String CLIENT_REGISTER_FAILED = "FAILED TO REGISTER USER";
    public static final String CLIENT_PASSORD = "CLIENT CHANGED PASSWORD";
    public static final String ACCOUNT_DELETED = "USER ACCOUNT DELETED";

    public static final double LOGIN_HEIGHT = 450d;
    public static final double LOGIN_WIDTH = 350d;
    public static final double CLIENT_WIDTH = 900d;
    public static final double CLIENT_HEIGHT = 600d;


}
