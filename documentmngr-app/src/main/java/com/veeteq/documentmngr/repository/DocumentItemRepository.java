package com.veeteq.documentmngr.repository;

import com.veeteq.documentmngr.model.DocumentItem;
import com.veeteq.documentmngr.model.DocumentItemId;
import com.veeteq.documentmngr.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DocumentItemRepository extends JpaRepository<DocumentItem, DocumentItemId> {

    @Query()
    /*
    @Query("SELECT di FROM DocumentItem di " +
           "LEFT JOIN di.expense e ON e.itemId = :itemId " +
           "LEFT JOIN di.income i ON i.itemId = :itemId " +
           "WHERE COALESCE(e.itemId, i.itemId) IS NOT NULL")
     */
    long countByExpense_Item(Item item);
}
