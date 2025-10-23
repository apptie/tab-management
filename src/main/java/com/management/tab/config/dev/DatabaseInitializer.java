package com.management.tab.config.dev;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DatabaseInitializer {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        try {
            executeSqlFile("sql/schema.sql");
        } catch (Exception e) {
            throw new IllegalStateException("데이터베이스 초기화 중 오류 발생", e);
        }
    }

    private void executeSqlFile(String filePath) {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);

            String sql = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            String[] statements = sql.split(";");

            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                    jdbcTemplate.execute(trimmed);
                }
            }

        } catch (Exception e) {
            throw new IllegalStateException(filePath + " 실행 중 오류 발생", e);
        }
    }
}
