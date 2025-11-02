-- 사용자 생성
INSERT INTO users (id, nickname, registration_id, social_id, created_at, updated_at)
VALUES
    (1, '테스트 사용자1', 'KAKAO', 'kakako12345', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 탭 그룹 생성
INSERT INTO tab_groups (id, name, writer_id, created_at, updated_at)
VALUES
    (1, 'JdbcTabRepository 테스트 그룹', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 테스트용 트리 구조
-- 구조:
-- 100 (루트)
--   ├─ 101 (자식1)
--   │   ├─ 103 (손자1)
--   │   └─ 104 (손자2)
--   └─ 102 (자식2)
--       └─ 105 (손자3)

-- 루트 탭
INSERT INTO tabs (id, writer_id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (100, 1, 1, NULL, '루트_탭1', 'https://root1.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 자식 탭
INSERT INTO tabs (id, writer_id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (101, 1, 1, 100, '자식_탭1', 'https://child1.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (102, 1, 1, 100, '자식_탭2', 'https://child2.com', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 손자 탭
INSERT INTO tabs (id, writer_id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (103, 1, 1, 101, '손자_탭1', 'https://grandchild1.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (104, 1, 1, 101, '손자_탭2', 'https://grandchild2.com', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (105, 1, 1, 102, '손자_탭3', 'https://grandchild3.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Closure Table 경로 생성
-- 100 (루트)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
VALUES
    (100, 100, 0);

-- 101 (자식1)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
VALUES
    (101, 101, 0),
    (100, 101, 1);

-- 102 (자식2)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
VALUES
    (102, 102, 0),
    (100, 102, 1);

-- 103 (손자1)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
VALUES
    (103, 103, 0),
    (101, 103, 1),
    (100, 103, 2);

-- 104 (손자2)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
VALUES
    (104, 104, 0),
    (101, 104, 1),
    (100, 104, 2);

-- 105 (손자3)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
VALUES
    (105, 105, 0),
    (102, 105, 1),
    (100, 105, 2);

-- H2 시퀀스 리셋
ALTER TABLE tab_groups ALTER COLUMN id RESTART WITH 2;
ALTER TABLE tabs ALTER COLUMN id RESTART WITH 106;
