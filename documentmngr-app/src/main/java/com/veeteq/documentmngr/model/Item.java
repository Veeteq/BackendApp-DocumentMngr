package com.veeteq.documentmngr.model;

import com.veeteq.documentmngr.model.generator.CustomId;
import jakarta.persistence.*;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @CustomId
    @Column(name = "item_id")
    Long id;

    @Column(name = "item_name_tx")
    String name;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "cate_id", nullable = false)
    Category category;

    public Item() {}

    public Item(Builder builder) {
        this.id = builder.entity.id;
        this.name = builder.entity.name;
        this.category = builder.entity.category;
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

    public static Builder updater(Item item) {
        return new Builder(item);
    }

    public static class Builder {
        private final Item entity;

        private Builder() {
            entity = new Item();
        }

        private Builder(Item item) {
            entity = item;
        }

        public Builder withId(Long id) {
            entity.id = id;
            return this;
        }

        public Builder withName(String name) {
            entity.name = name;
            return this;
        }

        public Builder withCategory(Category category) {
            entity.category = category;
            return this;
        }

        public Item build() {
            return entity;
        }
    }

}
