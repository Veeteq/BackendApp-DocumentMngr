package com.veeteq.documentmngr.processor;

import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.model.Item;

import java.math.BigDecimal;

public class TransferData {
    private Account sourceAccount;
    private Account targetAccount;
    private BigDecimal transferAmount;
    private Item transferItem;

    private BigDecimal provisionAmount;
    private Account provisionAccount;
    private Item provisionItem;

    public Account getSourceAccount() {
        return sourceAccount;
    }

    public TransferData setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
        return this;
    }

    public Account getTargetAccount() {
        return targetAccount;
    }

    public TransferData setTargetAccount(Account targetAccount) {
        this.targetAccount = targetAccount;
        return this;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public TransferData setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
        return this;
    }

    public Item getTransferItem() {
        return transferItem;
    }

    public TransferData setTransferItem(Item transferItem) {
        this.transferItem = transferItem;
        return this;
    }

    public BigDecimal getProvisionAmount() {
        return provisionAmount;
    }

    public TransferData setProvisionAmount(BigDecimal provisionAmount) {
        this.provisionAmount = provisionAmount;
        return this;
    }

    public Account getProvisionAccount() {
        return provisionAccount;
    }

    public TransferData setProvisionAccount(Account provisionAccount) {
        this.provisionAccount = provisionAccount;
        return this;
    }

    public Item getProvisionItem() {
        return provisionItem;
    }

    public TransferData setProvisionItem(Item provisionItem) {
        this.provisionItem = provisionItem;
        return this;
    }
}
