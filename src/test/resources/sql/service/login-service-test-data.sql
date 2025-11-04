-- 사용자 생성
INSERT INTO users (id, nickname, registration_id, social_id, created_at, updated_at)
VALUES
    (1, '기존 사용자', 'KAKAO', 'kakao12345', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, '다른 사용자', 'KAKAO', 'kakao54321', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- H2 시퀀스 리셋
ALTER TABLE users ALTER COLUMN id RESTART WITH 3;
