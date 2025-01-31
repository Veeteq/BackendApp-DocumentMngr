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

    public Long getId() {
        return id;
    }

    public Item setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Item setName(String name) {
        this.name = name;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public Item setCategory(Category category) {
        this.category = category;
        return this;
    }
}
