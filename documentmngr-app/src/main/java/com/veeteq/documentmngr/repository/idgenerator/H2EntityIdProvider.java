package com.veeteq.documentmngr.repository.idgenerator;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile({"default", "test"})
public class H2EntityIdProvider implements EntityIdProvider {

    private final JdbcTemplate jdbcTemplate;

    public H2EntityIdProvider(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long getNextId(EntityIdMapping mapping) {
        var sql = "select next value for " + mapping.getSequenceName();
        var result = jdbcTemplate.queryForObject(sql, Long.class);
        return result;
    }
}
