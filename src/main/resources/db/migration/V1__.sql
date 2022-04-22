CREATE SEQUENCE IF NOT EXISTS app_user_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS user_role_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE app_user
(
    id                         BIGINT       NOT NULL,
    username                   VARCHAR(255) NOT NULL,
    password                   VARCHAR(255) NOT NULL,
    is_enabled                 BOOLEAN      NOT NULL,
    is_account_non_expired     BOOLEAN      NOT NULL,
    is_account_non_locked      BOOLEAN      NOT NULL,
    is_credentials_non_expired BOOLEAN      NOT NULL,
    user_role_id               BIGINT       NOT NULL,
    CONSTRAINT pk_app_user PRIMARY KEY (id)
);

CREATE TABLE user_role
(
    id   BIGINT       NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user_role PRIMARY KEY (id)
);

CREATE TABLE user_role_granted_authorities
(
    owner_id          BIGINT NOT NULL,
    granted_authority VARCHAR(255)
);

ALTER TABLE app_user
    ADD CONSTRAINT FK_APP_USER_ON_USER_ROLE FOREIGN KEY (user_role_id) REFERENCES user_role (id);

ALTER TABLE user_role_granted_authorities
    ADD CONSTRAINT fk_user_role_granted_authorities_on_user_role FOREIGN KEY (owner_id) REFERENCES user_role (id);