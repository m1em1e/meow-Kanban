package com.godotvillage.meowkanban.common.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class DatabaseMigrationConfig {

    private static final String DEFAULT_DATA_INIT_KEY = "default-data-v1";

    @Bean
    public ApplicationRunner databaseInitializationRunner(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        return args -> {
            runScript(dataSource, "schema.sql");

            Set<String> userColumns = jdbcTemplate.queryForList("PRAGMA table_info(mk_user)")
                    .stream()
                    .map(this::getColumnName)
                    .collect(Collectors.toSet());

            addColumnIfMissing(jdbcTemplate, userColumns, "gender", "ALTER TABLE mk_user ADD COLUMN gender INTEGER NOT NULL DEFAULT -1");
            addColumnIfMissing(jdbcTemplate, userColumns, "birthday", "ALTER TABLE mk_user ADD COLUMN birthday DATE");
            jdbcTemplate.update("UPDATE mk_user SET gender = -1 WHERE gender IS NULL");
            migrateStatusColumn(jdbcTemplate, "mk_user");
            migrateStatusColumn(jdbcTemplate, "mk_role");
            migrateCreatorColumns(jdbcTemplate);
            migrateTaskPriority(jdbcTemplate);

            initializeDefaultDataIfNeeded(dataSource, jdbcTemplate);
        };
    }

    private void runScript(DataSource dataSource, String scriptPath) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource(scriptPath));
        populator.execute(dataSource);
    }

    private void initializeDefaultDataIfNeeded(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        Long initializedCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM mk_database_init WHERE init_key = ?",
                Long.class,
                DEFAULT_DATA_INIT_KEY);

        if (initializedCount != null && initializedCount > 0) {
            return;
        }

        Long userCount = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM mk_user", Long.class);
        if (userCount != null && userCount > 0) {
            markDefaultDataInitialized(jdbcTemplate);
            return;
        }

        runScript(dataSource, "data.sql");
        markDefaultDataInitialized(jdbcTemplate);
    }

    private void markDefaultDataInitialized(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("INSERT OR IGNORE INTO mk_database_init (init_key) VALUES (?)", DEFAULT_DATA_INIT_KEY);
    }

    private void addColumnIfMissing(JdbcTemplate jdbcTemplate, Set<String> columns, String column, String sql) {
        if (!columns.contains(column)) {
            jdbcTemplate.execute(sql);
        }
    }

    private void migrateStatusColumn(JdbcTemplate jdbcTemplate, String tableName) {
        jdbcTemplate.update("UPDATE " + tableName + " SET status = 1 WHERE status = 'active'");
        jdbcTemplate.update("UPDATE " + tableName + " SET status = 0 WHERE status = 'disabled'");
        jdbcTemplate.update("UPDATE " + tableName + " SET status = 1 WHERE status IS NULL");
    }

    private void migrateCreatorColumns(JdbcTemplate jdbcTemplate) {
        Set<String> userRoleColumns = getTableColumns(jdbcTemplate, "mk_user_role");
        renameColumnIfNeeded(
                jdbcTemplate,
                userRoleColumns,
                "created_by",
                "creater_id",
                "ALTER TABLE mk_user_role RENAME COLUMN created_by TO creater_id"
        );

        Set<String> taskColumns = getTableColumns(jdbcTemplate, "mk_task");
        renameColumnIfNeeded(
                jdbcTemplate,
                taskColumns,
                "created_by",
                "creater_id",
                "ALTER TABLE mk_task RENAME COLUMN created_by TO creater_id"
        );
        renameColumnIfNeeded(
                jdbcTemplate,
                taskColumns,
                "updated_by",
                "updater_id",
                "ALTER TABLE mk_task RENAME COLUMN updated_by TO updater_id"
        );
    }

    private void migrateTaskPriority(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("UPDATE mk_task SET priority = 0 WHERE lower(CAST(priority AS TEXT)) = 'low'");
        jdbcTemplate.update("UPDATE mk_task SET priority = 1 WHERE lower(CAST(priority AS TEXT)) = 'normal'");
        jdbcTemplate.update("UPDATE mk_task SET priority = 3 WHERE lower(CAST(priority AS TEXT)) = 'urgent'");
        jdbcTemplate.update("UPDATE mk_task SET priority = 1 WHERE priority IS NULL OR CAST(priority AS TEXT) NOT IN ('0', '1', '2', '3')");
    }

    private Set<String> getTableColumns(JdbcTemplate jdbcTemplate, String tableName) {
        return jdbcTemplate.queryForList("PRAGMA table_info(" + tableName + ")")
                .stream()
                .map(this::getColumnName)
                .collect(Collectors.toSet());
    }

    private void renameColumnIfNeeded(
            JdbcTemplate jdbcTemplate,
            Set<String> columns,
            String oldColumn,
            String newColumn,
            String sql
    ) {
        if (columns.contains(oldColumn) && !columns.contains(newColumn)) {
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
