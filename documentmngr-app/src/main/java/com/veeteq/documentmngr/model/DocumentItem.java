package com.veeteq.documentmngr.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_items")
public class DocumentItem {

    @EmbeddedId
    private DocumentItemId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "docu_item_type_tx", nullable = false)
    private DocumentItemType type;

    @MapsId("id")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "docu_id", nullable = false)
    private Document document;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "expe_id", nullable = true)
    private Expense expense;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inco_id", nullable = true)
    private Income income;

    @CreationTimestamp
    @Column(name = "crea_dt", nullable = false, updatable = false)
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    @Column(name = "updt_dt", nullable = false, updatable = true)
    private LocalDateTime updateDateTime;

    @Version
    @Column(name = "vers_nm")
    private Integer version;

    public DocumentItem() {}

    public DocumentItem(DocumentItemId id, Document document) {
        this.id = id;
        this.document = document;
    }

    public DocumentItemId getId() {
        return id;
    }

    public DocumentItem setId(DocumentItemId id) {
        this.id = id;
        return this;
    }

    public Document getDocument() {
        return document;
    }

    public DocumentItem setDocument(Document document) {
        this.document = document;
        return this;
    }

    public DocumentItemType getType() {
        return type;
    }

    public DocumentItem setType(DocumentItemType type) {
        this.type = type;
        return this;
    }

    public DocumentItem setType(String type) {
        this.type = DocumentItemType.findByValue(type);
        return this;
    }

    public Expense getExpense() {
        return expense;
    }

    public Income getIncome() {
        return income;
    }

    public <T extends FinancialRecord> DocumentItem setFinancialRecord(T financialRecord) {
        if (financialRecord instanceof Expense expense) {
            this.expense = expense;
        }
        if (financialRecord instanceof Income income) {
            this.income = income;
        }
        return this;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Document document;
        private DocumentItemType type;
        private Long itemId;
        private Item item;
        private BigDecimal itemQuantity;
        private BigDecimal itemPrice;
        private String comment;

        private Builder() {}

        public Builder withDocument(Document document) {
            this.document = document;
            return this;
        }

        public Builder withDocumentItemType(DocumentItemType type) {
            this.type = type;
            return this;
        }

        public Builder withItemId(long itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder withItem(Item item) {
            this.item = item;
            return this;
        }

        public Builder withItemQuantity(BigDecimal quantity) {
            this.itemQuantity = quantity;
            return this;
        }

        public Builder withItemPrice(BigDecimal price) {
            this.itemPrice = price;
            return this;
        }

        public Builder withItemComment(String comment) {
            this.comment = comment;
            return this;
        }

        public DocumentItem build() {
            var entity = new DocumentItem();
            entity.type = this.type;
            var financialRecord = createFinancialRecord(this);
            entity.setFinancialRecord(financialRecord);
            return entity;
        }

        private FinancialRecord createFinancialRecord(Builder builder) {
            var a = switch (builder.type) {
                case EXP -> new Expense.ExpenseBuilder();
                case INC -> new Income.IncomeBuilder();
            };
            a.withId(builder.itemId)
                    .withOperationDate(builder.document.getDocumentDate())
                    .withAccount(builder.document.getAccount())
                    .withPrice(builder.itemPrice)
                    .withItem(builder.item)
                    .withCount(builder.itemQuantity)
                    .withComment(builder.comment);
            return a.build();
        }
    }

}
