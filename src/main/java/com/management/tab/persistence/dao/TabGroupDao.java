package com.management.tab.persistence.dao;

import com.management.tab.persistence.dao.dto.TabGroupDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class TabGroupDao {

    private static final RowMapper<TabGroupDto> tabGroupRowMapper = (rs, rowNum) -> new TabGroupDto(
            rs.getLong("id"),
            rs.getLong("writer_id"),
            rs.getString("name"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
    );

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TabGroupDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public List<TabGroupDto> findAll() {
        String sql = "SELECT * FROM tab_groups ORDER BY id";

        return jdbcTemplate.query(sql, tabGroupRowMapper);
    }

    public Optional<TabGroupDto> findById(Long id) {
        String sql = "SELECT * FROM tab_groups WHERE id = :id";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id);

        try {
            TabGroupDto result = jdbcTemplate.queryForObject(sql, parameters, tabGroupRowMapper);

            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalArgumentException("조건에 맞는 행이 두 개 이상입니다.");
        }
    }

    public Long save(String name, Long writerId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        String sql = """
                INSERT INTO tab_groups (name, writer_id, created_at, updated_at)
                VALUES (:name, :writerId, :createdAt, :updatedAt)
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("writerId", writerId)
                .addValue("createdAt", createdAt)
                .addValue("updatedAt", updatedAt);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, params, keyHolder);

        return keyHolder.getKeyAs(Long.class);
    }

    public void update(Long id, String name) {
        String sql = "UPDATE tab_groups SET name = :name WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("id", id);

        jdbcTemplate.update(sql, params);
    }

    public void delete(Long id) {
        String deleteTabsSql = "DELETE FROM tabs WHERE group_id = :groupId";
        MapSqlParameterSource deleteTabsParams = new MapSqlParameterSource()
                .addValue("groupId", id);
        jdbcTemplate.update(deleteTabsSql, deleteTabsParams);

        String deleteGroupSql = "DELETE FROM tab_groups WHERE id = :id";
        MapSqlParameterSource deleteGroupParams = new MapSqlParameterSource()
                .addValue("id", id);
        jdbcTemplate.update(deleteGroupSql, deleteGroupParams);
    }

    public int countTabs(Long groupId) {
        String sql = "SELECT COUNT(*) FROM tabs WHERE group_id = :groupId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("groupId", groupId);

        try {
            Integer result = jdbcTemplate.queryForObject(sql, params, Integer.class);

            return result == null ? 0 : result;
        } catch (EmptyResultDataAccessException ignored) {
            return 0;
        }
    }
}
