package com.veeteq.documentmngr.repository.idgenerator;

public interface EntityIdProvider {
    Long getNextId(EntityIdMapping mapping);
}
