-- Run this MySQL script before running the project for first time
-- WARNING !!! Before running this script check if 'hotifi_db' exists or not. If exists backup the data, and then run this script

DROP DATABASE IF EXISTS hotifi_db;

CREATE DATABASE hotifi_db;

USE hotifi_db;

SELECT now(); -- Check this for timezones, If UTC execute below commands

SET time_zone = '+5:30'; -- Add this to change timezone from UTC to IST

-- After running above queries run the server to create MySql tables for first time and execute below queries sequentially

INSERT INTO `role`
(`description`,`name`,`created_at`)
VALUES
    ('hotifi customer','CUSTOMER',CURRENT_TIMESTAMP),
    ('hotifi administrator','ADMINISTRATOR',CURRENT_TIMESTAMP);

INSERT INTO authentication (`email`, `password`, `created_at`, `modified_at`, `is_activated`, `is_banned`, `is_freezed`, `is_deleted`, `is_email_verified`, `is_phone_verified`) VALUES
('suraj.admin@hotifi', '$2a$04$ldBBCdBHmjcNpDsL5fa0Te5gzp6to61CvVNtNuVOXqebZ54Pc3afa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0, 0, 0, 1, 1),
('satya.admin@hotifi', '$2a$10$k6K3G4o.FVVKi2jizHWr/.3yZ8ZQquu0xULH4hxCpaWRaAlMB/Brq', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0, 0, 0, 1, 1);

INSERT INTO authentication_roles (role_id, authentication_id)
values (2, 1), (2, 2);
    