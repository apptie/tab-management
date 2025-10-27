-- 사용자 생성
INSERT INTO users (id, nickname, created_at, updated_at)
VALUES
    (1, '테스트 사용자1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 탭 그룹 생성
INSERT INTO tab_groups (id, creator_id, name, created_at, updated_at)
VALUES
    (1, 1, '개발 탭', '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
    (2, 1, '업무 탭', '2024-01-02 11:00:00', '2024-01-02 11:00:00');

-- 탭 데이터 생성
INSERT INTO tabs (id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (1, 1, NULL, 'Spring 공식문서', 'https://spring.io', 0, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
    (2, 1, NULL, 'Java 공식문서', 'https://java.com', 1, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
    (3, 2, NULL, '이메일', 'https://mail.com', 0, '2024-01-02 11:00:00', '2024-01-02 11:00:00');

-- 탭 컨텐츠 생성
INSERT INTO tab_contents (id, tab_id, content, created_at, updated_at)
VALUES
    (1, 1, 'Spring 프레임워크 학습 내용', '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
    (2, 1, 'Spring Boot 실습 내용', '2024-01-01 10:30:00', '2024-01-01 10:30:00'),
    (3, 2, 'Java 기초 문법 정리', '2024-01-01 11:00:00', '2024-01-01 11:00:00');

-- ID SEQUENCE 초기화
ALTER TABLE tab_groups ALTER COLUMN id RESTART WITH 3;
ALTER TABLE tabs ALTER COLUMN id RESTART WITH 4;
ALTER TABLE tab_contents ALTER COLUMN id RESTART WITH 4;
