package org.academiadecodigo.bootcamp8.freespeech.shared.message;

/**
 * Created by codecadet on 26/06/17.
 */
public enum MessageType {

    DATA,
    PRIVATE_DATA, //content will be an hashmap with 2 entries ( Values.DESTINY_USER : <destiny user> ; Values.MESSAGE : <message_to_the_user>). both values can be encrypted
    TEXT,
    PRIVATE_TEXT, //content will be an hashmap with 2 entries ( Values.DESTINY_USER : <destiny user> ; Values.MESSAGE : <message_to_the_user>). both values can be encrypted
    LOGIN,
    REGISTER,
    REQUEST_USERS_ONLINE,
    NOTIFICATION,
    COMMAND
}
