-- 사용자 생성
INSERT INTO users (id, nickname, registration_id, social_id, created_at, updated_at)
VALUES
    (1, '테스트 사용자1', 'KAKAO', 'kakako12345', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, '테스트 사용자2', 'KAKAO', 'kakako54321', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 탭 그룹 초기화
INSERT INTO tab_groups (id, name, writer_id, created_at, updated_at)
VALUES
    (1, '테스트 그룹1', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, '테스트 그룹2', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 그룹1에 탭 추가 (countTabs 테스트용)
INSERT INTO tabs (id, writer_id, group_id, parent_id, title, url, position, created_at, updated_at)
VALUES
    (100, 1, 1, NULL, '그룹1_루트탭', 'https://root.com', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Closure Table 경로
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
VALUES
    (100, 100, 0);

-- H2 시퀀스 리셋
ALTER TABLE tab_groups ALTER COLUMN id RESTART WITH 3;
ALTER TABLE tabs ALTER COLUMN id RESTART WITH 101;
