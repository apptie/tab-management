-- 사용자 생성
INSERT INTO users (id, nickname, created_at, updated_at)
VALUES
    (1, '테스트 사용자1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 탭 그룹 생성
INSERT INTO tab_groups (id, creator_id, name, created_at, updated_at)
VALUES (1, 1, '업데이트 테스트 그룹', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 테스트용 트리 구조
-- 구조:
-- 300 (루트A)
--   ├─ 301 (자식A-1)
--   │   ├─ 303 (손자A-1-1)
--   │   └─ 304 (손자A-1-2)
--   └─ 302 (자식A-2)
-- 310 (루트B)
--   └─ 311 (자식B-1)

INSERT INTO tabs (id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    -- 루트 탭
    (300, 1, NULL, '업데이트_루트A', 'http://rootA.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (310, 1, NULL, '업데이트_루트B', 'http://rootB.com', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 루트A 자식
    (301, 1, 300, '업데이트_자식A-1', 'http://childA-1.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (302, 1, 300, '업데이트_자식A-2', 'http://childA-2.com', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 자식A-1 손자
    (303, 1, 301, '업데이트_손자A-1-1', 'http://gcA-1-1.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (304, 1, 301, '업데이트_손자A-1-2', 'http://gcA-1-2.com', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 루트B 자식
    (311, 1, 310, '업데이트_자식B-1', 'http://childB-1.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 300 (루트A)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES (300, 300, 0);

-- 301 (자식A-1)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
    (301, 301, 0),
    (300, 301, 1);

-- 302 (자식A-2)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
    (302, 302, 0),
    (300, 302, 1);

-- 303 (손자A-1-1)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
    (303, 303, 0),
    (301, 303, 1),
    (300, 303, 2);

-- 304 (손자A-1-2)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
    (304, 304, 0),
    (301, 304, 1),
    (300, 304, 2);

-- 310 (루트B)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES (310, 310, 0);

-- 311 (자식B-1)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
    (311, 311, 0),
    (310, 311, 1);
