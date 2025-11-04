-- 사용자 생성
INSERT INTO users (id, nickname, created_at, updated_at)
VALUES
    (1, '테스트 사용자1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 탭 그룹 생성
INSERT INTO tab_groups (id, name, writer_id, created_at, updated_at)
VALUES
    (1, '테스트 그룹 1', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, '테스트 그룹 2', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, '테스트 그룹 3', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 지삭 탭 테스트 관련 더미 데이터
INSERT INTO tabs (id, writer_id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (100, 1, 1, NULL, '테스트용 부모 탭', 'http://parent.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 자기 자신과의 경로 생성
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
VALUES
    (100, 100, 0);
