package com.veeteq.documentmngr.repository.idgenerator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile({"prod"})
public class OracleEntityIdProvider implements EntityIdProvider {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Long getNextId(EntityIdMapping mapping) {
        var sql = "select generate_id(:entity) from dual";
        var result = (BigDecimal) entityManager.createNativeQuery(sql)
                .setParameter(mapping.name(), 1L)
                .getSingleResult();
        return result.longValue();
    }
}
