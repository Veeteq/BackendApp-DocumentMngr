package com.veeteq.documentmngr.model;

import com.veeteq.documentmngr.repository.UtilityRepository;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "documents", uniqueConstraints = @UniqueConstraint(columnNames = "docu_id"))
public class Document {

    @Id
    /*@GeneratedValue(generator = "doc-generator")
    @GenericGenerator(name = "doc-generator",
                      parameters = {@Parameter(name = "prefix", value = "prod")},
                      strategy = "com.veeteq.documentmngr.model.generator.DocumentIdGenerator")*/
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "docu_seq")
    @SequenceGenerator(name = "docu_seq", allocationSize = 1)
    @Column(name = "docu_id")
    private Long id;

    @Column(name = "docu_dt", nullable = false)
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate documentDate;

    @Enumerated(EnumType.STRING)
    @Column(name="docu_type_tx", nullable = false)
    private DocumentType documentType;

    @Column(name="docu_name_tx")
    private String documentName;

    @Column(name="docu_desc_tx")
    private String documentDescription;

    @Column(name="invo_numb_tx")
    private String invoiceNumber;

    @ManyToOne
    @JoinColumn(name = "acco_id")
    private Account account;

    @Column(name="cprt_id")
    private Long counterpartyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "paym_meth_tx")
    private PaymentMethod paymentMethod;

    @Column(name="curr_cd")
    private Currency currency;

    @Column(name="curr_exch_am")
    private BigDecimal exchangeRate;

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private final List<DocumentItem> documentItems = new LinkedList<>();

    @CreationTimestamp
    @Column(name = "crea_dt", nullable = false, updatable = false)
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    @Column(name = "updt_dt", nullable = false, updatable = true)
    private LocalDateTime updateDateTime;

    @Version
    @Column(name = "vers_nm")
    private Integer version;

    public void addToDocumentItems(DocumentItem documentItem) {
        var documentItemId = new DocumentItemId(this.id, this.documentItems.size() + 1);
        documentItem.setId(documentItemId);
        documentItem.setDocument(this);
        this.documentItems.add(documentItem);
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDocumentDate() {
        return documentDate;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getDocumentDescription() {
        return documentDescription;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public Account getAccount() {
        return account;
    }

    public Long getCounterpartyId() {
        return counterpartyId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public List<DocumentItem> getDocumentItems() {
        return documentItems;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public Integer getVersion() {
        return version;
    }

    public Integer getDocumentItemsCount() {
        return this.documentItems.size();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Document entity;

        private Account targetAccount;
        private BigDecimal transferAmount;
        private Item transferItem;
        private UtilityRepository utilityRepository;

        private Builder() {
            entity = new Document();
        }

        public Builder withId(Long id) {
            entity.id = id;
            return this;
        }

        public Builder withDocumentDate(LocalDate documentDate) {
            entity.documentDate = documentDate;
            return this;
        }

        public Builder withDocumentType(DocumentType documentType) {
            entity.documentType = documentType;
            return this;
        }

        public Builder withDocumentName(String documentName) {
            entity.documentName = documentName;
            return this;
        }

        public Builder withDocumentDescription(String documentDescription) {
            entity.documentDescription = documentDescription;
            return this;
        }

        public Builder withInvoiceNumber(String invoiceNumber) {
            entity.invoiceNumber = invoiceNumber;
            return this;
        }

        public Builder withAccount(Account account) {
            entity.account = account;
            return this;
        }

        public Builder withCounterpartyId(Long counterpartyId) {
            entity.counterpartyId = counterpartyId;
            return this;
        }

        public Builder withPaymentMethod(String paymentMethod) {
            entity.paymentMethod = PaymentMethod.valueOf(paymentMethod);
            return this;
        }

        public Builder withPaymentMethod(PaymentMethod paymentMethod) {
            entity.paymentMethod = paymentMethod;
            return this;
        }

        public Builder withCurrencyCode(String currencyCode) {
            entity.currency = Currency.getInstance(currencyCode);
            return this;
        }

        public Builder withExchangeRate(BigDecimal exchangeRate) {
            entity.exchangeRate = exchangeRate;
            return this;
        }

        public Builder withTargetAccount(Account account) {
            this.targetAccount = account;
            return this;
        }

        public Builder withTransferAmount(BigDecimal transferAmount) {
            this.transferAmount = transferAmount;
            return this;
        }

        public Builder withTransferItem(Item transferItem) {
            this.transferItem = transferItem;
            return this;
        }

        public Builder withRepository(UtilityRepository utilityRepository) {
            this.utilityRepository = utilityRepository;
            return this;
        }

        public Document build() {
            if (entity.documentType == DocumentType.TRANSFER) {
                entity.documentName = DocumentType.TRANSFER.name();
                entity.documentDescription = MessageFormat.format("Transfer of {0} from {1} to {2}", transferAmount, entity.account.getName(), targetAccount.getName());
                return buildTransfer();
            }
            return entity;
        }

        public Document buildTransfer() {
            // Create the EXPENSE record for the source account
            Expense expense = Expense.builder()
                    .withOperationDate(entity.documentDate)
                    .withAccount(entity.account)
                    .withItem(this.transferItem)
                    .withPrice(this.transferAmount)
                    .withCount(BigDecimal.ONE) // Quantity is always 1
                    .withComment(entity.account.getName() + " -> " + this.targetAccount.getName())
                    .build();

            // Create the INCOME record for the target account
            Income income = Income.builder()
                    .withOperationDate(entity.documentDate)
                    .withAccount(this.targetAccount)
                    .withItem(this.transferItem)
                    .withPrice(this.transferAmount)
                    .withCount(BigDecimal.ONE) // Quantity is always 1
                    .withComment(entity.account.getName() + " -> " + this.targetAccount.getName())
                    .build();

            // Add the expense and income to the document items
            DocumentItem expenseItem = new DocumentItem();
            expenseItem.setType(DocumentItemType.EXP);
            expenseItem.setFinancialRecord(expense);
            entity.addToDocumentItems(expenseItem);

            DocumentItem incomeItem = new DocumentItem();
            incomeItem.setType(DocumentItemType.INC);
            incomeItem.setFinancialRecord(income);
            entity.addToDocumentItems(incomeItem);

            return entity;
        }
    }

}
