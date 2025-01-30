package com.veeteq.documentmngr.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Currency;

@Entity
@Table(name = "users")
public class Account {

    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_name_tx")
    private String name;

    @Column(name = "user_desc_tx")
    private String description;

    @Column(name = "user_imag_tx")
    private String imageUrl;

    @Column(name = "user_curr_cd")
    private Currency currency;

    public Long getId() {
        return id;
    }

    public Account setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Account setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Account setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Account setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Account setCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }
}
