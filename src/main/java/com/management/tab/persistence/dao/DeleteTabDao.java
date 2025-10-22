package com.management.tab.persistence.dao;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DeleteTabDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DeleteTabDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public void deleteTabOnly(Long tabId, Long currentParentId) {
        List<Long> childrenIds = findDirectChildren(tabId);

        if (!childrenIds.isEmpty()) {
            deleteOldPaths(tabId, childrenIds);

            if (currentParentId != null) {
                insertNewPaths(currentParentId, childrenIds);
            }

            updateChildrenParent(currentParentId, childrenIds);
        }

        deleteTab(tabId);
    }

    private List<Long> findDirectChildren(Long tabId) {
        String sql = """
                SELECT descendant_id
                FROM tab_tree_paths
                WHERE ancestor_id = :ancestorId AND depth = 1
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ancestorId", tabId);

        return jdbcTemplate.queryForList(sql, params, Long.class);
    }

    private void deleteOldPaths(Long tabId, List<Long> childrenIds) {
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

    private void insertNewPaths(Long currentParentId, List<Long> childrenIds) {
        String sql = """
                INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
                SELECT p.ancestor_id, c.descendant_id, p.depth + c.depth + 1
                FROM tab_tree_paths p
                CROSS JOIN tab_tree_paths c
                WHERE p.descendant_id = :currentParentId AND c.ancestor_id IN (:childrenIds)
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("currentParentId", currentParentId)
                .addValue("childrenIds", childrenIds);

        jdbcTemplate.update(sql, params);
    }

    private void updateChildrenParent(Long currentParentId, List<Long> childrenIds) {
        String sql = """
                UPDATE tabs
                SET parent_id = :currentParentId
                WHERE id IN (:childrenIds)
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("currentParentId", currentParentId)
                .addValue("childrenIds", childrenIds);

        jdbcTemplate.update(sql, params);
    }

    private void deleteTab(Long tabId) {
        String sql = "DELETE FROM tabs WHERE id = :tabId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tabId", tabId);

        jdbcTemplate.update(sql, params);
    }

    public void deleteTabWithSubtree(Long tabId) {
        String sql = """
                DELETE FROM tabs
                WHERE id IN (
                    SELECT descendant_id
                    FROM tab_tree_paths
                    WHERE ancestor_id = :tabId
                )
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tabId", tabId);

        jdbcTemplate.update(sql, params);
    }
}
