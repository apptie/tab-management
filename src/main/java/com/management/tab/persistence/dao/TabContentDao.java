package com.management.tab.persistence.dao;

import com.management.tab.persistence.dao.dto.TabContentDto;
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
public class TabContentDao {

    private static final RowMapper<TabContentDto> tabContentRowMapper = (rs, rowNum) -> new TabContentDto(
            rs.getLong("id"),
            rs.getLong("tab_id"),
            rs.getString("content"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
    );

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TabContentDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public Optional<TabContentDto> findById(Long id) {
        String sql = "SELECT * FROM tab_contents WHERE id = :id";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id);

        try {
            TabContentDto result = jdbcTemplate.queryForObject(sql, parameters, tabContentRowMapper);

            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalArgumentException("조건에 맞는 행이 두 개 이상입니다.");
        }
    }

    public List<TabContentDto> findAllByTabId(Long tabId) {
        String sql = "SELECT * FROM tab_contents WHERE tab_id = :tabId ORDER BY id";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("tabId", tabId);

        return jdbcTemplate.query(sql, parameters, tabContentRowMapper);
    }

    public Long save(Long tabId, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        String sql = "INSERT INTO tab_contents (tab_id, content, created_at, updated_at) " +
                "VALUES (:tabId, :content, :createdAt, :updatedAt)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tabId", tabId)
                .addValue("content", content)
                .addValue("createdAt", createdAt)
                .addValue("updatedAt", updatedAt);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, params, keyHolder);

        return keyHolder.getKeyAs(Long.class);
    }

    public void update(Long id, String content, LocalDateTime updatedAt) {
        String sql = "UPDATE tab_contents SET content = :content, updated_at = :updatedAt WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("content", content)
                .addValue("updatedAt", updatedAt)
                .addValue("id", id);

        jdbcTemplate.update(sql, params);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM tab_contents WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        jdbcTemplate.update(sql, params);
    }

    public void deleteAllByTabId(Long tabId) {
        String sql = "DELETE FROM tab_contents WHERE tab_id = :tabId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tabId", tabId);

        jdbcTemplate.update(sql, params);
    }

    public int countByTabId(Long tabId) {
        String sql = "SELECT COUNT(*) FROM tab_contents WHERE tab_id = :tabId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tabId", tabId);

        try {
            Integer result = jdbcTemplate.queryForObject(sql, params, Integer.class);

            return result == null ? 0 : result;
        } catch (EmptyResultDataAccessException ignored) {
            return 0;
        }
    }
}
