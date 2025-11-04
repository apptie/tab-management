INSERT INTO users (id, nickname, created_at, updated_at)
VALUES
    (1, '개발자', '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
    (2, '디자이너', '2024-01-02 11:00:00', '2024-01-02 11:00:00');

ALTER TABLE users ALTER COLUMN id RESTART WITH 3;
