package com.example.housekeeping.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Lightweight startup migrations to keep the schema compatible with new features.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseMigrationConfig implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        ensureServiceImageSupportsLongtext();
    }

    private void ensureServiceImageSupportsLongtext() {
        try {
            String dataType = jdbcTemplate.queryForObject(
                    "SELECT DATA_TYPE FROM information_schema.COLUMNS WHERE TABLE_NAME = 'housekeep_service' " +
                            "AND COLUMN_NAME = 'image_base64' AND TABLE_SCHEMA = DATABASE()",
                    String.class);

            if (dataType != null && !"longtext".equalsIgnoreCase(dataType)) {
                log.info("Updating housekeep_service.image_base64 column type from {} to LONGTEXT", dataType);
                jdbcTemplate.execute("ALTER TABLE housekeep_service MODIFY image_base64 LONGTEXT COMMENT '服务封面图片（Base64）'");
            }
        } catch (Exception ex) {
            log.warn("Could not verify/upgrade image_base64 column type: {}", ex.getMessage());
        }
    }
}
