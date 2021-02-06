-- Run this MySQL script before running the project for first time
-- WARNING !!! Before running this script check if 'hotifi_db' exists or not. If exists backup the data, and then run this script

DROP DATABASE IF EXISTS hotifi_db;

CREATE DATABASE hotifi_db ;

USE hotifi_db;

-- After running above queries run the server to create MySql tables for first time and execute below queries sequentially

INSERT INTO `role`
(`description`,`name`,`created_at`)
VALUES
    ('hotifi customer','CUSTOMER',CURRENT_TIMESTAMP),
    ('hotifi administrator','ADMINISTRATOR',CURRENT_TIMESTAMP);

INSERT INTO authentication (`email`, `password`, `created_at`, `modified_at`, `is_activated`, `is_banned`, `is_freezed`, `is_deleted`, `is_email_verified`, `is_phone_verified`)
VALUES('suraj@gmail.com', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0, 0, 0, 1, 1);

INSERT INTO authentication_roles (role_id, authentication_id) values (2, 1);
    