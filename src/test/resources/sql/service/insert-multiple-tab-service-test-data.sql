-- Users
INSERT INTO users (id, nickname, registration_id, social_id, created_at, updated_at) VALUES
(1, 'test_user', 'test_registration', 'test_social_id', NOW(), NOW()),
(2, 'another_user', 'another_registration', 'another_social_id', NOW(), NOW());

-- Tab Groups
INSERT INTO tab_groups (id, name, writer_id, created_at, updated_at) VALUES
(1, 'Test Group', 1, NOW(), NOW()),
(2, 'Another Group', 2, NOW(), NOW());

-- Root Tabs (position 0, 1, 2, 3)
INSERT INTO tabs (id, writer_id, group_id, parent_id, title, url, position, created_at, updated_at) VALUES
(100, 1, 1, NULL, 'Root Tab 1', 'https://root1.com', 0, NOW(), NOW()),
(200, 1, 1, NULL, 'Root Tab 2', 'https://root2.com', 1, NOW(), NOW()),
(300, 1, 1, NULL, 'Root Tab 3', 'https://root3.com', 2, NOW(), NOW()),
(400, 1, 1, NULL, 'Root Tab 4', 'https://root4.com', 3, NOW(), NOW());

-- Child Tabs of 100 (depth 1)
INSERT INTO tabs (id, writer_id, group_id, parent_id, title, url, position, created_at, updated_at) VALUES
(101, 1, 1, 100, 'Child Tab 101', 'https://child101.com', 0, NOW(), NOW()),
(102, 1, 1, 100, 'Child Tab 102', 'https://child102.com', 1, NOW(), NOW());

-- Child Tabs of 200 (depth 1)
INSERT INTO tabs (id, writer_id, group_id, parent_id, title, url, position, created_at, updated_at) VALUES
(201, 1, 1, 200, 'Child Tab 201', 'https://child201.com', 0, NOW(), NOW()),
(202, 1, 1, 200, 'Child Tab 202', 'https://child202.com', 1, NOW(), NOW());

-- Grandchild Tabs of 101 (depth 2)
INSERT INTO tabs (id, writer_id, group_id, parent_id, title, url, position, created_at, updated_at) VALUES
(1001, 1, 1, 101, 'Grandchild Tab 1001', 'https://grandchild1001.com', 0, NOW(), NOW()),
(1002, 1, 1, 101, 'Grandchild Tab 1002', 'https://grandchild1002.com', 1, NOW(), NOW());

-- Tree Paths for Tabs
-- Root tabs (self-reference only)
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
(100, 100, 0),
(200, 200, 0),
(300, 300, 0),
(400, 400, 0);

-- Child tabs of 100
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
(100, 101, 1),
(101, 101, 0),
(100, 102, 1),
(102, 102, 0);

-- Grandchild tabs of 101
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
(100, 1001, 2),
(101, 1001, 1),
(1001, 1001, 0),
(100, 1002, 2),
(101, 1002, 1),
(1002, 1002, 0);

-- Child tabs of 200
INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth) VALUES
(200, 201, 1),
(201, 201, 0),
(200, 202, 1),
(202, 202, 0);
