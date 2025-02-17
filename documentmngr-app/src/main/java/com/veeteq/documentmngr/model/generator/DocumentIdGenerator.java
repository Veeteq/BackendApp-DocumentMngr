package com.veeteq.documentmngr.model.generator;

import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Properties;

public class DocumentIdGenerator implements IdentifierGenerator, Configurable {

    @Autowired
    private ApplicationContext applicationContext;

    private String prefix;

    @Override
    public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) throws MappingException {
        prefix = properties.getProperty("prefix");
    }

    @Override
    public Long generate(SharedSessionContractImplementor session, Object object) {
        String activeProfile = applicationContext.getEnvironment().getActiveProfiles()[0]; // Get the first active profile

        // Custom ID generation logic based on your requirements
        // For example, you can retrieve the maximum ID and increment it
        var className = object.getClass().getSimpleName();
        String query = String.format("select max(id) from %s", className);
        Long maxId = session.createQuery(query, Long.class).uniqueResult();
        return (maxId == null ? 1 : maxId + 1);
/*
        String query = String.format("select %s from %s",
                session.getEntityPersister(object.getClass().getName(), object)
                        .getIdentifierPropertyName(),
                object.getClass().getSimpleName());

        Stream<Long> ids = session.createQuery(query, Long.class).stream();

        Long max = ids
                //.map(o -> o.replace(prefix + "-", ""))
                //.mapToLong(Long::parseLong)
                .max(Long::compare)
                .orElse(0L);

        return max + 1;
 */
    }
}
