INSERT INTO app_user (id, username, password, is_enabled, is_account_non_expired, is_account_non_locked,
                      is_credentials_non_expired, user_role_id)
VALUES (1, 'Linda', 'password', TRUE, TRUE, TRUE, TRUE, 1);

INSERT INTO user_role_granted_authorities (owner_id, granted_authority)
VALUES (1, 'oauth:login');

INSERT INTO user_role (id, name)
VALUES (1, 'OAUTHUSER');