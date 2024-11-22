--liquibase formatted sql
--changeset MazurovY:2

--For GenerationType.SEQUENCE
/*
CREATE TABLE PUBLIC.USERS
(
    user_id_pk integer PRIMARY KEY,
    login      text,
    password   text
);
 */


--For GenerationType.IDENTITY
CREATE TABLE PUBLIC.USERS
(
    user_id_pk BIGSERIAL PRIMARY KEY,
    login      text,
    password   text
);