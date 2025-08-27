package com.veeteq.documentmngr.repository;

import com.veeteq.documentmngr.model.DocumentItem;
import com.veeteq.documentmngr.model.DocumentItemId;
import com.veeteq.documentmngr.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DocumentItemRepository extends JpaRepository<DocumentItem, DocumentItemId> {

    @Query()
    long countByExpense_Item(Item item);
}
