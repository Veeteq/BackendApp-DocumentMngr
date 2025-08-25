package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.*;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.rest.dto.*;
import com.veeteq.documentmngr.rest.dto.DocumentItemRequestDto.ItemTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DocumentMapperTest {

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private AccountRepository accountRepository;

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

        //when
        Document document = documentMapper.toEntity(dto, sourceAccount);

        //then
        assertEquals(dto.getDocumentDate(), document.getDocumentDate());
        assertEquals(dto.getDocumentType().name(), document.getDocumentType().name());
        assertEquals(dto.getDocumentName(), document.getDocumentName());
        assertEquals(dto.getDocumentDescription(), document.getDocumentDescription());
        assertEquals(dto.getInvoiceNumber(), document.getInvoiceNumber());
        assertEquals(dto.getCounterpartyId(), document.getCounterpartyId());
        assertEquals(dto.getPaymentMethod(), document.getPaymentMethod().name());
        assertEquals(dto.getCurrencyCode(), document.getCurrency().getCurrencyCode());
        assertEquals(dto.getExchangeRate(), document.getExchangeRate());

        assertNotNull(document.getAccount());
        assertEquals(dto.getAccountId(), document.getAccount().getId());

        assertTrue(document.getDocumentItems().size() > 0);
        var documentItem = document.getDocumentItems().get(0);
        assertEquals(DocumentItemType.EXP, documentItem.getType());
        assertNull(documentItem.getIncome());
        assertNotNull(documentItem.getExpense());
        assertEquals(BigDecimal.valueOf(1), documentItem.getExpense().getCount());
        assertEquals(BigDecimal.valueOf(199.99), documentItem.getExpense().getPrice());
        assertEquals("Payment for: Home electricity", documentItem.getExpense().getComment());

        assertNotNull(documentItem.getExpense().getAccount());
        var targetAccount = documentItem.getExpense().getAccount();
        assertEquals(dto.getAccountId(), targetAccount.getId());
        assertEquals("FGH", targetAccount.getName());

        assertNotNull(documentItem.getExpense().getItem());
        var item = documentItem.getExpense().getItem();
        assertEquals(5L, item.getId());
        assertEquals("Item_05", item.getName());

        assertEquals(15, document.getClass().getDeclaredFields().length);
    }

    @Test
    void toTransferEntity() {
        // Given
        var transferDto = createTransferRequestDto();
        Account sourceAccount = accountRepository.findById(transferDto.getAccountId()).orElseThrow();
        Account targetAccount = accountRepository.findById(transferDto.getTargetAccountId()).orElseThrow();
        Item trnasferItem = null;
        // When
        Document document = documentMapper.toEntity(transferDto, sourceAccount, targetAccount, trnasferItem);

        // Then
        assertEquals(transferDto.getDocumentDate(),        document.getDocumentDate());
        assertEquals(transferDto.getDocumentType().name(), document.getDocumentType().name());
        assertEquals(sourceAccount.getId(),                document.getAccount().getId());
        assertEquals(2,                           document.getDocumentItems().size());
        assertNotNull(document.getDocumentItems().stream().filter(di -> di.getType().equals(DocumentItemType.EXP)));
        assertNotNull(document.getDocumentItems().stream().filter(di -> di.getType().equals(DocumentItemType.INC)));
    }

    @Test
    void testUpdateDocument() {
        Document entity = createDocument();
        var dto = createDocumentPutRequestDto();
        Account account = accountRepository.findById(dto.getAccountId()).orElseThrow();
        var updated = documentMapper.updateWith(entity, dto, account);

        //then
        assertNotNull(updated);
        assertEquals(dto.getDocumentDate(), updated.getDocumentDate());
        assertEquals(dto.getDocumentType().name(), updated.getDocumentType().name());
        assertEquals(dto.getDocumentName(), updated.getDocumentName());
        assertEquals(dto.getDocumentDescription(), updated.getDocumentDescription());
        assertEquals(dto.getInvoiceNumber(), updated.getInvoiceNumber());
        assertEquals(dto.getCounterpartyId(), updated.getCounterpartyId());
        assertEquals(dto.getPaymentMethod(), updated.getPaymentMethod().name());
        assertEquals(dto.getCurrencyCode(), updated.getCurrency().getCurrencyCode());
        assertEquals(dto.getExchangeRate(), updated.getExchangeRate());

        assertNotNull(updated.getAccount());
        assertEquals(dto.getAccountId(), updated.getAccount().getId());

        assertTrue(updated.getDocumentItems().size() > 0);
        var documentItem = updated.getDocumentItems().get(0);
        assertEquals(DocumentItemType.EXP, documentItem.getType());
        assertNull(documentItem.getIncome());
        assertNotNull(documentItem.getExpense());
        assertEquals(BigDecimal.valueOf(1), documentItem.getExpense().getCount());
        assertEquals(BigDecimal.valueOf(199.99), documentItem.getExpense().getPrice());
        assertEquals("Payment for: Home electricity", documentItem.getExpense().getComment());

        assertNotNull(documentItem.getExpense().getAccount());
        var targetAccount = documentItem.getExpense().getAccount();
        assertEquals(dto.getAccountId(), targetAccount.getId());
        assertEquals("FGH", targetAccount.getName());

        assertNotNull(documentItem.getExpense().getItem());
        var item = documentItem.getExpense().getItem();
        assertEquals(5L, item.getId());
        assertEquals("Item_05", item.getName());

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
                .currencyCode("EUR")
                .exchangeRate(BigDecimal.valueOf(1.1))
                .documentDate(LocalDate.of(2025, Month.JANUARY, 3))
                .invoiceNumber("EL/2025/JAN/13579")
                .paymentMethod("EFT")
                .documentItems(List.of(new DocumentItemRequestDto()
                        .itemType(ItemTypeEnum.EXP)
                        .itemId(5L)
                        .itemName("Item_05")
                        .itemQuantity(BigDecimal.ONE)
                        .itemPrice(BigDecimal.valueOf(199.99))
                        .itemDescription("Payment for: Home electricity")));
        return dto;
    }

    private DocumentRequestDto createTransferRequestDto() {
        var dto = new DocumentRequestDto()
                .documentDate(LocalDate.of(2025, Month.JANUARY, 23))
                .documentType(DocumentTypeDto.TRANSFER)
                .paymentMethod("EFT")
                .accountId(6L)
                .targetAccountId(7L)
                .transferAmount(BigDecimal.valueOf(99.99))
                .currencyCode("EUR")
                .exchangeRate(BigDecimal.valueOf(1.1));
        return dto;
    }

    private DocumentRequestDto createDocumentPutRequestDto() {
        var dto = new DocumentRequestDto()
                .documentName("Home electricity")
                .accountId(6L)
                .documentDescription("Home electricity; January invoice")
                .documentType(DocumentTypeDto.INVOICE)
                .currencyCode("EUR")
                .exchangeRate(BigDecimal.valueOf(1.1))
                .documentDate(LocalDate.of(2025, Month.JANUARY, 3))
                .invoiceNumber("EL/2025/JAN/13579")
                .paymentMethod("EFT")
                .documentItems(List.of(new DocumentItemRequestDto()
                        .seqId(BigDecimal.valueOf(1))
                        .itemType(DocumentItemRequestDto.ItemTypeEnum.EXP)
                        .itemId(5L)
                        .itemName("Item_05")
                        .itemQuantity(BigDecimal.ONE)
                        .itemPrice(BigDecimal.valueOf(199.99))
                        .itemDescription("Payment for: Home electricity")));
        return dto;
    }
}