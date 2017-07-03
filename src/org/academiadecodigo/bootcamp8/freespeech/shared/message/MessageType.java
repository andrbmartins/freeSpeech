package org.academiadecodigo.bootcamp8.freespeech.shared.message;

/**
 * Developed @ <Academia de Código_>
 */

public enum MessageType {

    //TODO cleanup this mess

    DATA,
    PRIVATE_DATA, //content will be an hashmap with 2 entries ( "to" : <destiny_name> ; "message" : <message_to_send>). both values can be encrypted
    TEXT,
    PRIVATE_TEXT, //content will be an hashmap with 2 entries ( "to" : <destiny_name> ; "message" : <message_to_send>). both values can be encrypted
    LOGIN,
    REGISTER,
    USERS_ONLINE,
    NOTIFICATION,
    KEY,
    GET_BIO,
    BIO_UPDATE,
    PASS_CHANGE,
    EXIT,
    BIO,
    DELETE_ACCOUNT
}
