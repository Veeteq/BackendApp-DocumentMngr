package com.veeteq.documentmngr.bootstrap;

import com.veeteq.documentmngr.model.*;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.repository.DocumentRepository;
import com.veeteq.documentmngr.repository.ItemRepository;
import com.veeteq.documentmngr.repository.UtilityRepository;
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
                new Account().setId(1L).setName("ABC"),
                new Account().setId(2L).setName("BCD"),
                new Account().setId(3L).setName("CDE"),
                new Account().setId(4L).setName("DEF"),
                new Account().setId(5L).setName("EFG"),
                new Account().setId(6L).setName("FGH"),
                new Account().setId(7L).setName("GHI"),
                new Account().setId(8L).setName("HIJ"),
                new Account().setId(9L).setName("IJK"),
                new Account().setId(10L).setName("JKL")
        );
        accountRepository.saveAll(list);
    }

    private void loadItems() {
        Map<Long, Category> categories = Map.of(
                1L, new Category().setId(1L).setName("CAT_01").setCategoryType(CategoryType.Exp),
                2L, new Category().setId(2L).setName("CAT_02").setCategoryType(CategoryType.Exp),
                3L, new Category().setId(3L).setName("CAT_03").setCategoryType(CategoryType.Inc),
                4L, new Category().setId(4L).setName("CAT_04").setCategoryType(CategoryType.Inc),
                5L, new Category().setId(5L).setName("CAT_05").setCategoryType(CategoryType.Both)
        );

        var list = List.of(
                new Item().setId(101L).setName("Item_01").setCategory(categories.get(1L)),
                new Item().setId(102L).setName("Item_02").setCategory(categories.get(2L)),
                new Item().setId(103L).setName("Item_03").setCategory(categories.get(3L)),
                new Item().setId(104L).setName("Item_04").setCategory(categories.get(4L)),
                new Item().setId(105L).setName("Item_05").setCategory(categories.get(5L)),
                new Item().setId(106L).setName("Item_06").setCategory(categories.get(1L)),
                new Item().setId(107L).setName("Item_07").setCategory(categories.get(2L)),
                new Item().setId(108L).setName("Item_08").setCategory(categories.get(3L)),
                new Item().setId(109L).setName("Item_09").setCategory(categories.get(4L)),
                new Item().setId(110L).setName("Item_10").setCategory(categories.get(5L))
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
                .withOperationDate(LocalDate.now())
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
                        .withItemId(utilityRepository.getNextId(UtilityRepository.EntityIdMapping.EXPENSE))
                        .withItem(itemRepository.getReferenceById(102L))
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
                .withOperationDate(LocalDate.now().minusDays(1))
                .withDocumentType(DocumentType.TRANSFER)
                .withPaymentMethod(PaymentMethod.EFT)
                .withAccount(accountRepository.findById(4L).orElseThrow())
                .withTargetAccount(accountRepository.findById(5L).orElseThrow())
                .withCurrencyCode(Currency.getInstance("PLN").getCurrencyCode())
                .withExchangeRate(BigDecimal.ONE)
                .withTransferAmount(BigDecimal.valueOf(99.99))
                .withTransferItem(itemRepository.findById(110L).orElseThrow())
                .build();
        return document;
    }
}
