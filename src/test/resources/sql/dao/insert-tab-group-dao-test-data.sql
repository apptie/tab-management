-- 사용자 생성
INSERT INTO users (id, nickname, registration_id, social_id, created_at, updated_at)
VALUES
    (1, '테스트 사용자1', 'KAKAO', 'kakako12345', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 탭 그룹 생성
INSERT INTO tab_groups (id, name, writer_id, created_at, updated_at)
VALUES
    (1, '개발 탭', 1, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
    (2, '업무 탭', 1, '2024-01-02 11:00:00', '2024-01-02 11:00:00'),
    (3, '학습 탭', 1, '2024-01-03 12:00:00', '2024-01-03 12:00:00');

-- 탭 데이터 생성
-- 그룹1: 2개 탭, 그룹2: 1개 탭, 그룹3: 탭 없음
INSERT INTO tabs (id, writer_id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (1, 1, 1, NULL, 'Spring 공식문서', 'https://spring.io', 0, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
    (2, 1, 1, NULL, 'Java 공식문서', 'https://java.com', 1, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
    (3, 1, 2, NULL, '이메일', 'https://mail.com', 0, '2024-01-02 11:00:00', '2024-01-02 11:00:00');

-- H2 시퀀스 리셋
ALTER TABLE tab_groups ALTER COLUMN id RESTART WITH 4;
ALTER TABLE tabs ALTER COLUMN id RESTART WITH 4;
