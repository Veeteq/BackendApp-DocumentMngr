package com.veeteq.documentmngr.repository;

import com.veeteq.documentmngr.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemsByCategoryName(String categoryName);

    List<Item> findByNameContainingIgnoreCase(String pattern);

    Page<Item> findByNameContainingIgnoreCase(String pattern, Pageable pageable);
}
