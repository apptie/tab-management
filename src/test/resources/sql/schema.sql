DROP TABLE IF EXISTS tab_contents;
DROP TABLE IF EXISTS tab_tree_paths;
DROP TABLE IF EXISTS tabs;
DROP TABLE IF EXISTS tab_groups;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nickname VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE tab_groups (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    creator_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_tab_groups_creator FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE tabs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id BIGINT NOT NULL,
    parent_id BIGINT,
    title VARCHAR(500) NOT NULL,
    url TEXT,
    position INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_tabs_group FOREIGN KEY (group_id) REFERENCES tab_groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_tabs_parent FOREIGN KEY (parent_id) REFERENCES tabs(id) ON DELETE CASCADE
);

CREATE TABLE tab_tree_paths (
    ancestor_id BIGINT NOT NULL,
    descendant_id BIGINT NOT NULL,
    depth INT NOT NULL,

    PRIMARY KEY (ancestor_id, descendant_id),
    CONSTRAINT fk_paths_ancestor FOREIGN KEY (ancestor_id) REFERENCES tabs(id) ON DELETE CASCADE,
    CONSTRAINT fk_paths_descendant FOREIGN KEY (descendant_id) REFERENCES tabs(id) ON DELETE CASCADE
);

CREATE TABLE tab_contents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tab_id BIGINT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_tab_contents_tab FOREIGN KEY (tab_id) REFERENCES tabs(id) ON DELETE CASCADE
);
