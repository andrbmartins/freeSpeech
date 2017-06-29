package org.academiadecodigo.bootcamp8.freespeach.shared.message;

/**
 * Created by codecadet on 26/06/17.
 */
public enum MessageType {

    DATA,
    PRIVATE_DATA, //content will be an hashmap with 2 entries ( "to" : <destiny_name> ; "message" : <message_to_send>). both values can be encrypted
    TEXT,
    PRIVATE_TEXT, //content will be an hashmap with 2 entries ( "to" : <destiny_name> ; "message" : <message_to_send>). both values can be encrypted
    LOGIN,
    REGISTER,
    REQUEST_INFO_SERVER,
    REQUEST_CHANNEL,
    NOTIFICATION,
    COMMAND

}
