package com.veeteq.documentmngr.controller;

import com.veeteq.documentmngr.DocumentMngrApp;
import com.veeteq.documentmngr.rest.api.DocumentController;
import com.veeteq.documentmngr.rest.dto.DocumentItemRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentItemRequestDto.ItemTypeEnum;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentTypeDto;
import jakarta.transaction.Transactional;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DocumentMngrApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DocumentControllerTest extends BaseTest {

    @DisplayName("Test List Documents")
    @Test
    @Order(1)
    void testListDocuments() throws Exception {
        var trnId = UUID.randomUUID().toString();
        mockMvc.perform(get(DocumentController.BASE_URL.concat("/v1/documents"))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Transaction-Id", trnId)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Transaction-Id"))
                .andExpect(header().string("Transaction-Id", trnId))
                .andExpect(jsonPath("$.length()", greaterThan(0)));
    }

    @Transactional
    @DisplayName("Test Get Document By Id")
    @Test
    @Order(2)
    void testGetDocumentById() throws Exception {
        var trnId = UUID.randomUUID().toString();
        var result = mockMvc.perform(get(DocumentController.BASE_URL.concat("/v1/documents/{document_id}"), document.getId())
                        .header("Transaction-Id", trnId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("Transaction-Id"))
                .andExpect(header().string("Transaction-Id", trnId))
                .andExpect(jsonPath("$.documentId", Long.class).value(document.getId()))
                .andExpect(jsonPath("$.documentDate", equalTo(document.getDocumentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
                .andExpect(jsonPath("$.documentType", equalTo(document.getDocumentType().name())))
                .andExpect(jsonPath("$.documentName", equalTo(document.getDocumentName())))
                .andExpect(jsonPath("$.documentComment", equalTo(document.getDocumentDescription())))
                .andExpect(jsonPath("$.invoiceNumber", equalTo(document.getInvoiceNumber())))
                .andExpect(jsonPath("$.documentAmount").value(document.getTotalAmount().doubleValue()))
                .andExpect(jsonPath("$.paymentMethod", equalTo(document.getPaymentMethod().name())))
                .andExpect(jsonPath("$.currencyCode", equalTo(document.getCurrency().getCurrencyCode())))
                .andExpect(jsonPath("$.exchangeRate", equalTo(1.0)))
                .andExpect(jsonPath("$.account.accountId", Long.class).value(document.getAccount().getId()))
                .andExpect(jsonPath("$.account.accountName", equalTo(document.getAccount().getName())))
                .andExpect(jsonPath("$.account.accountDescription", equalTo(document.getAccount().getDescription())))
                .andExpect(jsonPath("$.account.accountCurrency", equalTo(document.getAccount().getCurrency().getCurrencyCode())))
                .andExpect(jsonPath("$.account.accountImageUrl", equalTo(document.getAccount().getImageUrl())))
                .andExpect(jsonPath("$.documentItemsCount", equalTo(1)))
                .andExpect(jsonPath("$.documentItems", hasSize(1)))
                .andExpect(jsonPath("$.documentItems[0].documentItemId", equalTo(document.getDocumentItems().get(0).getId().getSequenceNumber())))
                .andExpect(jsonPath("$.documentItems[0].itemQuantity", equalTo(document.getDocumentItems().get(0).getExpense().getCount().doubleValue())))
                .andExpect(jsonPath("$.documentItems[0].itemPrice", equalTo(document.getDocumentItems().get(0).getExpense().getPrice().doubleValue())))
                .andExpect(jsonPath("$.documentItems[0].itemComment", equalTo(document.getDocumentItems().get(0).getExpense().getComment())))
                .andExpect(jsonPath("$.documentItems[0].item.itemId", Long.class).value(document.getDocumentItems().get(0).getExpense().getItem().getId()))
                .andExpect(jsonPath("$.documentItems[0].item.itemName", equalTo(document.getDocumentItems().get(0).getExpense().getItem().getName())))
                .andExpect(jsonPath("$.documentItems[0].item.itemCategory.categoryId", Long.class).value(document.getDocumentItems().get(0).getExpense().getItem().getCategory().getId()))
                .andExpect(jsonPath("$.documentItems[0].item.itemCategory.categoryName", equalTo(document.getDocumentItems().get(0).getExpense().getItem().getCategory().getName())));
    }

    @DisplayName("Test Create Document")
    @Test
    @Order(3)
    void testCreateDocument() throws Exception {
        var dto = new DocumentRequestDto()
                .documentType(DocumentTypeDto.INVOICE)
                .documentName("Home electricity")
                .documentDescription("Home electricity; January invoice")
                .accountId(6L)
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
        var json = objectMapper.writeValueAsString(dto);

        var trnId = UUID.randomUUID().toString();
        mockMvc.perform(post(DocumentController.BASE_URL.concat("/v1/documents"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Transaction-Id", trnId)
                        .header("Accept-Language", "en-US")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().exists("Transaction-Id"))
                .andExpect(header().string("Transaction-Id", trnId));
    }

    @DisplayName("Test Create Transfer Document")
    @Test
    @Order(4)
    void testCreateTransferDocument() throws Exception {
        var dto = new DocumentRequestDto()
                .documentType(DocumentTypeDto.TRANSFER)
                .paymentMethod("EFT")
                .accountId(6L)
                .targetAccountId(7L)
                .transferAmount(BigDecimal.valueOf(99.99))
                .currencyCode("EUR")
                .exchangeRate(BigDecimal.valueOf(1.1))
                .documentDate(LocalDate.of(2025, Month.JANUARY, 23));
        var json = objectMapper.writeValueAsString(dto);

        var trnId = UUID.randomUUID().toString();
        mockMvc.perform(post(DocumentController.BASE_URL.concat("/v1/documents"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Transaction-Id", trnId)
                        .header("Accept-Language", "en-US")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().exists("Transaction-Id"))
                .andExpect(header().string("Transaction-Id", trnId));
    }

    @Transactional
    @DisplayName("Test Update Document")
    @Test
    @Order(5)
    void testUpdateDocument() throws Exception {
        var document = documentRepository.findById(1L).get();

        var dto = new DocumentRequestDto()
                .documentDate(LocalDate.of(2025, Month.JANUARY, 23))
                .documentType(DocumentTypeDto.INVOICE)
                .documentName("Home electricity updated")
                .documentDescription("Home electricity; January invoice updated")
                .invoiceNumber("EL/2025/JAN/13579/UPDATED")
                .accountId(4L)
                .currencyCode("EUR")
                .exchangeRate(BigDecimal.valueOf(1.1))
                .paymentMethod("EFT")
                .version(document.getVersion())
                .documentItems(List.of(new DocumentItemRequestDto()
                        .itemType(ItemTypeEnum.EXP)
                        .itemId(5L)
                        .itemName("Item_05")
                        .itemQuantity(BigDecimal.TWO)
                        .itemPrice(BigDecimal.valueOf(199.99))
                        .itemDescription("Payment for: Home electricity")
                        .version(0)));
        var json = objectMapper.writeValueAsString(dto);

        var trnId = UUID.randomUUID().toString();
        var docId = document.getId();
        mockMvc.perform(put(DocumentController.BASE_URL.concat("/v1/documents/{id}"), docId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Transaction-Id", trnId)
                        .header("Accept-Language", "en-US")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(header().exists("Transaction-Id"))
                .andExpect(header().string("Transaction-Id", trnId))
                .andExpect(jsonPath("$.documentId", Long.class).value(docId.longValue()))
                .andExpect(jsonPath("$.documentDate", equalTo("2025-01-23")))
                .andExpect(jsonPath("$.documentType", equalTo("INVOICE")))
                .andExpect(jsonPath("$.documentName", equalTo("Home electricity updated")))
                .andExpect(jsonPath("$.documentComment", equalTo("Home electricity; January invoice updated")))
                .andExpect(jsonPath("$.invoiceNumber", equalTo("EL/2025/JAN/13579/UPDATED")))
                .andExpect(jsonPath("$.documentAmount", equalTo(-399.98)))
                .andExpect(jsonPath("$.paymentMethod", equalTo("EFT")))
                .andExpect(jsonPath("$.currencyCode", equalTo("EUR")))
                .andExpect(jsonPath("$.exchangeRate", equalTo(1.1)))
                .andExpect(jsonPath("$.account.accountId", equalTo(4)))
                .andExpect(jsonPath("$.account.accountName", equalTo("DEF")))
                .andExpect(jsonPath("$.account.accountDescription", equalTo("DEF Description")))
                .andExpect(jsonPath("$.account.accountCurrency", equalTo("USD")))
                .andExpect(jsonPath("$.account.accountImageUrl", equalTo("https://image.com/def.png")))
                .andExpect(jsonPath("$.documentItemsCount", equalTo(1)))
                .andExpect(jsonPath("$.documentItems", hasSize(1)))
                .andExpect(jsonPath("$.documentItems[0].documentItemId", equalTo(5)))
                .andExpect(jsonPath("$.documentItems[0].itemQuantity", equalTo(2)))
                .andExpect(jsonPath("$.documentItems[0].itemPrice", equalTo(199.99)))
                .andExpect(jsonPath("$.documentItems[0].itemComment", equalTo("Payment for: Home electricity")))
                .andExpect(jsonPath("$.documentItems[0].item.itemId", equalTo(5)))
                .andExpect(jsonPath("$.documentItems[0].item.itemName", equalTo("Item_05")))
                .andExpect(jsonPath("$.documentItems[0].item.itemCategory.categoryId", equalTo(5)))
                .andExpect(jsonPath("$.documentItems[0].item.itemCategory.categoryName", equalTo("CAT_05")));
    }

    @DisplayName("Test Update Transfer Document")
    @Test
    @Order(6)
    void testUpdateTransferDocument() throws Exception {
        var document = documentRepository.findById(2L).get();

        var dto = new DocumentRequestDto()
                .documentDate(LocalDate.of(2025, Month.MAY, 12))
                .documentType(DocumentTypeDto.TRANSFER)
                .accountId(4L)
                .targetAccountId(6L)
                .transferAmount(BigDecimal.valueOf(1234.56))
                .currencyCode("EUR")
                .exchangeRate(BigDecimal.valueOf(1.16))
                .paymentMethod("EFT")
                .version(document.getVersion());
        var json = objectMapper.writeValueAsString(dto);

        var trnId = UUID.randomUUID().toString();
        var docId = document.getId();
        mockMvc.perform(put(DocumentController.BASE_URL.concat("/v1/documents/{id}"), docId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Transaction-Id", trnId)
                        .header("Accept-Language", "en-US")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(header().exists("Transaction-Id"))
                .andExpect(header().string("Transaction-Id", trnId))
                .andExpect(jsonPath("$.documentId", Long.class).value(docId.longValue()))
                .andExpect(jsonPath("$.documentDate", equalTo("2025-05-12")))
                .andExpect(jsonPath("$.documentType", equalTo("TRANSFER")))
                .andExpect(jsonPath("$.documentName", equalTo("TRANSFER")))
                .andExpect(jsonPath("$.documentComment", equalTo("Transfer of 1 234,56 USD from DEF to 1 064,28 EUR FGH")))
                .andExpect(jsonPath("$.invoiceNumber").doesNotExist())
                .andExpect(jsonPath("$.documentAmount", equalTo(-170.28)))
                .andExpect(jsonPath("$.paymentMethod", equalTo("EFT")))
                .andExpect(jsonPath("$.currencyCode", equalTo("EUR")))
                .andExpect(jsonPath("$.exchangeRate", equalTo(1.16)))
                .andExpect(jsonPath("$.documentItemsCount", equalTo(2)))
                .andExpect(jsonPath("$.documentItems", hasSize(2)))

                .andExpect(jsonPath("$.account.accountId", equalTo(4)))
                .andExpect(jsonPath("$.account.accountName", equalTo("DEF")))
                .andExpect(jsonPath("$.account.accountDescription", equalTo("DEF Description")))
                .andExpect(jsonPath("$.account.accountCurrency", equalTo("USD")))
                .andExpect(jsonPath("$.account.accountImageUrl", equalTo("https://image.com/def.png")))

                .andExpect(jsonPath("$.targetAccount.accountId", equalTo(6)))
                .andExpect(jsonPath("$.targetAccount.accountName", equalTo("FGH")))
                .andExpect(jsonPath("$.targetAccount.accountDescription", equalTo("FGH Description")))
                .andExpect(jsonPath("$.targetAccount.accountCurrency", equalTo("HRK")))
                .andExpect(jsonPath("$.targetAccount.accountImageUrl", equalTo("https://image.com/fgh.png")))

                .andExpect(jsonPath("$.documentItems[0].documentItemId", equalTo(6)))
                .andExpect(jsonPath("$.documentItems[0].itemQuantity", equalTo(1)))
                .andExpect(jsonPath("$.documentItems[0].itemPrice", equalTo(1234.56)))
                .andExpect(jsonPath("$.documentItems[0].itemComment", equalTo("DEF -> FGH")))
                .andExpect(jsonPath("$.documentItems[0].item.itemId", equalTo(10)))
                .andExpect(jsonPath("$.documentItems[0].item.itemName", equalTo("Item_10")))
                .andExpect(jsonPath("$.documentItems[0].item.itemCategory.categoryId", equalTo(5)))
                .andExpect(jsonPath("$.documentItems[0].item.itemCategory.categoryName", equalTo("CAT_05")))

                .andExpect(jsonPath("$.documentItems[1].documentItemId", equalTo(3)))
                .andExpect(jsonPath("$.documentItems[1].itemQuantity", equalTo(1)))
                .andExpect(jsonPath("$.documentItems[1].itemPrice", equalTo(1064.28)))
                .andExpect(jsonPath("$.documentItems[1].itemComment", equalTo("DEF -> FGH")))
                .andExpect(jsonPath("$.documentItems[1].item.itemId", equalTo(10)))
                .andExpect(jsonPath("$.documentItems[1].item.itemName", equalTo("Item_10")))
                .andExpect(jsonPath("$.documentItems[1].item.itemCategory.categoryId", equalTo(5)))
                .andExpect(jsonPath("$.documentItems[1].item.itemCategory.categoryName", equalTo("CAT_05")));
    }
}
