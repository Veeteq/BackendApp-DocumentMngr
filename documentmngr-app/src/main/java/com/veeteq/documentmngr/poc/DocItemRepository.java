package com.veeteq.documentmngr.poc;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocItemRepository extends JpaRepository<DocItem, DocItemId> {
}
