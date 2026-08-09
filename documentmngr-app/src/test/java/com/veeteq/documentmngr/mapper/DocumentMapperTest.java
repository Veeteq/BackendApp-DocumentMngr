package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.DocumentItemType;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.model.Item;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.repository.ItemRepository;
import com.veeteq.documentmngr.rest.dto.DocumentItemRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentItemRequestDto.ItemTypeEnum;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import com.veeteq.documentmngr.rest.dto.DocumentTypeDto;
import com.veeteq.documentmngr.rest.dto.PaymentDto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DocumentMapperTest {

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void toDto() {
        //given
        Document document = createDocument();

        //when
        DocumentResponseDto dto = documentMapper.toDto(document);

        //then
        assertEquals(document.getDocumentDate(), dto.getDocumentDate());
        assertEquals(document.getDocumentType().name(), dto.getDocumentType());
        assertEquals(document.getDocumentName(), dto.getDocumentName());
        assertEquals(document.getDocumentDescription(), dto.getDocumentComment());
        assertEquals(document.getPaymentMethod().name(), dto.getPaymentMethod());
        assertEquals(document.getInvoiceNumber(), dto.getInvoiceNumber());
        assertEquals(document.getCurrency().getCurrencyCode(), dto.getCurrencyCode());
        assertEquals(document.getExchangeRate(), dto.getExchangeRate());

        assertNotNull(dto.getAccount());
        assertEquals(document.getAccount().getId(), dto.getAccount().getAccountId());
        assertEquals(document.getAccount().getName(), dto.getAccount().getAccountName());

        assertEquals(18, dto.getClass().getDeclaredFields().length);
    }

    @Test
    void toEntity() {
        //given
        var dto = createDocumentRequestDto();
        var sourceAccount = accountRepository.findById(dto.getAccountId()).orElseThrow();

        var itemId = dto.getDocumentItems().getFirst().getItemId();
        var expectedItem = itemRepository.findById(itemId).orElseThrow();

        //when
        Document document = documentMapper.toEntity(dto, sourceAccount);

        //then
        assertEquals(dto.getDocumentDate(), document.getDocumentDate());
        assertEquals(dto.getDocumentType().name(), document.getDocumentType().name());
        assertEquals(dto.getDocumentName(), document.getDocumentName());
        assertEquals(dto.getDocumentDescription(), document.getDocumentDescription());
        assertEquals(dto.getInvoiceNumber(), document.getInvoiceNumber());
        assertEquals(dto.getCounterpartyId(), document.getCounterpartyId());
        assertEquals(dto.getPayment().getPaymentMethod(), document.getPaymentMethod().name());
        assertEquals(dto.getPayment().getCurrencyCode(), document.getCurrency().getCurrencyCode());
        assertEquals(dto.getPayment().getExchangeRate(), document.getExchangeRate());

        assertNotNull(document.getAccount());
        assertEquals(dto.getAccountId(), document.getAccount().getId());

        assertFalse(document.getDocumentItems().isEmpty());
        var documentItem = document.getDocumentItems().getFirst();
        assertEquals(DocumentItemType.EXP, documentItem.getType());
        assertNull(documentItem.getIncome());
        assertNotNull(documentItem.getExpense());
        assertEquals(BigDecimal.valueOf(1), documentItem.getExpense().getCount());
        assertEquals(BigDecimal.valueOf(199.99), documentItem.getExpense().getPrice());
        assertEquals("Payment for: Home electricity", documentItem.getExpense().getComment());

        assertNotNull(documentItem.getExpense().getAccount());
        var targetAccount = documentItem.getExpense().getAccount();
        assertEquals(dto.getAccountId(), targetAccount.getId());
        assertEquals(sourceAccount.getName(), targetAccount.getName());

        assertNotNull(documentItem.getExpense().getItem());
        var item = documentItem.getExpense().getItem();
        assertEquals(expectedItem.getId(), item.getId());
        assertEquals(expectedItem.getName(), item.getName());

        assertEquals(15, document.getClass().getDeclaredFields().length);
    }

    @Test
    void toTransferEntity() {
        // Given
        var transferDto = createTransferRequestDto();
        var sourceAccount = accountRepository.findById(transferDto.getAccountId()).orElseThrow();
        var targetAccount = accountRepository.findById(transferDto.getTargetAccountId()).orElseThrow();
        Item transferItem = null;

        // When
        Document document = documentMapper.toEntity(transferDto, sourceAccount, targetAccount, transferItem);

        // Then
        assertEquals(transferDto.getDocumentDate(),        document.getDocumentDate());
        assertEquals(transferDto.getDocumentType().name(), document.getDocumentType().name());
        assertEquals(sourceAccount.getId(),                document.getAccount().getId());
        assertEquals(2,                           document.getDocumentItems().size());
        assertTrue(document.getDocumentItems().stream().anyMatch(di -> di.getType().equals(DocumentItemType.EXP)));
        assertTrue(document.getDocumentItems().stream().anyMatch(di -> di.getType().equals(DocumentItemType.INC)));
    }

    @Test
    void testUpdateDocument() {
        //given
        Document entity = createDocument();
        var dto = createDocumentPutRequestDto();
        var sourceAccount = accountRepository.findById(dto.getAccountId()).orElseThrow();

        var itemId = dto.getDocumentItems().getFirst().getItemId();
        var expectedItem = itemRepository.findById(itemId).orElseThrow();

        //when
        var updated = documentMapper.updateWith(entity, dto, sourceAccount);

        //then
        assertNotNull(updated);
        assertEquals(dto.getDocumentDate(), updated.getDocumentDate());
        assertEquals(dto.getDocumentType().name(), updated.getDocumentType().name());
        assertEquals(dto.getDocumentName(), updated.getDocumentName());
        assertEquals(dto.getDocumentDescription(), updated.getDocumentDescription());
        assertEquals(dto.getInvoiceNumber(), updated.getInvoiceNumber());
        assertEquals(dto.getCounterpartyId(), updated.getCounterpartyId());
        assertEquals(dto.getPayment().getPaymentMethod(), updated.getPaymentMethod().name());
        assertEquals(dto.getPayment().getCurrencyCode(), updated.getCurrency().getCurrencyCode());
        assertEquals(dto.getPayment().getExchangeRate(), updated.getExchangeRate());

        assertNotNull(updated.getAccount());
        assertEquals(dto.getAccountId(), updated.getAccount().getId());

        assertFalse(updated.getDocumentItems().isEmpty());
        var documentItem = updated.getDocumentItems().getFirst();
        assertEquals(DocumentItemType.EXP, documentItem.getType());
        assertNull(documentItem.getIncome());
        assertNotNull(documentItem.getExpense());
        assertEquals(BigDecimal.valueOf(1), documentItem.getExpense().getCount());
        assertEquals(BigDecimal.valueOf(199.99), documentItem.getExpense().getPrice());
        assertEquals("Payment for: Home electricity", documentItem.getExpense().getComment());

        assertNotNull(documentItem.getExpense().getAccount());
        var targetAccount = documentItem.getExpense().getAccount();
        assertEquals(dto.getAccountId(), targetAccount.getId());
        assertEquals(sourceAccount.getName(), targetAccount.getName());

        assertNotNull(documentItem.getExpense().getItem());
        var item = documentItem.getExpense().getItem();
        assertEquals(expectedItem.getId(), item.getId());
        assertEquals(expectedItem.getName(), item.getName());

        assertEquals(15, updated.getClass().getDeclaredFields().length);
    }

    private Document createDocument() {
        var document = Document.builder()
                .withDocumentDate(LocalDate.of(2025, Month.JANUARY, 31))
                .withDocumentType(DocumentType.BILL)
                .withDocumentName("Document Name")
                .withDocumentDescription("Document description")
                .withInvoiceNumber("INV-132435")
                .withAccount(accountRepository.findById(1L).orElseThrow())
                .withCounterpartyId(100L)
                .withPaymentMethod("CASH")
                .withCurrencyCode("GBP")
                .withExchangeRate(BigDecimal.valueOf(1.5))
                .build();
        return document;
    }

    private DocumentRequestDto createDocumentRequestDto() {
        var dto = new DocumentRequestDto()
                .documentName("Home electricity")
                .accountId(6L)
                .documentDescription("Home electricity; January invoice")
                .documentType(DocumentTypeDto.INVOICE)
                .payment(paymentDto())
                .documentDate(LocalDate.of(2025, Month.JANUARY, 3))
                .invoiceNumber("EL/2025/JAN/13579")
                .documentItems(List.of(expenseDocumentItemRequestDto()));
        return dto;
    }

    private DocumentRequestDto createTransferRequestDto() {
        var dto = new DocumentRequestDto()
                .documentDate(LocalDate.of(2025, Month.JANUARY, 23))
                .documentType(DocumentTypeDto.TRANSFER)
                .payment(paymentDto())
                .accountId(6L)
                .targetAccountId(7L)
                .transferAmount(BigDecimal.valueOf(99.99));
        return dto;
    }

    private DocumentRequestDto createDocumentPutRequestDto() {
        var dto = new DocumentRequestDto()
                .documentName("Home electricity")
                .accountId(6L)
                .documentDescription("Home electricity; January invoice")
                .documentType(DocumentTypeDto.INVOICE)
                .payment(paymentDto())
                .documentDate(LocalDate.of(2025, Month.JANUARY, 3))
                .invoiceNumber("EL/2025/JAN/13579")
                .documentItems(List.of(existingExpenseDocumentItemRequestDto()));
        return dto;
    }

    private PaymentDto paymentDto() {
        return new PaymentDto()
                .paymentMethod("EFT")
                .currencyCode("EUR")
                .exchangeRate(BigDecimal.valueOf(1.1));
    }

    private DocumentItemRequestDto expenseDocumentItemRequestDto() {
        return new DocumentItemRequestDto()
                .itemType(ItemTypeEnum.EXP)
                .itemId(5L)
                .itemName("Item_05")
                .itemQuantity(BigDecimal.ONE)
                .itemPrice(BigDecimal.valueOf(199.99))
                .itemDescription("Payment for: Home electricity");
    }

    private DocumentItemRequestDto existingExpenseDocumentItemRequestDto() {
        return expenseDocumentItemRequestDto()
                .seqId(BigDecimal.ONE);
    }

}