package com.veeteq.documentmngr.model;

public enum CategoryType {

    Exp(1, "Expense"),
    Inc(2, "Income"),
    Both(3, "Both");

    private int id;
    private String description;

    private CategoryType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
