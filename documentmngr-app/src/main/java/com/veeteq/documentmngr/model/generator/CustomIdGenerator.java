package com.veeteq.documentmngr.model.generator;

import com.veeteq.documentmngr.repository.EntityIdMapping;
import com.veeteq.documentmngr.repository.UtilityRepository;
import org.hibernate.HibernateException;
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
        if (object == null) throw new HibernateException("Cannot generate ID for null entity");

        var className = object.getClass().getName();

        final EntityPersister persister = session.getEntityPersister(className, object);
        Object identifier = persister.getIdentifier(object, session);
        if (Objects.nonNull(identifier)) {
            return identifier;
        }

        var entityIdMapping = resolveEntityIdMapping(object);
        var id = getUtilityRepository().getNextId(entityIdMapping);
        return id;
        //generateIdForEntity(session, persister.getEntityName());
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

    private UtilityRepository getUtilityRepository() {
        if (applicationContext == null) throw new HibernateException("Spring ApplicationContext is not available in CustomIdGenerator");
        return applicationContext.getBean(UtilityRepository.class);
    }

    private EntityIdMapping resolveEntityIdMapping(Object object) {
        return switch (object.getClass().getSimpleName()) {
            case "Account" -> EntityIdMapping.ACCOUNT;
            case "Category" -> EntityIdMapping.CATEGORY;
            case "Document" -> EntityIdMapping.DOCUMENT;
            case "Item" -> EntityIdMapping.ITEM;
            case "Income" -> EntityIdMapping.INCOME;
            case "Expense" -> EntityIdMapping.EXPENSE;
            default -> throw new HibernateException(
                    "No EntityIdMapping configured for entity: " + object.getClass().getName()
            );
        };
    }

}
