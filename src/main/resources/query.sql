-- use https://www.devglan.com/online-tools/bcrypt-hash-generator for generating password
INSERT INTO user (id, username, password) VALUES (1, 'user1', '$2a$04$Ye7/lJoJin6.m9sOJZ9ujeTgHEVM4VXgI2Ingpsnf9gXyXEXf/IlW');
INSERT INTO user (id, username, password) VALUES (2, 'user2', '$2a$04$StghL1FYVyZLdi8/DIkAF./2rz61uiYPI3.MaAph5hUq03XKeflyW');
INSERT INTO user (id, username, password) VALUES (3, 'user3', '$2a$04$Lk4zqXHrHd82w5/tiMy8ru9RpAXhvFfmHOuqTmFPWQcUhBD8SSJ6W');
INSERT INTO user (id, username, password) VALUES (4, 'demo', '$2a$04$LwfYcQrBAYJVnGjfHXw7Ee6zKt3R6jjLJ/Vu2Fcr9ophrEivkoyPK');

INSERT INTO role (id, description, name) VALUES (1, 'Admin role', 'ROLE_ADMIN');
INSERT INTO role (id, description, name) VALUES (2, 'User role', 'ROLE_USER');
INSERT INTO role (id, description, name) VALUES (3, 'Demo', 'ROLE_DEMO');

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (4, 3);
