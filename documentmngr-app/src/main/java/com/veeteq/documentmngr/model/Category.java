package com.veeteq.documentmngr.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @Column(name = "cate_id")
    private Long id;

    @Column(name = "cate_name_tx")
    String name;

    @Column(name = "cate_type_tx")
    @Enumerated(value = EnumType.STRING)
    private CategoryType categoryType;

    public Category() {}

    public Category(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.categoryType = builder.categoryType;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private CategoryType categoryType;

        private Builder() {}

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withCategoryType(CategoryType categoryType) {
            this.categoryType = categoryType;
            return this;
        }

        public Category build() {
            return new Category(this);
        }
    }
}
