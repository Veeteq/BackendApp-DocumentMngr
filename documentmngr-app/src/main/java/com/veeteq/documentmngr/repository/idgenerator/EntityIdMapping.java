package com.veeteq.documentmngr.repository.idgenerator;

import com.veeteq.documentmngr.model.*;

import java.util.Arrays;

public enum EntityIdMapping {
    ACCOUNT(Account.class,   "user_seq"),
    CATEGORY(Category.class, "cate_seq"),
    DOCUMENT(Document.class, "docu_seq"),
    INCOME(Income.class,     "inco_seq"),
    ITEM(Item.class,         "item_seq"),
    EXPENSE(Expense.class,   "expe_seq");

    private Class<?> clazz;
    private final String sequenceName;

    EntityIdMapping(Class clazz, String sequenceName) {
        this.clazz = clazz;
        this.sequenceName = sequenceName;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public static EntityIdMapping from(Class<?> clazz) {
        return Arrays.stream(values())
                .filter(e -> e.getClazz().equals(clazz))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported entity: " + clazz.getName()));
    }
}
