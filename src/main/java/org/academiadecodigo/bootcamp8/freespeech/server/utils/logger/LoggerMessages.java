package org.academiadecodigo.bootcamp8.freespeech.server.utils.logger;

/**
 * @author by André Martins <Code Cadet>
 *         Project freeSpeech (04/07/17)
 *         <Academia de Código_>
 */
public class LoggerMessages {

    public static final String SERVER_START = "SERVER START";
    public static final String SERVER_STOP = "SERVER STOP";
    public static final String DB_CONNECT = "CONNECTION TO DATABASE ESTABLISHED";
    public static final String DB_DISCONNECT = "SERVER NOT CONNECTED TO DATABASE";
    public static final String DB_TERMINATE = "CONNECTION TO DATABASE TERMINATED";
    public static final String CONNECT_CLIENT = "CLIENT CONNECTED";
    public static final String CLIENT_ILLEGAL_LOG = "CLIENT ILLEGAL INIT MESSAGE HEADER";
    public static final String CLIENT_DISCONNECTED = "CLIENT DISCONNECTED: ";
    public static final String CLIENT_LOGIN_OK = "CLIENT LOGGED IN: ";
    public static final String CLIENT_LOGIN_FAILED = "CLIENT LOGIN FAILED: ";
    public static final String CLIENT_ALREADY_LOGGED = "CLIENT TRYING TO LOG INTO ALREADY LOGGED ACCOUNT: ";
    public static final String CLIENT_LOGOUT = "CLIENT LOGGED OUT: ";
    public static final String CLIENT_REGISTERED = "CLIENT REGISTERED: ";
    public static final String CLIENT_REGISTER_FAILED = "FAILED TO REGISTER USER: ";
    public static final String CLIENT_PASSWORD = "CLIENT CHANGED PASSWORD: ";
    public static final String CLIENT_PASS_FAILED = "FAILED ATTEMPT AT CHANGING PASSWORD: ";
    public static final String ACCOUNT_DELETED = "USER ACCOUNT DELETED: ";
    public static final String ACCOUNT_DEL_FAILED = "FAILED ATTEMPT AT DELETING ACCOUNT: ";
    public static final String CLIENT_REPORTED = " WAS REPORTED BY ";

}
