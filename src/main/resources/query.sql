-- use https://www.devglan.com/online-tools/bcrypt-hash-generator for generating password
INSERT INTO user (id, username, password, salary, age) VALUES (1, 'user1', '$2a$04$Ye7/lJoJin6.m9sOJZ9ujeTgHEVM4VXgI2Ingpsnf9gXyXEXf/IlW', 3456, 33);
INSERT INTO user (id, username, password, salary, age) VALUES (2, 'user2', '$2a$04$StghL1FYVyZLdi8/DIkAF./2rz61uiYPI3.MaAph5hUq03XKeflyW', 7823, 23);
INSERT INTO user (id, username, password, salary, age) VALUES (3, 'user3', '$2a$04$Lk4zqXHrHd82w5/tiMy8ru9RpAXhvFfmHOuqTmFPWQcUhBD8SSJ6W', 4234, 45);
INSERT INTO user (id, username, password, salary, age) VALUES (4, 'demo', '$2a$04$LwfYcQrBAYJVnGjfHXw7Ee6zKt3R6jjLJ/Vu2Fcr9ophrEivkoyPK', 9999, 99);

INSERT INTO role (id, description, name) VALUES (1, 'Admin role', 'ADMIN');
INSERT INTO role (id, description, name) VALUES (2, 'User role', 'USER');
INSERT INTO role (id, description, name) VALUES (3, 'Demo', 'DEMO');

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (4, 3);