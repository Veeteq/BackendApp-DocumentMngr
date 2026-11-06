package com.veeteq.documentmngr.repository;

import com.veeteq.documentmngr.model.*;
import com.veeteq.documentmngr.repository.idgenerator.H2EntityIdProvider;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(H2EntityIdProvider.class)
class DocumentOrphanRemovalTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentItemRepository documentItemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldDeleteDocumentItemsAndFinancialRecordsWhenCollectionIsCleared() {
        var document = createDocumentWithItems();

        var savedDocument = documentRepository.saveAndFlush(document);

        var documentId = savedDocument.getId();

        var financialRecordIds = savedDocument.getDocumentItems().stream()
                        .map(DocumentItem::getFinancialRecord)
                        .map(FinancialRecord::getId)
                        .toList();

        entityManager.clear();

        var managedDocument = documentRepository.findById(documentId).orElseThrow();

        managedDocument.clearDocumentItems();

        entityManager.flush();
        entityManager.clear();

        var reloadedDocument = documentRepository.findById(documentId).orElseThrow();

        assertThat(reloadedDocument.getDocumentItems()).isEmpty();

        assertThat(documentItemRepository.findAll().stream()
                        .filter(item -> item.getId().getId().equals(documentId))
                        .toList()
        ).isEmpty();

        financialRecordIds.forEach(financialRecordId -> assertThat(entityManager.find(Expense.class, financialRecordId)).isNull());
    }

    @Test
    void shouldReplaceDocumentItemsDuringUpdate() {
        var document = createDocumentWithItems();
        var savedDocument = documentRepository.saveAndFlush(document);
        var documentId = savedDocument.getId();

        var oldFinancialRecordIds = savedDocument.getDocumentItems().stream()
                .map(DocumentItem::getFinancialRecord)
                .map(FinancialRecord::getId)
                .toList();
        entityManager.clear();

        // Create reference data for the replacement DocumentItem
        var category = Category.builder()
                .withId(10L)
                .withName("UPDATED_CATEGORY")
                .withCategoryType(CategoryType.Exp)
                .build();

        var replacementItem = Item.builder()
                .withId(10L)
                .withName("Updated Item")
                .withCategory(category)
                .build();

        var savedReplacementItem = itemRepository.saveAndFlush(replacementItem);
        entityManager.clear();

        // Load managed aggregate exactly like the service
        var managedDocument = documentRepository.findById(documentId).orElseThrow();

        // Same lifecycle as service update
        managedDocument.clearDocumentItems();

        entityManager.flush();

        var replacementDocumentItem = DocumentItem.builder()
                .withDocument(managedDocument)
                .withDocumentItemType(DocumentItemType.EXP)
                .withItem(savedReplacementItem)
                .withItemQuantity(BigDecimal.valueOf(3))
                .withItemPrice(BigDecimal.valueOf(15))
                .withItemComment("Updated item")
                .build();
        managedDocument.addToDocumentItems(replacementDocumentItem);
        documentRepository.save(managedDocument);

        entityManager.flush();
        entityManager.clear();

        // Assert persisted aggregate
        var updatedDocument = documentRepository.findById(documentId).orElseThrow();
        assertThat(updatedDocument.getDocumentItems()).hasSize(1);

        var updatedDocumentItem = updatedDocument.getDocumentItems().get(0);
        assertThat(updatedDocumentItem.getId().getSequenceNumber()).isEqualTo(1);
        assertThat(updatedDocumentItem.getType()).isEqualTo(DocumentItemType.EXP);
        assertThat(updatedDocumentItem.getExpense()).isNotNull();
        assertThat(updatedDocumentItem.getIncome()).isNull();

        var updatedExpense = updatedDocumentItem.getExpense();
        assertThat(updatedExpense.getItem().getId()).isEqualTo(savedReplacementItem.getId());
        assertThat(updatedExpense.getItem().getCategory().getName()).isEqualTo("UPDATED_CATEGORY");
        assertThat(updatedExpense.getCount()).isEqualByComparingTo("3");
        assertThat(updatedExpense.getPrice()).isEqualByComparingTo("15");
        assertThat(updatedExpense.getComment()).isEqualTo("Updated item");

        oldFinancialRecordIds.forEach(financialRecordId -> assertThat(entityManager.find(Expense.class, financialRecordId)).isNull());
        assertThat(documentItemRepository.findAll().stream()
                        .filter(item -> item.getId().getId().equals(documentId))
                        .toList()
        ).hasSize(1);
    }


    private Document createDocumentWithItems() {
        var account = Account.builder().withId(1L).withName("ABC").withDescription("ABC Description").withCurrency(Currency.getInstance("PLN")).withImageUrl("https://image.com/abc.png").withDefaultPaymentMethod(PaymentMethod.CASH).build();
        var savedAccount = accountRepository.saveAndFlush(account);

        var category = Category.builder().withId(1L).withName("CAT_01").withCategoryType(CategoryType.Exp).build();
        var item2 = Item.builder().withId(1L).withName("Item_01").withCategory(category).build();
        var savedItem2 = itemRepository.saveAndFlush(item2);

        var item4 = Item.builder().withId(2L).withName("Item_01").withCategory(category).build();
        var savedItem4 = itemRepository.saveAndFlush(item4);

        var document = Document.builder()
                .withDocumentDate(LocalDate.now())
                .withDocumentType(DocumentType.INVOICE)
                .withDocumentName("Car insurance")
                .withDocumentDescription("January invoice for Car Insurance, 1st installment")
                .withInvoiceNumber("CAR/INS/2025/17900")
                .withPaymentMethod(PaymentMethod.BANKTRANSFER)
                .withAccount(savedAccount)
                .withCurrencyCode(Currency.getInstance("PLN").getCurrencyCode())
                .withExchangeRate(BigDecimal.ONE)
                .build();
        document.addToDocumentItems(DocumentItem.builder()
                .withDocument(document)
                .withDocumentItemType(DocumentItemType.EXP)
                .withItem(savedItem2)
                .withItemQuantity(BigDecimal.ONE)
                .withItemPrice(BigDecimal.TWO)
                .withItemComment("Personal Liability")
                .build()
        );
        document.addToDocumentItems(DocumentItem.builder()
                .withDocument(document)
                .withDocumentItemType(DocumentItemType.EXP)
                .withItem(savedItem4)
                .withItemQuantity(BigDecimal.TWO)
                .withItemPrice(BigDecimal.TEN)
                .withItemComment("Professional Liability")
                .build()
        );
        return document;
    }
}