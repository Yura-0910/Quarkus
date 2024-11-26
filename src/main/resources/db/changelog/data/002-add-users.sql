--liquibase formatted sql
--changeset MazurovY:2

--For the case when the "id" is generated automatically via "users_seq" Sequence
/*
INSERT INTO PUBLIC.USERS (user_id_pk, login, password)
VALUES (nextval('PUBLIC.users_seq'), 'user1', '{noop}pwd1');

INSERT INTO PUBLIC.USERS (user_id_pk, login, password)
VALUES (nextval('PUBLIC.users_seq'), 'user2', '{noop}pwd2');

INSERT INTO PUBLIC.USERS (user_id_pk, login, password)
VALUES (nextval('PUBLIC.users_seq'), 'user3', '{noop}pwd3');
 */

--For the case when the "id" is generated automatically via "BIGSERIAL"
INSERT INTO PUBLIC.USERS (login, email, password, role)
VALUES ('user1', 'email1@ya.ru', 'pwd1', 'User');
INSERT INTO PUBLIC.USERS (login, email, password, role)
VALUES ('user2', 'email2@ya.ru', 'pwd2', 'Moderator');
INSERT INTO PUBLIC.USERS (login, email, password, role)
VALUES ('user3', 'email3@ya.ru', 'pwd3', 'Admin');