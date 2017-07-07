DROP DATABASE IF EXISTS freespeech;
CREATE DATABASE freespeech;
USE freespeech;

CREATE TABLE user (
  user_name     CHAR(15)  NOT NULL,
  user_password CHAR(150) NOT NULL,
  PRIMARY KEY (user_name)
);

CREATE TABLE bio (
  user_name         CHAR(15) NOT NULL,
  email             CHAR(100),
  date_birth        CHAR(30),
  picture           CHAR(100),
  date_registration DATE,
  bio               VARCHAR(500),
  PRIMARY KEY (user_name),
  FOREIGN KEY (user_name) REFERENCES user (user_name)
    ON DELETE CASCADE
);

CREATE TABLE log (
  log_id        INTEGER   AUTO_INCREMENT,
  date_event    TIMESTAMP DEFAULT now(),
  type_event    CHAR(50)  NOT NULL,
  message_event CHAR(100) NOT NULL,
  PRIMARY KEY (log_id)
);

CREATE TABLE admin (
  admin_type ENUM ('ROOT', 'ADMIN'),
  pass_word  CHAR(10)
);


CREATE TABLE report (
  report_id 	INTEGER AUTO_INCREMENT,
  user_name     CHAR(15)  NOT NULL,
  user_reported CHAR(15) NOT NULL,
  date_report 	DATE,
  PRIMARY KEY (report_id, user_name, user_reported),
  FOREIGN KEY (user_name) REFERENCES user (user_name) ON DELETE CASCADE,
  FOREIGN KEY (user_reported) REFERENCES user (user_name) ON DELETE CASCADE

);

INSERT INTO admin VALUES ('ROOT', 'dblord'), ('ADMIN', 'noob');