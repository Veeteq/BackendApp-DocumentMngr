package com.veeteq.documentmngr.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class FinancialRecord {

    protected FinancialRecord() {}

    @Id
    protected Long id;

    @Column(name = "oper_dt", nullable = false, columnDefinition = "DATE")
    protected LocalDate operationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    protected Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    protected Item item;

    protected BigDecimal count;

    protected BigDecimal price;

    protected String comment;

    @CreationTimestamp
    @Column(name = "crea_dt", nullable = false, updatable = false)
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    @Column(name = "updt_dt", nullable = false, updatable = true)
    private LocalDateTime updateDateTime;

    @Version
    @Column(name = "vers_nm")
    private Integer version;

    public Long getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public Account getAccount() {
        return account;
    }

    public BigDecimal getCount() {
        return count;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getComment() {
        return comment;
    }

    public static abstract class Builder<T extends FinancialRecord, B extends Builder<T, B>> {
        protected T entity;

        protected abstract T createEntity();

        protected Builder() {
            entity = createEntity();
        }

        public B withId(Long id) {
            entity.id = id;
            return self();
        }

        public B withOperationDate(LocalDate operationDate) {
            entity.operationDate = operationDate;
            return self();
        }

        public B withAccount(Account account) {
            entity.account = account;
            return self();
        }

        public B withItem(Item item) {
            entity.item = item;
            return self();
        }

        public B withCount(BigDecimal count) {
            entity.count = count;
            return self();
        }

        public B withPrice(BigDecimal price) {
            entity.price = price;
            return self();
        }

        public B withComment(String comment) {
            entity.comment = comment;
            return self();
        }

        protected abstract B self();

        public T build() {
            return entity;
        }
    }
}
