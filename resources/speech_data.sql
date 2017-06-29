USE freespeech;

INSERT INTO user(user_name, user_password ) VALUES ('jpmramos', '12345');
INSERT INTO user(user_name, user_password ) VALUES ('amartins', '12345');
INSERT INTO user(user_name, user_password ) VALUES ('ffernandes', '12345');
INSERT INTO user(user_name, user_password ) VALUES ('fsantos', '12345');


INSERT INTO bio(user_name, email, date_birth, picture, date_registration) VALUES ('jpmramos', 'jpmramos@academiadecodigo.org', date(now()), 'jpmramospic', date(now()));
INSERT INTO bio(user_name, email, date_birth, picture, date_registration) VALUES ('amartins', 'amartins@academiadecodigo.org', date(now()), 'amartinspic', date(now()));
INSERT INTO bio(user_name, email, date_birth, picture, date_registration) VALUES ('ffernandes', 'ffernandes@academiadecodigo.org', date(now()), 'ffernadespic', date(now()));
INSERT INTO bio(user_name, email, date_birth, picture, date_registration) VALUES ('fsantos', 'fsantos@academiadecodigo.org', date(now()), 'fsantospic', date(now()));
