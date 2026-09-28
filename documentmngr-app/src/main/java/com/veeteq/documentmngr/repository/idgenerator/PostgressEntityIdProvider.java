package com.veeteq.documentmngr.repository.idgenerator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile({"qa", "dev"})
public class PostgressEntityIdProvider implements EntityIdProvider {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Long getNextId(EntityIdMapping mapping) {
        var sql = "select nextval('" + mapping.getSequenceName() + "')";
        var result = (BigDecimal) entityManager.createNativeQuery(sql).getSingleResult();
        return result.longValue();
    }
}
