package com.veeteq.documentmngr.model.generator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

public class CustomIdGenerator implements IdentifierGenerator {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        String activeProfile = applicationContext.getEnvironment().getActiveProfiles()[0]; // Get the first active profile
        var className = object.getClass().getName();

        final EntityPersister persister = session.getEntityPersister(className, object);
        Object identifier = persister.getIdentifier(object, session);
        if (Objects.nonNull(identifier)) {
            return identifier;
        }

        return generateIdForEntity(session, persister.getEntityName());
    }

    @Override
    public boolean allowAssignedIdentifiers() {
        return true;
    }

    private Long generateIdForEntity(SharedSessionContractImplementor session, String className) {
        String query = String.format("select max(id) from %s", className);
        Long maxId = session.createQuery(query, Long.class).uniqueResult();
        return (maxId == null ? 1 : maxId + 1);
    }
}
