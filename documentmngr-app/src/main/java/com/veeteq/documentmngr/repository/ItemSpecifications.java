package com.veeteq.documentmngr.repository;

import com.veeteq.documentmngr.model.Category;
import com.veeteq.documentmngr.model.Item;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ItemSpecifications {

    private ItemSpecifications() {}

    public static Specification<Item> nameContains(String name) {
        return (Root<Item> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Item> hasCategory(Long categoryId) {
        return (Root<Item> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }
}
