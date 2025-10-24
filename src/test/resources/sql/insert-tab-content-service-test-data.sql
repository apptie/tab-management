INSERT INTO tab_groups (id, name, created_at, updated_at)
VALUES
    (1, '테스트 그룹1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, '테스트 그룹2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO tabs (id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (1, 1, NULL, '테스트 탭1', 'https://test1.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, NULL, '테스트 탭2', 'https://test2.com', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 2, NULL, '테스트 탭3', 'https://test3.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
VALUES
    (1, 1, 0),
    (2, 2, 0),
    (3, 3, 0);

INSERT INTO tab_contents (id, tab_id, content, created_at, updated_at)
VALUES
    (1, 1, 'Spring 프레임워크 학습 내용', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 'Spring Boot 실습 내용', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 2, 'Java 기초 문법 정리', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

ALTER TABLE tab_groups ALTER COLUMN id RESTART WITH 3;
ALTER TABLE tabs ALTER COLUMN id RESTART WITH 4;
ALTER TABLE tab_contents ALTER COLUMN id RESTART WITH 4;
