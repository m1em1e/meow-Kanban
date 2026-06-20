package com.godotvillage.meowkanban.common.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class DatabaseMigrationConfig {

    @Bean
    public ApplicationRunner userTableMigrationRunner(JdbcTemplate jdbcTemplate) {
        return args -> {
            Set<String> userColumns = jdbcTemplate.queryForList("PRAGMA table_info(mk_user)")
                    .stream()
                    .map(this::getColumnName)
                    .collect(Collectors.toSet());

            addColumnIfMissing(jdbcTemplate, userColumns, "gender", "ALTER TABLE mk_user ADD COLUMN gender INTEGER NOT NULL DEFAULT -1");
            addColumnIfMissing(jdbcTemplate, userColumns, "birthday", "ALTER TABLE mk_user ADD COLUMN birthday DATE");
            jdbcTemplate.update("UPDATE mk_user SET gender = -1 WHERE gender IS NULL");
        };
    }

    private void addColumnIfMissing(JdbcTemplate jdbcTemplate, Set<String> columns, String column, String sql) {
        if (!columns.contains(column)) {
            jdbcTemplate.execute(sql);
        }
    }

    private String getColumnName(Map<String, Object> row) {
        List<String> candidates = List.of("name", "NAME");
        return candidates.stream()
                .map(row::get)
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .findFirst()
                .orElse("")
                .toLowerCase(Locale.ROOT);
    }
}
