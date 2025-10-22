package com.management.tab.persistence.dao;

import java.time.LocalDateTime;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class InsertTabDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public InsertTabDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public Long saveRootTab(
            Long groupId,
            String title,
            String url,
            int position,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        Long tabId = saveTab(groupId, title, url, null, position, createdAt, updatedAt);

        insertSelfPath(tabId);
        return tabId;
    }

    private void insertSelfPath(Long tabId) {
        String sql = """
                INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
                VALUES (:tabId, :tabId, 0)
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tabId", tabId);

        jdbcTemplate.update(sql, params);
    }

    private void insertParentPaths(Long newTabId, Long parentId) {
        String sql = """
                INSERT INTO tab_tree_paths (ancestor_id, descendant_id, depth)
                SELECT ancestor_id, :newTabId, depth + 1
                FROM tab_tree_paths
                WHERE descendant_id = :parentId
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("newTabId", newTabId)
                .addValue("parentId", parentId);

        jdbcTemplate.update(sql, params);
    }

    private Long saveTab(
            Long groupId,
            String title,
            String url,
            Long parentId,
            int position,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        String sql = """
                INSERT INTO tabs (group_id, parent_id, title, url, position, created_at, updated_at)
                VALUES (:groupId, :parentId, :title, :url, :position, :createdAt, :updatedAt)
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("groupId", groupId)
                .addValue("parentId", parentId)
                .addValue("title", title)
                .addValue("url", url)
                .addValue("position", position)
                .addValue("createdAt", createdAt)
                .addValue("updatedAt", updatedAt);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, parameters, keyHolder, new String[]{"id"});
        return keyHolder.getKeyAs(Long.class);
    }

    public Long saveChildTab(
            Long groupId,
            String title,
            String url,
            Long parentId,
            int position,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        Long childId = saveTab(groupId, title, url, parentId, position, createdAt, updatedAt);

        insertParentPaths(childId, parentId);
        insertSelfPath(childId);
        return childId;
    }
}
