package com.management.tab.persistence.dao;

import com.management.tab.persistence.dao.dto.TabDto;
import com.management.tab.persistence.dao.dto.TabWithDepthDto;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SelectTabDao {

    private static final RowMapper<TabDto> tabRowMapper = (rs, rowNum) -> new TabDto(
            rs.getLong("id"),
            rs.getLong("group_id"),
            rs.getLong("parent_id"),
            rs.getString("title"),
            rs.getString("url"),
            rs.getInt("position"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
    );

    private static final RowMapper<TabWithDepthDto> tabWithDepthRowMapper = (rs, rowNum) -> new TabWithDepthDto(
            rs.getLong("id"),
            rs.getLong("group_id"),
            rs.getObject("parent_id", Long.class),
            rs.getString("title"),
            rs.getString("url"),
            rs.getInt("position"),
            rs.getInt("depth"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
    );

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SelectTabDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public Optional<TabDto> findById(Long tabId) {
        String sql = "SELECT * FROM tabs WHERE id = :tabId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tabId", tabId);

        try {
            TabDto result = jdbcTemplate.queryForObject(sql, params, tabRowMapper);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalArgumentException("조건에 맞는 행이 두 개 이상입니다.");
        }
    }

    public List<TabWithDepthDto> findTreeByGroup(Long groupId) {
        String sql = """
                    SELECT DISTINCT t.id, t.group_id, t.parent_id, t.title, t.url, t.position,
                           t.created_at, t.updated_at,
                           (SELECT MAX(depth)
                            FROM tab_tree_paths
                            WHERE descendant_id = t.id) AS depth
                    FROM tabs t
                    WHERE t.group_id = :groupId
                    ORDER BY depth, t.position
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("groupId", groupId);

        return jdbcTemplate.query(sql, parameters, tabWithDepthRowMapper);
    }

    public Optional<Long> findParentId(Long tabId) {
        String sql = "SELECT parent_id FROM tabs WHERE id = :tabId";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("tabId", tabId);

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, parameters, Long.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<TabDto> findSiblings(Long parentId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if (parentId == null) {
            String sql = "SELECT * FROM tabs WHERE parent_id IS NULL ORDER BY position";

            return jdbcTemplate.query(sql, parameters, tabRowMapper);
        }

        String sql = "SELECT * FROM tabs WHERE parent_id = :parentId ORDER BY position";

        parameters.addValue("parentId", parentId);
        return jdbcTemplate.query(sql, parameters, tabRowMapper);
    }

    public int findTabLastPosition(Long groupId, Long parentId) {
        String sql = """
                SELECT MAX(position)
                FROM tabs
                WHERE group_id = :groupId AND parent_id = :parentId
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("groupId", groupId)
                .addValue("parentId", parentId);
        Integer lastPosition = jdbcTemplate.queryForObject(sql, params, Integer.class);

        return lastPosition != null ? lastPosition : 0;
    }

    public int findRootTabLastPosition(Long groupId) {
        String sql = """
                SELECT MAX(position)
                FROM tabs
                WHERE group_id = :groupId AND parent_id IS NULL
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("groupId", groupId);
        Integer lastPosition = jdbcTemplate.queryForObject(sql, params, Integer.class);

        return lastPosition != null ? lastPosition : 0;
    }
}
