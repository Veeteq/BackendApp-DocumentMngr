package com.veeteq.documentmngr.repository;

import com.veeteq.documentmngr.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
