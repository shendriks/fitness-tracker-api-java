INSERT INTO user (authority, created_at, email, password, ulid, updated_at)
VALUES ('ROLE_USER',
        '1970-01-01 00:00:00+00',
        'foo@bar.baz',
        '$2a$10$maFA.4eEqwA2ufjqepOBZe9PExmGjHN1D9QWNvca0NGlLFseCTH86', -- Sup3rS3cr3tPa$$w0rd!
        'USER0000000000000000000000',
        '1970-01-01 00:00:00+00');

-- INSERT INTO application (api_key, accountType, created_at, description, name, ulid, updated_at, developer_id)
-- VALUES ('api-key-1',
--         'BASIC',
--         '1970-01-01 00:00:00+00',
--         'My shiny new application',
--         'My Basic App',
--         'APP00000000000000000000000',
--         '1970-01-01 00:00:00+00',
--         SELECT id FROM user WHERE ulid = 'USER0000000000000000000000'),
--        ('api-key-2',
--         'PREMIUM',
--         '1970-01-01 00:00:00+00',
--         'My shiny new application',
--         'My Premium App',
--         'APP00000000000000000000001',
--         '1970-01-01 00:00:00+00',
--         SELECT id FROM user WHERE ulid = 'USER0000000000000000000000');
-- 
-- INSERT INTO activity (activity_type, calories, created_at, duration, ulid, updated_at, username, application_id)
-- VALUES ('RUNNING',
--         500,
--         '1970-01-01 00:00:00+00',
--         60,
--         'ACTIVITY000000000000000000',
--         '1970-01-01 00:00:00+00',
--         'user-1',
--         SELECT id FROM application WHERE ulid = 'APP00000000000000000000000');
