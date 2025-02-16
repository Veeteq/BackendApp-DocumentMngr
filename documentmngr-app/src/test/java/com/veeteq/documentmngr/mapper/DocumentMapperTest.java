package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.DocumentItemType;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.rest.dto.DocumentItemRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import com.veeteq.documentmngr.rest.dto.DocumentTypeDto;
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

        assertEquals(17, dto.getClass().getDeclaredFields().length);
    }

    @Test
    void toEntity() {
        //given
        DocumentRequestDto dto = createDocumentRequestDto();

        //when
        Document document = documentMapper.toEntity(dto, accountRepository);

        //then
        assertEquals(dto.getDate(), document.getDocumentDate());
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
        var account = documentItem.getExpense().getAccount();
        assertEquals(dto.getAccountId(), account.getId());
        assertEquals("FGH", account.getName());

        assertNotNull(documentItem.getExpense().getItem());
        var item = documentItem.getExpense().getItem();
        assertEquals(5L, item.getId());
        assertEquals("Item_05", item.getName());

        assertEquals(15, document.getClass().getDeclaredFields().length);
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
        DocumentRequestDto dto = new DocumentRequestDto()
                .documentName("Home electricity")
                .accountId(6L)
                .documentDescription("Home electricity; January invoice")
                .documentType(DocumentTypeDto.INVOICE)
                .currencyCode("EUR")
                .exchangeRate(BigDecimal.valueOf(1.1))
                .date(LocalDate.of(2025, Month.JANUARY, 3))
                .invoiceNumber("EL/2025/JAN/13579")
                .paymentMethod("EFT")
                .documentItems(List.of(new DocumentItemRequestDto()
                        .itemType("EXP")
                        .itemId(5L)
                        .itemName("Item_05")
                        .itemQuantity(BigDecimal.ONE)
                        .itemPrice(BigDecimal.valueOf(199.99))
                        .itemDescription("Payment for: Home electricity")));
        return dto;
    }

    private DocumentRequestDto createTransferRequestDto() {
        DocumentRequestDto dto = new DocumentRequestDto();
        return dto;
    }
}