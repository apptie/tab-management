-- 사용자 생성
INSERT INTO users (id, nickname, created_at, updated_at)
VALUES
    (1, '테스트 사용자1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, '테스트 사용자2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 탭 그룹 생성
INSERT INTO tab_groups (id, creator_id, name, created_at, updated_at)
VALUES
    (1, 1, 'TabService 테스트 그룹', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 테스트용 트리 구조
-- 구조:
-- 100 (루트1, depth 0)
--   ├─ 101 (자식1, depth 1)
--   │   ├─ 103 (손자1, depth 2)
--   │   └─ 104 (손자2, depth 2)
--   └─ 102 (자식2, depth 1)
--       └─ 105 (손자3, depth 2)
--           └─ 106 (증손자1, depth 3)
--               └─ 107 (고손자1, depth 4)
--                   └─ 108 (5대손, depth 5)
--                       └─ 109 (6대손, depth 6)
--                           └─ 110 (7대손, depth 7)
--                               └─ 111 (8대손, depth 8)
--                                   └─ 112 (9대손, depth 9)
-- 200 (루트2, depth 0)
-- 300 (루트3, depth 0)

-- 루트 탭
INSERT INTO tabs (id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (100, 1, NULL, '루트_탭1', 'https://root1.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (200, 1, NULL, '루트_탭2', 'https://root2.com', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (300, 1, NULL, '루트_탭3', 'https://root3.com', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- depth 1
INSERT INTO tabs (id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (101, 1, 100, '자식_탭1', 'https://child1.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (102, 1, 100, '자식_탭2', 'https://child2.com', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- depth 2
INSERT INTO tabs (id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (103, 1, 101, '손자_탭1', 'https://grandchild1.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (104, 1, 101, '손자_탭2', 'https://grandchild2.com', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (105, 1, 102, '손자_탭3', 'https://grandchild3.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- depth 3-9 (깊이 테스트용)
INSERT INTO tabs (id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (106, 1, 105, '증손자_탭1', 'https://depth3.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (107, 1, 106, '고손자_탭1', 'https://depth4.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (108, 1, 107, 'depth5_탭', 'https://depth5.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (109, 1, 108, 'depth6_탭', 'https://depth6.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (110, 1, 109, 'depth7_탭', 'https://depth7.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (111, 1, 110, 'depth8_탭', 'https://depth8.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (112, 1, 111, 'depth9_탭', 'https://depth9.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Closure Table 경로 생성
-- 100 (루트1)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
VALUES
    (100, 100, 0);

-- 200 (루트2)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
VALUES
    (200, 200, 0);

-- 300 (루트3)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
VALUES
    (300, 300, 0);

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

-- 106-112 (depth 3-9)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
VALUES
    (106, 106, 0), (105, 106, 1), (102, 106, 2), (100, 106, 3),
    (107, 107, 0), (106, 107, 1), (105, 107, 2), (102, 107, 3), (100, 107, 4),
    (108, 108, 0), (107, 108, 1), (106, 108, 2), (105, 108, 3), (102, 108, 4), (100, 108, 5),
    (109, 109, 0), (108, 109, 1), (107, 109, 2), (106, 109, 3), (105, 109, 4), (102, 109, 5), (100, 109, 6),
    (110, 110, 0), (109, 110, 1), (108, 110, 2), (107, 110, 3), (106, 110, 4), (105, 110, 5), (102, 110, 6), (100, 110, 7),
    (111, 111, 0), (110, 111, 1), (109, 111, 2), (108, 111, 3), (107, 111, 4), (106, 111, 5), (105, 111, 6), (102, 111, 7), (100, 111, 8),
    (112, 112, 0), (111, 112, 1), (110, 112, 2), (109, 112, 3), (108, 112, 4), (107, 112, 5), (106, 112, 6), (105, 112, 7), (102, 112, 8), (100, 112, 9);

-- H2 시퀀스 리셋
ALTER TABLE tab_groups ALTER COLUMN id RESTART WITH 2;
ALTER TABLE tabs ALTER COLUMN id RESTART WITH 301;
