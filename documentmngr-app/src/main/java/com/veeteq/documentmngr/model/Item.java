package com.veeteq.documentmngr.model;

import jakarta.persistence.*;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @Column(name = "item_id")
    Long id;

    @Column(name = "item_name_tx")
    String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cate_id", nullable = false)
    Category category;

    public Item() {}

    public Item(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.category = builder.category;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private Category category;

        private Builder() {}

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withCategory(Category category) {
            this.category = category;
            return this;
        }

        public Item build() {
            return new Item(this);
        }
    }

}
