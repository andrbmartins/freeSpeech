DROP DATABASE IF EXISTS freespeech;
CREATE DATABASE freespeech;
USE freespeech;

CREATE TABLE user(
    user_name CHAR(15) NOT NULL,
    user_password CHAR(15) NOT NULL,
    PRIMARY KEY (user_name)
);


CREATE TABLE bio(
	user_name CHAR(15) NOT NULL,
    email CHAR(100),
    date_birth DATE,
    picture CHAR(100),
    date_registration DATE,
    PRIMARY KEY (user_name),
	FOREIGN KEY (user_name) REFERENCES user(user_name) ON DELETE CASCADE
    );

CREATE TABLE log(
    log_id INTEGER AUTO_INCREMENT,
    useruseruseruserdate_event DATETIME NOT NULL,
    type_event CHAR(50) NOT NULL,
    message_event CHAR(100) NOT NULL,
    PRIMARY KEY (log_id)
    
);

	