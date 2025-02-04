package com.veeteq.documentmngr.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class UtilityRepositoryBase implements UtilityRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Long getNextId(EntityIdMapping mapping) {
        var sql = mapping.getH2Sql();
        var id = (Long) entityManager.createNativeQuery(sql, Long.class).getSingleResult();
        return id;
    }
}
