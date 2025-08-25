package com.veeteq.documentmngr.repository;

import com.veeteq.documentmngr.model.DocumentItem;
import com.veeteq.documentmngr.model.DocumentItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentItemRepository extends JpaRepository<DocumentItem, DocumentItemId> {
}
