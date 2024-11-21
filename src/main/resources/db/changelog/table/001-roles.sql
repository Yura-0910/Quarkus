--liquibase formatted sql
--changeset MazurovY:1

--For GenerationType.SEQUENCE
/*
CREATE TABLE PUBLIC.ROLES
(
    role_id_pk integer PRIMARY KEY,
    role       text
);
 */

--For GenerationType.IDENTITY
CREATE TABLE PUBLIC.ROLES
(
    role_id_pk BIGSERIAL PRIMARY KEY,
    role       text
);