package com.veeteq.documentmngr.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "expenses")
@AttributeOverride(name = "id",      column = @Column(name = "expe_id"))
@AttributeOverride(name = "count",   column = @Column(name = "expe_item_cn"))
@AttributeOverride(name = "price",   column = @Column(name = "expe_pric_am"))
@AttributeOverride(name = "comment", column = @Column(name = "expe_comm_tx"))
public class Expense extends FinancialRecord {

    public static class ExpenseBuilder extends Builder<Expense, ExpenseBuilder> {
        @Override
        protected Expense createEntity() {
            return new Expense();
        }

        @Override
        protected ExpenseBuilder self() {
            return this;
        }
    }

    public static ExpenseBuilder builder() {
        return new ExpenseBuilder();
    }
}
