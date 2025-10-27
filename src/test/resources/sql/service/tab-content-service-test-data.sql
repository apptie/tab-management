-- 사용자 생성
INSERT INTO users (id, nickname, created_at, updated_at)
VALUES
    (1, '테스트 사용자1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 탭 그룹 생성
INSERT INTO tab_groups (id, creator_id, name, created_at, updated_at)
VALUES
    (1, 1, '테스트 그룹1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, '테스트 그룹2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 테스트용 트리 구조 (그룹 1)
-- 구조:
-- 1 (루트 탭1)
-- 2 (루트 탭2)

-- 그룹 1 루트 탭
INSERT INTO tabs (id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (1, 1, NULL, '테스트 탭1', 'https://test1.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, NULL, '테스트 탭2', 'https://test2.com', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 테스트용 트리 구조 (그룹 2)
-- 구조:
-- 3 (루트 탭3)

-- 그룹 2 루트 탭
INSERT INTO tabs (id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (3, 2, NULL, '테스트 탭3', 'https://test3.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Closure Table 경로 생성
-- 1 (루트 탭1)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
VALUES
    (1, 1, 0);

-- 2 (루트 탭2)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
VALUES
    (2, 2, 0);

-- 3 (루트 탭3)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
VALUES
    (3, 3, 0);

-- 탭 컨텐츠 생성
-- 탭 1에 대한 컨텐츠 2개
INSERT INTO tab_contents (id, tab_id, content, created_at, updated_at)
VALUES
    (1, 1, 'Spring 프레임워크 학습 내용', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 'Spring Boot 실습 내용', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 탭 2에 대한 컨텐츠 1개
INSERT INTO tab_contents (id, tab_id, content, created_at, updated_at)
VALUES
    (3, 2, 'Java 기초 문법 정리', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- H2 시퀀스 리셋
ALTER TABLE tab_groups ALTER COLUMN id RESTART WITH 3;
ALTER TABLE tabs ALTER COLUMN id RESTART WITH 4;
ALTER TABLE tab_contents ALTER COLUMN id RESTART WITH 4;
