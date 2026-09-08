package com.veeteq.documentmngr.repository;

import com.veeteq.documentmngr.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameIgnoreCase(@Param("name") String categoryName);
}
