-- 사용자 생성
INSERT INTO users (id, nickname, registration_id, social_id, created_at, updated_at)
VALUES
    (1, '테스트 사용자1', 'KAKAO', 'kakako12345', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 탭 그룹 생성
INSERT INTO tab_groups (id, name, writer_id, created_at, updated_at)
VALUES
    (1, '조회 테스트 그룹 1', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, '조회 테스트 그룹 2', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 테스트용 트리 구조 (그룹 1)
-- 구조:
-- 100 (루트1)
--   ├─ 101 (자식1-1) position=0
--   └─ 102 (자식1-2) position=1
--       └─ 103 (손자1-1) position=0
-- 104 (루트2) position=1

INSERT INTO tabs (id, writer_id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (100, 1, 1, NULL, '조회테스트_루트1', 'http://root1.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (101, 1, 1, 100, '조회테스트_자식1-1', 'http://child1-1.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (102, 1, 1, 100, '조회테스트_자식1-2', 'http://child1-2.com', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (103, 1, 1, 102, '조회테스트_손자1-1', 'http://gc1-1.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (104, 1, 1, NULL, '조회테스트_루트2', 'http://root2.com', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 테스트용 트리 구조 (그룹 2)
INSERT INTO tabs (id, writer_id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (200, 1, 2, NULL, '조회테스트_그룹2_루트', 'http://g2root.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 100 (루트1)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES (100, 100, 0);

-- 101 (자식1-1)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
    (101, 101, 0),
    (100, 101, 1);

-- 102 (자식1-2)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
    (102, 102, 0),
    (100, 102, 1);

-- 103 (손자1-1)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
    (103, 103, 0),
    (102, 103, 1),
    (100, 103, 2);

-- 104 (루트2)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES (104, 104, 0);

-- 200 (그룹2 루트)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES (200, 200, 0);
