package com.veeteq.documentmngr.model;

import com.veeteq.documentmngr.model.generator.CustomId;
import jakarta.persistence.*;

import java.util.Currency;

@Entity
@Table(name = "users")
public class Account {

    @Id
    @CustomId
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Currency getCurrency() {
        return currency;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder updater(Account account) {
        return new Builder(account);
    }

    public static class Builder {
        private final Account entity;

        private Builder() {
            entity = new Account();
        }

        private Builder(Account account) {
            entity = account;
        }

        public Builder withId(Long id) {
            entity.id = id;
            return this;
        }

        public Builder withName(String name) {
            entity.name = name;
            return this;
        }

        public Builder withCurrency(Currency currency) {
            entity.currency = currency;
            return this;
        }

        public Builder withDescription(String description) {
            entity.description = description;
            return this;
        }

        public Builder withImageUrl(String imageUrl) {
            entity.imageUrl = imageUrl;
            return this;
        }

        public Account build() {
            return entity;
        }
    }
}
