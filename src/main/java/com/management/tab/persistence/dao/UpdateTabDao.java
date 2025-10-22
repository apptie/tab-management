package com.management.tab.persistence.dao;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UpdateTabDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UpdateTabDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public void updateMovingTabOnly(Long tabId, Long currentParentId, Long newParentId) {
        List<Long> childrenIds = findDirectChildren(tabId);

        if (!childrenIds.isEmpty()) {
            reconnectChildrenToParent(tabId, currentParentId, childrenIds);
        }

        moveTabToNewParent(tabId, newParentId);
    }

    private List<Long> findDirectChildren(Long tabId) {
        String sql = """
                SELECT descendant_id
                FROM tab_tree_paths
                WHERE ancestor_id = :tabId AND depth = 1
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tabId", tabId);

        return jdbcTemplate.queryForList(sql, params, Long.class);
    }

    private void reconnectChildrenToParent(Long tabId, Long currentParentId, List<Long> childrenIds) {
        deleteChildrenOldPaths(tabId, childrenIds);

        if (currentParentId != null) {
            insertChildrenNewPaths(currentParentId, childrenIds);
        }

        updateChildrenParent(currentParentId, childrenIds);
    }

    private void deleteChildrenOldPaths(Long tabId, List<Long> childrenIds) {
        String sql = """
                DELETE FROM tab_tree_paths
                WHERE (ancestor_id, descendant_id) IN (
                    SELECT p.ancestor_id, c.descendant_id
                    FROM tab_tree_paths p
                    CROSS JOIN tab_tree_paths c
                    WHERE p.descendant_id = :tabId AND c.ancestor_id IN (:childrenIds)
                )
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tabId", tabId)
                .addValue("childrenIds", childrenIds);

        jdbcTemplate.update(sql, params);
    }

    private void insertChildrenNewPaths(Long currentParentId, List<Long> childrenIds) {
        String sql = """
                INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
                SELECT p.ancestor_id, c.descendant_id, p.depth + c.depth + 1
                FROM tab_tree_paths p
                CROSS JOIN tab_tree_paths c
                WHERE p.descendant_id = :parentId AND c.ancestor_id IN (:childrenIds)
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("parentId", currentParentId)
                .addValue("childrenIds", childrenIds);

        jdbcTemplate.update(sql, params);
    }

    private void updateChildrenParent(Long parentId, List<Long> childrenIds) {
        String sql = """
                UPDATE tabs
                SET parent_id = :parentId
                WHERE id IN (:childrenIds)
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("parentId", parentId)
                .addValue("childrenIds", childrenIds);

        jdbcTemplate.update(sql, params);
    }

    private void moveTabToNewParent(Long tabId, Long newParentId) {
        deleteTabOldPaths(tabId);

        if (newParentId != null) {
            insertTabNewPaths(tabId, newParentId);
        }

        updateTabParent(tabId, newParentId);
    }

    private void deleteTabOldPaths(Long tabId) {
        String sql = """
                DELETE FROM tab_tree_paths
                WHERE descendant_id = :tabId AND ancestor_id != :tabId
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tabId", tabId);

        jdbcTemplate.update(sql, params);
    }

    private void insertTabNewPaths(Long tabId, Long newParentId) {
        String sql = """
                INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
                SELECT ancestor_id, :tabId, depth + 1
                FROM tab_tree_paths
                WHERE descendant_id = :newParentId
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tabId", tabId)
                .addValue("newParentId", newParentId);

        jdbcTemplate.update(sql, params);
    }

    private void updateTabParent(Long tabId, Long newParentId) {
        String sql = """
                UPDATE tabs
                SET parent_id = :parentId, updated_at = CURRENT_TIMESTAMP
                WHERE id = :tabId
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("parentId", newParentId)
                .addValue("tabId", tabId);

        jdbcTemplate.update(sql, params);
    }

    public void updateMovingTabWithSubtree(Long tabId, Long newParentId) {
        deleteOldPathsForSubtree(tabId);

        if (newParentId != null) {
            insertNewPathsForSubtree(tabId, newParentId);
        }

        updateTabParent(tabId, newParentId);
    }

    private void deleteOldPathsForSubtree(Long tabId) {
        String sql = """
            DELETE FROM tab_tree_paths
            WHERE descendant_id IN (
                SELECT d.descendant_id
                FROM (SELECT descendant_id FROM tab_tree_paths
                      WHERE ancestor_id = :tabId) AS d
            )
            AND ancestor_id IN (
                SELECT a.ancestor_id
                FROM (SELECT ancestor_id FROM tab_tree_paths
                      WHERE descendant_id = :tabId
                      AND ancestor_id != :tabId) AS a
            )
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tabId", tabId);

        jdbcTemplate.update(sql, params);
    }

    private void insertNewPathsForSubtree(Long tabId, Long newParentId) {
        String sql = """
            INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
            SELECT p.ancestor_id, c.descendant_id, p.depth + c.depth + 1
            FROM tab_tree_paths p
            CROSS JOIN tab_tree_paths c
            WHERE p.descendant_id = :newParentId
              AND c.ancestor_id = :tabId
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("newParentId", newParentId)
                .addValue("tabId", tabId);

        jdbcTemplate.update(sql, params);
    }

    public void updatePosition(Long tabId, int position) {
        String sql = "UPDATE tabs SET position = :position, updated_at = CURRENT_TIMESTAMP WHERE id = :tabId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("position", position)
                .addValue("tabId", tabId);

        jdbcTemplate.update(sql, params);
    }

    public void updateTab(Long tabId, String title, String url) {
        String sql = """
                UPDATE tabs
                SET title = :title, url = :url, updated_at = CURRENT_TIMESTAMP
                WHERE id = :tabId
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", title)
                .addValue("url", url)
                .addValue("tabId", tabId);

        jdbcTemplate.update(sql, params);
    }

}
