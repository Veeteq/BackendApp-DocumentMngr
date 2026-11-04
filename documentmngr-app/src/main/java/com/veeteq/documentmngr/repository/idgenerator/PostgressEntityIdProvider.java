package com.veeteq.documentmngr.repository.idgenerator;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile({"qa", "dev"})
public class PostgressEntityIdProvider implements EntityIdProvider {

    private final JdbcTemplate jdbcTemplate;

    public PostgressEntityIdProvider(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long getNextId(EntityIdMapping mapping) {
        var sql = "select nextval('" + mapping.getSequenceName() + "')";
        var result = jdbcTemplate.queryForObject(sql, Long.class);
        return result;
    }
}
