package com.veeteq.documentmngr.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @Column(name = "cate_id")
    Long id;

    @Column(name = "cate_name_tx")
    String name;

    @Column(name = "cate_type_tx")
    @Enumerated(value = EnumType.STRING)
    private CategoryType categoryType;

    public Long getId() {
        return id;
    }

    public Category setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Category setName(String name) {
        this.name = name;
        return this;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public Category setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
        return this;
    }
}
