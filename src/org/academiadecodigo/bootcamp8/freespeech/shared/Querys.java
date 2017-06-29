package org.academiadecodigo.bootcamp8.freespeech.shared;

/**
 * Created by Jramos on 30-06-2017.
 */
public class Querys {

    public static final String SELECTUSER = "Select * from users;";
    public static final String SELECT = "login";
    public static final String SELECTUSER_SCENE = "user";

}


// SELECT * FROM freespeech.user;
// SELECT * FROM freespeech.bio;

// DELETE FROM user USING user WHERE user.user_name = 'jpmramos';

// SELECT * FROM `bio`, `user` WHERE `user`.`user_name` = `bio`.`user_name`;

// SELECT * FROM `bio`, `user` WHERE `user`.`user_name` = `bio`.`user_name` AND  `user`.`user_name` = 'jpmramos'