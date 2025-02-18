package com.veeteq.documentmngr.repository;

import com.veeteq.documentmngr.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
