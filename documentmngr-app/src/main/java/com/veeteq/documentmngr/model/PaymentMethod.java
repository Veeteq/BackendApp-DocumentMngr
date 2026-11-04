package com.veeteq.documentmngr.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum PaymentMethod {
    ATMDEPOSIT("ATM Deposit"),
    ATMWITHDRAWAL("ATM Withdrawal"),
    BANKTRANSFER("Bank transfer"),
    CASH("Cash"),
    CREDITCARD("Credit card"),
    DEBITCARD("Debit card");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PaymentMethod findByValue(String value) {
        return Stream.of(PaymentMethod.values())
                .filter(el -> el.value.equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
