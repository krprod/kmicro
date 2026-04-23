CREATE SEQUENCE IF NOT EXISTS user_schema.roles_seq
    START WITH 1
    INCREMENT BY 50;

ALTER TABLE IF EXISTS user_schema.roles
    ALTER COLUMN id SET DEFAULT nextval('user_schema.roles_seq');

SELECT setval(
               'user_schema.roles_seq',
               COALESCE((SELECT MAX(id) FROM user_schema.roles), 1),
               true
       );
