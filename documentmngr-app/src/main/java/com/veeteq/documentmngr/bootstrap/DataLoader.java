package com.veeteq.documentmngr.bootstrap;

import com.veeteq.documentmngr.model.*;
import com.veeteq.documentmngr.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import static com.veeteq.documentmngr.repository.EntityIdMapping.*;

@Component
@Profile("test")
public class DataLoader implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final ItemRepository itemRepository;
    private final DocumentRepository documentRepository;
    private final UtilityRepository utilityRepository;

    @Autowired
    public DataLoader(AccountRepository accountRepository, ItemRepository itemRepository, DocumentRepository documentRepository, UtilityRepository utilityRepository) {
        this.accountRepository = accountRepository;
        this.itemRepository = itemRepository;
        this.documentRepository = documentRepository;
        this.utilityRepository = utilityRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loadAccounts();
        loadItems();
        loadDocuments();
    }

    private void loadAccounts() {
        var list = List.of(
                Account.builder().withId(nextId(ACCOUNT)).withName("ABC").build(),
                Account.builder().withId(nextId(ACCOUNT)).withName("BCD").withDescription("BCD Description").withCurrency(Currency.getInstance("EUR")).withImageUrl("https://image.com/logo.png").build(),
                Account.builder().withId(nextId(ACCOUNT)).withName("CDE").build(),
                Account.builder().withId(nextId(ACCOUNT)).withName("DEF").build(),
                Account.builder().withId(nextId(ACCOUNT)).withName("EFG").withDescription("EFG Description").withCurrency(Currency.getInstance("GBP")).withImageUrl("https://image.com/logo.png").build(),
                Account.builder().withId(nextId(ACCOUNT)).withName("FGH").build(),
                Account.builder().withId(nextId(ACCOUNT)).withName("GHI").build(),
                Account.builder().withId(nextId(ACCOUNT)).withName("HIJ").build(),
                Account.builder().withId(nextId(ACCOUNT)).withName("IJK").build(),
                Account.builder().withId(nextId(ACCOUNT)).withName("JKL").build()
        );
        accountRepository.saveAll(list);
    }

    private void loadItems() {
        Map<Long, Category> categories = Map.of(
                1L, Category.builder().withId(nextId(CATEGORY)).withName("CAT_01").withCategoryType(CategoryType.Exp).build(),
                2L, Category.builder().withId(nextId(CATEGORY)).withName("CAT_02").withCategoryType(CategoryType.Exp).build(),
                3L, Category.builder().withId(nextId(CATEGORY)).withName("CAT_03").withCategoryType(CategoryType.Inc).build(),
                4L, Category.builder().withId(nextId(CATEGORY)).withName("CAT_04").withCategoryType(CategoryType.Inc).build(),
                5L, Category.builder().withId(nextId(CATEGORY)).withName("CAT_05").withCategoryType(CategoryType.Both).build()
        );

        var list = List.of(
                Item.builder().withId(nextId(ITEM)).withName("Item_01").withCategory(categories.get(1L)).build(),
                Item.builder().withId(nextId(ITEM)).withName("Item_02").withCategory(categories.get(2L)).build(),
                Item.builder().withId(nextId(ITEM)).withName("Item_03").withCategory(categories.get(3L)).build(),
                Item.builder().withId(nextId(ITEM)).withName("Item_04").withCategory(categories.get(4L)).build(),
                Item.builder().withId(nextId(ITEM)).withName("Item_05").withCategory(categories.get(5L)).build(),
                Item.builder().withId(nextId(ITEM)).withName("Item_06").withCategory(categories.get(1L)).build(),
                Item.builder().withId(nextId(ITEM)).withName("Item_07").withCategory(categories.get(2L)).build(),
                Item.builder().withId(nextId(ITEM)).withName("Item_08").withCategory(categories.get(3L)).build(),
                Item.builder().withId(nextId(ITEM)).withName("Item_09").withCategory(categories.get(4L)).build(),
                Item.builder().withId(nextId(ITEM)).withName("Item_10").withCategory(categories.get(5L)).build()
        );
        itemRepository.saveAll(list);
    }

    private void loadDocuments() {
        var document1 = createExpense();
        documentRepository.save(document1);
        var document2 = createTransfer();
        documentRepository.save(document2);
    }

    private Document createExpense() {
        var accRef = accountRepository.getReferenceById(3L);
        var document = Document.builder()
                .withRepository(utilityRepository)
                .withDocumentDate(LocalDate.now())
                .withDocumentType(DocumentType.INVOICE)
                .withDocumentName("Car insurance")
                .withDocumentDescription("January invoice for Car Insurance, 1st installment")
                .withPaymentMethod(PaymentMethod.EFT)
                .withAccount(accRef)
                .withCurrencyCode(Currency.getInstance("PLN").getCurrencyCode())
                .withExchangeRate(BigDecimal.ONE)
                .build();
        document.addToDocumentItems(
                DocumentItem.builder()
                        .withDocument(document)
                        .withDocumentItemType(DocumentItemType.EXP)
                        .withItem(itemRepository.getReferenceById(2L))
                        .withItemQuantity(BigDecimal.ONE)
                        .withItemPrice(BigDecimal.TWO)
                        .withItemComment("Personal Liability")
                        .build()
        );
        return document;
    }

    @Transactional
    private Document createTransfer() {
        var document = Document.builder()
                .withRepository(utilityRepository)
                .withDocumentDate(LocalDate.now().minusDays(1))
                .withDocumentType(DocumentType.TRANSFER)
                .withPaymentMethod(PaymentMethod.EFT)
                .withAccount(accountRepository.findById(4L).orElseThrow())
                .withTargetAccount(accountRepository.findById(5L).orElseThrow())
                .withCurrencyCode(Currency.getInstance("PLN").getCurrencyCode())
                .withExchangeRate(BigDecimal.ONE)
                .withTransferAmount(BigDecimal.valueOf(99.99))
                .withTransferItem(itemRepository.findById(10L).orElseThrow())
                .build();
        return document;
    }

    private Long nextId(EntityIdMapping mapping) {
        return utilityRepository.getNextId(mapping);
    }
}
