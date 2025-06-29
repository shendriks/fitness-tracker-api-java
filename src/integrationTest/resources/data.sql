INSERT INTO users (authority, created_at, email, password, ulid, updated_at, account_type)
VALUES ('ROLE_USER',
        '1970-01-01 00:00:00+00',
        'foo@bar.baz',
        '$2a$10$maFA.4eEqwA2ufjqepOBZe9PExmGjHN1D9QWNvca0NGlLFseCTH86', -- Sup3rS3cr3tPa$$w0rd!
        'USER0000000000000000000000',
        '1970-01-01 00:00:00+00', 
        'BASIC');

-- INSERT INTO activity (activity_type, calories, created_at, duration, ulid, updated_at, username, application_id)
-- VALUES ('RUNNING',
--         500,
--         '1970-01-01 00:00:00+00',
--         60,
--         'ACTIVITY000000000000000000',
--         '1970-01-01 00:00:00+00',
--         'user-1',
--         SELECT id FROM application WHERE ulid = 'APP00000000000000000000000');
