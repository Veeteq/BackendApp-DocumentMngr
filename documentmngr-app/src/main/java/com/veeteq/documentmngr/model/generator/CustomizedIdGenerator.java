package com.veeteq.documentmngr.model.generator;

import com.veeteq.documentmngr.repository.idgenerator.EntityIdMapping;
import com.veeteq.documentmngr.repository.idgenerator.EntityIdProvider;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.persister.entity.EntityPersister;

import java.util.Objects;

public class CustomizedIdGenerator implements IdentifierGenerator {
    private static final long serialVersionUID = 1L;

    private final EntityIdProvider entityIdProvider;

    public CustomizedIdGenerator(EntityIdProvider entityIdProvider) {
        this.entityIdProvider = entityIdProvider;
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        if (object == null) throw new HibernateException("Cannot generate ID for null entity");

        var className = object.getClass().getName();
        final EntityPersister persister = session.getEntityPersister(className, object);
        Object identifier = persister.getIdentifier(object, session);
        if (Objects.nonNull(identifier)) {
            return identifier;
        }

        var mapping = resolveEntityIdMapping(object);
        return entityIdProvider.getNextId(mapping);
    }

    private EntityIdMapping resolveEntityIdMapping(Object object) {
        return EntityIdMapping.from(object.getClass());
    }
}
