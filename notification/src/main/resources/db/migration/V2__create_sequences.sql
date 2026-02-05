-- 1. Create the sequence
-- allocationSize = 50 in Java means increment by 50 in DB
CREATE SEQUENCE notification_schema.user_data_seq
    START WITH 1
    INCREMENT BY 50;

-- 2. Sync the sequence with your existing data
-- This prevents "Duplicate Key" errors on your next insert
SELECT setval(' notification_schema.user_data_seq', COALESCE((SELECT MAX(id) FROM  notification_schema.user_data), 0) + 1, false);


CREATE SEQUENCE notification_schema.user_address_seq
    START WITH 1
    INCREMENT BY 50;

SELECT setval(' notification_schema.user_address_seq', COALESCE((SELECT MAX(id) FROM  notification_schema.user_address), 0) + 1, false);

-- -- 3. (Optional) Remove the default auto-increment from the column
-- -- If you were using SERIAL/IDENTITY before, this cleans up the DB schema
-- ALTER TABLE users ALTER COLUMN id DROP DEFAULT;