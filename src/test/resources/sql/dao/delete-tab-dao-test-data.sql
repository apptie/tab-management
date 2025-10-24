-- 탭 그룹 생성
INSERT INTO tab_groups (id, name, created_at, updated_at)
VALUES
    (1, '삭제 테스트 그룹', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 테스트용 트리 구조
-- 구조:
-- 200 (루트)
--   ├─ 201 (자식1)
--   │   ├─ 203 (손자1)
--   │   └─ 204 (손자2)
--   └─ 202 (자식2)
--       └─ 205 (손자3)

-- 루트 탭
INSERT INTO tabs (id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES (200, 1, NULL, '삭제테스트_루트', 'http://root.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 자식 탭
INSERT INTO tabs (id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (201, 1, 200, '삭제테스트_자식1', 'http://child1.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (202, 1, 200, '삭제테스트_자식2', 'http://child2.com', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 손자 탭
INSERT INTO tabs (id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (203, 1, 201, '삭제테스트_손자1', 'http://gc1.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (204, 1, 201, '삭제테스트_손자2', 'http://gc2.com', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (205, 1, 202, '삭제테스트_손자3', 'http://gc3.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 200 (루트)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES (200, 200, 0);

-- 201 (자식1)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
    (201, 201, 0),
    (200, 201, 1);

-- 202 (자식2)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
    (202, 202, 0),
    (200, 202, 1);

-- 203 (손자1)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
    (203, 203, 0),
    (201, 203, 1),
    (200, 203, 2);

-- 204 (손자2)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
    (204, 204, 0),
    (201, 204, 1),
    (200, 204, 2);

-- 205 (손자3)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
    (205, 205, 0),
    (202, 205, 1),
    (200, 205, 2);
