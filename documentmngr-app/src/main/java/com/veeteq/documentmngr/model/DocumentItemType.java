package com.veeteq.documentmngr.model;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DocumentItemType {
    EXP("Expense"),
    INC("Income");

    private final String displayName;

	DocumentItemType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

    @JsonCreator
    public static DocumentItemType findByValue(String displayName) {
        return Stream.of(DocumentItemType.values())
                .filter(el -> el.displayName.equals(displayName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
