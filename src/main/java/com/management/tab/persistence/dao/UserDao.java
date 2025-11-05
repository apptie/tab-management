package com.management.tab.persistence.dao;

import com.management.tab.persistence.dao.dto.UserDto;
import java.time.LocalDateTime;
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
public class UserDao {

    private static final RowMapper<UserDto> userRowMapper = (rs, rowNum) -> new UserDto(
            rs.getLong("id"),
            rs.getString("nickname"),
            rs.getString("registration_id"),
            rs.getString("social_id"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
    );

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public Optional<UserDto> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = :id";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id);

        try {
            UserDto result = jdbcTemplate.queryForObject(sql, parameters, userRowMapper);

            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalArgumentException("조건에 맞는 행이 두 개 이상입니다.");
        }
    }

    public Optional<UserDto> findBySocialInfo(String registrationId, String socialId) {
        String sql = "SELECT * FROM users WHERE registration_id = :registrationId AND social_id = :socialId";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("registrationId", registrationId)
                .addValue("socialId", socialId);

        try {
            UserDto result = jdbcTemplate.queryForObject(sql, parameters, userRowMapper);

            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalArgumentException("조건에 맞는 행이 두 개 이상입니다.");
        }
    }

    public Long save(
            String nickname,
            String registrationId,
            String socialId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        String sql = """
                INSERT INTO users (nickname, registration_id, social_id, created_at, updated_at)
                VALUES (:nickname, :registrationId, :socialId, :createdAt, :updatedAt)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nickname", nickname)
                .addValue("registrationId", registrationId)
                .addValue("socialId", socialId)
                .addValue("createdAt", createdAt)
                .addValue("updatedAt", updatedAt);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, params, keyHolder);

        return keyHolder.getKeyAs(Long.class);
    }
}
