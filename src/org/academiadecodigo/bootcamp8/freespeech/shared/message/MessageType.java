package org.academiadecodigo.bootcamp8.freespeech.shared.message;

/**
 * Developed @ <Academia de Código_>
 */

public enum MessageType {

    //TODO cleanup this mess

    DATA,
    PRIVATE_DATA, //content will be an hashmap with 2 entries ( Values.DESTINY_USER : <destiny user> ; Values.MESSAGE : <message_to_the_user>). both values can be encrypted
    TEXT,
    PRIVATE_TEXT, //content will be an hashmap with 2 entries ( Values.DESTINY_USER : <destiny user> ; Values.MESSAGE : <message_to_the_user>). both values can be encrypted
    LOGIN,
    REGISTER,
    USERS_ONLINE,
    NOTIFICATION,
    KEY,
    OWN_BIO,
    BIO_UPDATE,
    PASS_CHANGE,
    EXIT,
    BIO,
    REPORT, DELETE_ACCOUNT
}
