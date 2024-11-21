--liquibase formatted sql
--changeset MazurovY:1

--For the case when the "id" is generated automatically via "roles_seq" Sequence
/*
INSERT INTO PUBLIC.ROLES (role_id_pk, role) VALUES (nextval('PUBLIC.roles_seq'),'USER');
INSERT INTO PUBLIC.ROLES (role_id_pk, role) VALUES (nextval('PUBLIC.roles_seq'),'MODERATOR');
INSERT INTO PUBLIC.ROLES (role_id_pk, role) VALUES (nextval('PUBLIC.roles_seq'), 'ADMIN');
 */

--For the case when the "id" is generated automatically via "BIGSERIAL"
INSERT INTO PUBLIC.ROLES (role) VALUES ('USER');
INSERT INTO PUBLIC.ROLES (role) VALUES ('MODERATOR');
INSERT INTO PUBLIC.ROLES (role) VALUES ('ADMIN');