package org.academiadecodigo.bootcamp8.freespeech.shared;

/**
 * Created by Jramos on 30-06-2017.
 */
public class Queries {

    public static final String SELECT_USER = "SELECT * FROM user WHERE user_name=?";
    public static final String DELETE_USER = "DELETE FROM user USING user WHERE user.user_name =?";
    public static final String ALTER_PASSWORD = "UPDATE user SET user.user_password = ? WHERE user_name=?";
    public static final String INSERT_USER =  "INSERT INTO user(user_name, user_password) VALUES(?, ?)";
    public static final String COUNT_USERS = "SELECT COUNT(*) FROM users";

    public static final String INSERT_INTO_BIO = "INSERT INTO bio(user_name, date_registration) VALUES (?, now())";
    public static final String UPDATE_BIO = "UPDATE bio SET email = ? , date_birth = ? , bio = ? WHERE user_name = ? ";
    public static final String SHOW_BIO = "SELECT * FROM bio WHERE user_name = ?";
    public static final String LOG = "INSERT INTO log(date_event, type_event, message_event) VALUES (now(), ?, ?)";

    public static final String REPORT_USER = "INSERT INTO report(user_name, user_reported) VALUES (?, ?)";
    public static final String DELETE_USER_REPORTED = "DELETE from report WHERE report.user_reported = ? ";
    public static final String COUNT_REPORTED = "SELECT count(*) from report WHERE report.user_reported = ?";
    public static final String REPORTED_USER = "SELECT count(*) FROM report WHERE user_name = ? AND user_reported = ?";


}

