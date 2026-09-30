package com.veeteq.documentmngr.repository.idgenerator;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile({"prod"})
public class OracleEntityIdProvider implements EntityIdProvider {

    private final JdbcTemplate jdbcTemplate;

    public OracleEntityIdProvider(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long getNextId(EntityIdMapping mapping) {
        var sql = "select generate_id(?) from dual";
        return jdbcTemplate.queryForObject(sql, Long.class, mapping.name());
    }
}
