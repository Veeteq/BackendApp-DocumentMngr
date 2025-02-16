package com.veeteq.documentmngr.model;

import jakarta.persistence.*;

@Entity
@Table(name = "incomes")
@SequenceGenerator(name = "default_gen", sequenceName = "inco_seq", allocationSize = 1)
@AttributeOverride(name = "id",      column = @Column(name = "inco_id"))
@AttributeOverride(name = "count",   column = @Column(name = "inco_item_cn"))
@AttributeOverride(name = "price",   column = @Column(name = "inco_pric_am"))
@AttributeOverride(name = "comment", column = @Column(name = "inco_comm_tx"))
public class Income extends FinancialRecord {

    public static class IncomeBuilder extends Builder<Income, IncomeBuilder> {
        @Override
        protected Income createEntity() {
            return new Income();
        }

        @Override
        protected IncomeBuilder self() {
            return this;
        }
    }

    public static IncomeBuilder builder() {
        return new IncomeBuilder();
    }
}
