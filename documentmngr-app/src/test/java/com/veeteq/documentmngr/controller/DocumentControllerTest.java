package com.veeteq.documentmngr.controller;

import com.veeteq.documentmngr.DocumentMngrApp;
import com.veeteq.documentmngr.integration.addressbook.AddressBookClient;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.DocumentItem;
import com.veeteq.documentmngr.model.DocumentItemType;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.rest.dto.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers.openApi;
import static com.veeteq.documentmngr.config.ApiConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DocumentMngrApp.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DocumentControllerTest extends BaseTest {

    @Value("${open.api.spec.url}")
    private String apiSpecification;

    @MockitoBean
    AddressBookClient addressBookClient;

    @DisplayName("Test List Documents")
    @Test
    @Order(1)
    void testListDocuments() throws Exception {
        var cookie = new MockCookie("cookieParam", "12345");
        var transactionId = UUID.randomUUID().toString();
        mockMvc.perform(get(DOCUMENTS_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .header(ACCEPT_LANGUAGE_HEADER, ACCEPT_LANGUAGE)
                        .header(TRANSACTION_ID, transactionId))
                .andExpect(status().isOk())
                .andExpect(header().exists(TRANSACTION_ID))
                .andExpect(header().string(TRANSACTION_ID, transactionId))
                .andExpect(jsonPath("$.length()", greaterThan(0)));
    }

    @Transactional
    @DisplayName("Test Get Document By Id")
    @Test
    @Order(2)
    void testGetDocumentById() throws Exception {
        var transactionId = UUID.randomUUID().toString();
        mockMvc.perform(get(DOCUMENTS_URL.concat("/{document_id}"), document.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header(TRANSACTION_ID, transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentId").value(document.getId()))
                .andExpect(header().exists(TRANSACTION_ID))
                .andExpect(header().string(TRANSACTION_ID, transactionId))
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
                .andExpect(jsonPath("$.documentItems[0].item.itemCategory.categoryName", equalTo(document.getDocumentItems().get(0).getExpense().getItem().getCategory().getName())))

                .andExpect(jsonPath("$.transferDetails").doesNotExist());
    }

    @Transactional
    @DisplayName("Test Get Transfer Document By Id")
    @Test
    @Order(3)
    void testGetTransferDocumentById() throws Exception {
        var documentItems = transfer.getDocumentItems().stream()
                .sorted(Comparator.comparing(di -> di.getId().getSequenceNumber()))
                .toList();
        var transactionId = UUID.randomUUID().toString();
        mockMvc.perform(get(DOCUMENTS_URL.concat("/{document_id}"), transfer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header(TRANSACTION_ID, transactionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentId").value(transfer.getId()))
                .andExpect(header().exists(TRANSACTION_ID))
                .andExpect(header().string(TRANSACTION_ID, transactionId))
                .andExpect(jsonPath("$.documentId", Long.class).value(transfer.getId()))
                .andExpect(jsonPath("$.documentDate", equalTo(transfer.getDocumentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
                .andExpect(jsonPath("$.documentType", equalTo(transfer.getDocumentType().name())))
                .andExpect(jsonPath("$.documentName", equalTo(transfer.getDocumentName())))
                .andExpect(jsonPath("$.documentComment", equalTo(transfer.getDocumentDescription())))
                .andExpect(jsonPath("$.invoiceNumber").doesNotExist())
                .andExpect(jsonPath("$.documentAmount").value(transfer.getTotalAmount().doubleValue()))
                .andExpect(jsonPath("$.paymentMethod", equalTo(transfer.getPaymentMethod().name())))
                .andExpect(jsonPath("$.currencyCode", equalTo(transfer.getCurrency().getCurrencyCode())))
                .andExpect(jsonPath("$.exchangeRate", equalTo(10.0)))

                .andExpect(jsonPath("$.account.accountId", Long.class).value(transfer.getAccount().getId()))
                .andExpect(jsonPath("$.account.accountName", equalTo(transfer.getAccount().getName())))
                .andExpect(jsonPath("$.account.accountDescription", equalTo(transfer.getAccount().getDescription())))
                .andExpect(jsonPath("$.account.accountCurrency", equalTo(transfer.getAccount().getCurrency().getCurrencyCode())))
                .andExpect(jsonPath("$.account.accountImageUrl", equalTo(transfer.getAccount().getImageUrl())))

                .andExpect(jsonPath("$.documentItemsCount", equalTo(2)))
                .andExpect(jsonPath("$.documentItems", hasSize(2)))

                .andExpect(jsonPath("$.documentItems[0].documentItemId", equalTo(documentItems.get(0).getId().getSequenceNumber())))
                .andExpect(jsonPath("$.documentItems[0].itemQuantity", equalTo(documentItems.get(0).getExpense().getCount().doubleValue())))
                .andExpect(jsonPath("$.documentItems[0].itemPrice", equalTo(documentItems.get(0).getExpense().getPrice().doubleValue())))
                .andExpect(jsonPath("$.documentItems[0].itemComment", equalTo(documentItems.get(0).getExpense().getComment())))
                .andExpect(jsonPath("$.documentItems[0].item.itemId", Long.class).value(documentItems.get(0).getExpense().getItem().getId()))
                .andExpect(jsonPath("$.documentItems[0].item.itemName", equalTo(documentItems.get(0).getExpense().getItem().getName())))
                .andExpect(jsonPath("$.documentItems[0].item.itemCategory.categoryId", Long.class).value(documentItems.get(0).getExpense().getItem().getCategory().getId()))
                .andExpect(jsonPath("$.documentItems[0].item.itemCategory.categoryName", equalTo(documentItems.get(0).getExpense().getItem().getCategory().getName())))

                .andExpect(jsonPath("$.documentItems[1].documentItemId", equalTo(documentItems.get(1).getId().getSequenceNumber())))
                .andExpect(jsonPath("$.documentItems[1].itemQuantity", equalTo(documentItems.get(1).getIncome().getCount().doubleValue())))
                .andExpect(jsonPath("$.documentItems[1].itemPrice", equalTo(documentItems.get(1).getIncome().getPrice().doubleValue())))
                .andExpect(jsonPath("$.documentItems[1].itemComment", equalTo(documentItems.get(1).getIncome().getComment())))
                .andExpect(jsonPath("$.documentItems[1].item.itemId", Long.class).value(documentItems.get(1).getIncome().getItem().getId()))
                .andExpect(jsonPath("$.documentItems[1].item.itemName", equalTo(documentItems.get(1).getIncome().getItem().getName())))
                .andExpect(jsonPath("$.documentItems[1].item.itemCategory.categoryId", Long.class).value(documentItems.get(1).getIncome().getItem().getCategory().getId()))
                .andExpect(jsonPath("$.documentItems[1].item.itemCategory.categoryName", equalTo(documentItems.get(1).getIncome().getItem().getCategory().getName())))

                .andExpect(jsonPath("$.transferDetails").exists());
    }

    @DisplayName("Test Create Document")
    @Test
    @Order(4)
    void testCreateDocument() throws Exception {
        when(addressBookClient
                .getContact(anyLong()))
                .thenReturn(Optional.of(new CounterpartyDto().counterpartyId(1L).counterpartyName("ACME Corporation")));
        var dto = new DocumentRequestDto()
                .documentType(DocumentTypeDto.INVOICE)
                .documentName("Home electricity")
                .documentDescription("Home electricity; January invoice")
                .invoiceNumber("EL/2025/01/ABCDE")
                .accountId(6L)
                .counterpartyId(1L)
                .payment(new PaymentDto()
                        .currencyCode("EUR")
                        .exchangeRate(BigDecimal.valueOf(1.1))
                        .paymentMethod("BANKTRANSFER"))
                .documentDate(LocalDate.of(2025, Month.JANUARY, 3))
                .invoiceNumber("EL/2025/JAN/13579")
                .documentItems(List.of(new DocumentItemRequestDto()
                        .itemType(DocumentItemTypeDto.EXP)
                        .itemId(5L)
                        .itemName("Item_05")
                        .itemQuantity(BigDecimal.ONE)
                        .itemPrice(BigDecimal.valueOf(199.99))
                        .itemDescription("Payment for: Home electricity")));
        var json = objectMapper.writeValueAsString(dto);

        var cookie = new MockCookie("cookieParam", "12345");
        var transactionId = UUID.randomUUID().toString();
        mockMvc.perform(post(DOCUMENTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .header(ACCEPT_LANGUAGE_HEADER, ACCEPT_LANGUAGE)
                        .header(TRANSACTION_ID, transactionId)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists(LOCATION))
                .andExpect(header().exists(TRANSACTION_ID))
                .andExpect(header().string(TRANSACTION_ID, transactionId));
    }

    @DisplayName("Test Create Transfer Document")
    @Test
    @Order(5)
    void testCreateTransferDocument() throws Exception {
        var dto = new DocumentRequestDto()
                .documentType(DocumentTypeDto.TRANSFER)
                .payment(new PaymentDto()
                        .paymentMethod("BANKTRANSFER")
                        .currencyCode("EUR")
                        .exchangeRate(BigDecimal.valueOf(1.1)))
                .accountId(6L)
                .transfer(new TransferRequestDto()
                        .targetAccountId(7L)
                        .transferAmount(BigDecimal.valueOf(99.99)))
                .documentDate(LocalDate.of(2025, Month.JANUARY, 23));
        var json = objectMapper.writeValueAsString(dto);

        var transactionId = UUID.randomUUID().toString();
        mockMvc.perform(post(DOCUMENTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(ACCEPT_LANGUAGE_HEADER, ACCEPT_LANGUAGE)
                        .header(TRANSACTION_ID, transactionId)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().exists(TRANSACTION_ID))
                .andExpect(header().string(TRANSACTION_ID, transactionId));
    }

    @Transactional
    @DisplayName("Test Update Document")
    @Test
    @Order(6)
    void testUpdateDocument() throws Exception {
        var document = documentRepository.findById(1L).get();

        var dto = new DocumentRequestDto()
                .documentDate(LocalDate.of(2025, Month.JANUARY, 23))
                .documentType(DocumentTypeDto.INVOICE)
                .documentName("Home electricity updated")
                .documentDescription("Home electricity; January invoice updated")
                .invoiceNumber("EL/2025/JAN/13579/UPDATED")
                .accountId(4L)
                .counterpartyId(1L)
                .payment(new PaymentDto()
                        .paymentMethod("BANKTRANSFER")
                        .currencyCode("EUR")
                        .exchangeRate(BigDecimal.valueOf(1.1)))
                .version(document.getVersion())
                .documentItems(List.of(new DocumentItemRequestDto()
                        .itemType(DocumentItemTypeDto.EXP)
                        .itemId(5L)
                        .itemName("Item_05")
                        .itemQuantity(BigDecimal.TWO)
                        .itemPrice(BigDecimal.valueOf(199.99))
                        .itemDescription("Payment for: Home electricity")
                        .version(0)));
        var json = objectMapper.writeValueAsString(dto);

        var transactionId = UUID.randomUUID().toString();
        var docId = document.getId();
        mockMvc.perform(put(DOCUMENTS_URL.concat("/{id}"), docId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(TRANSACTION_ID, transactionId)
                        .header("Accept-Language", "en-US")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(header().exists(TRANSACTION_ID))
                .andExpect(header().string(TRANSACTION_ID, transactionId))
                .andExpect(jsonPath("$.documentId", Long.class).value(docId.longValue()))
                .andExpect(jsonPath("$.documentDate", equalTo("2025-01-23")))
                .andExpect(jsonPath("$.documentType", equalTo("INVOICE")))
                .andExpect(jsonPath("$.documentName", equalTo("Home electricity updated")))
                .andExpect(jsonPath("$.documentComment", equalTo("Home electricity; January invoice updated")))
                .andExpect(jsonPath("$.invoiceNumber", equalTo("EL/2025/JAN/13579/UPDATED")))
                .andExpect(jsonPath("$.documentAmount", equalTo(-399.98)))
                .andExpect(jsonPath("$.paymentMethod", equalTo("BANKTRANSFER")))
                .andExpect(jsonPath("$.currencyCode", equalTo("EUR")))
                .andExpect(jsonPath("$.exchangeRate", equalTo(1.1)))

                .andExpect(jsonPath("$.account.accountId", equalTo(4)))
                .andExpect(jsonPath("$.account.accountName", equalTo("DEF")))
                .andExpect(jsonPath("$.account.accountDescription", equalTo("DEF Description")))
                .andExpect(jsonPath("$.account.accountCurrency", equalTo("USD")))
                .andExpect(jsonPath("$.account.accountImageUrl", equalTo("https://image.com/def.png")))
                .andExpect(jsonPath("$.documentItemsCount", equalTo(1)))

                .andExpect(jsonPath("$.documentItems", hasSize(1)))
                .andExpect(jsonPath("$.documentItems[0].documentItemId", equalTo(1)))
                .andExpect(jsonPath("$.documentItems[0].itemQuantity", equalTo(2)))
                .andExpect(jsonPath("$.documentItems[0].itemPrice", equalTo(199.99)))
                .andExpect(jsonPath("$.documentItems[0].itemComment", equalTo("Payment for: Home electricity")))
                .andExpect(jsonPath("$.documentItems[0].item.itemId", equalTo(5)))
                .andExpect(jsonPath("$.documentItems[0].item.itemName", equalTo("Item_05")))
                .andExpect(jsonPath("$.documentItems[0].item.itemCategory.categoryId", equalTo(5)))
                .andExpect(jsonPath("$.documentItems[0].item.itemCategory.categoryName", equalTo("CAT_05")))

                .andExpect(jsonPath("$.transferDetails").doesNotExist());
    }

    @DisplayName("Test Update Transfer Document")
    @Test
    @Order(7)
    void testUpdateTransferDocument() throws Exception {
        var document = documentRepository.findById(2L).get();

        var dto = new DocumentRequestDto()
                .documentDate(LocalDate.of(2025, Month.MAY, 12))
                .documentType(DocumentTypeDto.TRANSFER)
                .documentDescription("My money transfer")
                .accountId(4L)
                .transfer(new TransferRequestDto()
                        .targetAccountId(6L)
                        .transferAmount(BigDecimal.valueOf(1234.56)))
                .payment(new PaymentDto()
                        .paymentMethod("BANKTRANSFER")
                        .currencyCode("EUR")
                        .exchangeRate(BigDecimal.valueOf(1.16)))
                .version(document.getVersion());
        var json = objectMapper.writeValueAsString(dto);

        var transactionId = UUID.randomUUID().toString();
        var docId = document.getId();
        mockMvc.perform(put(DOCUMENTS_URL.concat("/{id}"), docId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(TRANSACTION_ID, transactionId)
                        .header("Accept-Language", "en-US")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(header().exists(TRANSACTION_ID))
                .andExpect(header().string(TRANSACTION_ID, transactionId))
                .andExpect(jsonPath("$.documentId", Long.class).value(docId.longValue()))
                .andExpect(jsonPath("$.documentDate", equalTo("2025-05-12")))
                .andExpect(jsonPath("$.documentType", equalTo("TRANSFER")))
                .andExpect(jsonPath("$.documentName", equalTo("TRANSFER")))
                .andExpect(jsonPath("$.documentComment", equalTo("Transfer of 1 234,56 USD from DEF to 1 064,28 EUR FGH \nTransfer of 99,99 USD from DEF to 10 GBP EFG null")))
                .andExpect(jsonPath("$.invoiceNumber").doesNotExist())
                .andExpect(jsonPath("$.documentAmount", equalTo(-170.28)))
                .andExpect(jsonPath("$.paymentMethod", equalTo("BANKTRANSFER")))
                .andExpect(jsonPath("$.currencyCode", equalTo("EUR")))
                .andExpect(jsonPath("$.exchangeRate", equalTo(1.16)))
                .andExpect(jsonPath("$.documentItemsCount", equalTo(2)))
                .andExpect(jsonPath("$.documentItems", hasSize(2)))

                .andExpect(jsonPath("$.account.accountId", equalTo(4)))
                .andExpect(jsonPath("$.account.accountName", equalTo("DEF")))
                .andExpect(jsonPath("$.account.accountDescription", equalTo("DEF Description")))
                .andExpect(jsonPath("$.account.accountCurrency", equalTo("USD")))
                .andExpect(jsonPath("$.account.accountImageUrl", equalTo("https://image.com/def.png")))

                .andExpect(jsonPath("$.transferDetails").exists())

                .andExpect(jsonPath("$.transferDetails.targetAccount.accountId", equalTo(6)))
                .andExpect(jsonPath("$.transferDetails.targetAccount.accountName", equalTo("FGH")))
                .andExpect(jsonPath("$.transferDetails.targetAccount.accountDescription", equalTo("FGH Description")))
                .andExpect(jsonPath("$.transferDetails.targetAccount.accountCurrency", equalTo("HRK")))
                .andExpect(jsonPath("$.transferDetails.targetAccount.accountImageUrl", equalTo("https://image.com/fgh.png")))

                .andExpect(jsonPath("$.documentItems[0].documentItemId", equalTo(1)))
                .andExpect(jsonPath("$.documentItems[0].itemQuantity", equalTo(1)))
                .andExpect(jsonPath("$.documentItems[0].itemPrice", equalTo(1234.56)))
                .andExpect(jsonPath("$.documentItems[0].itemComment", equalTo("DEF -> FGH")))
                .andExpect(jsonPath("$.documentItems[0].item.itemId", equalTo(10)))
                .andExpect(jsonPath("$.documentItems[0].item.itemName", equalTo("Item_10")))
                .andExpect(jsonPath("$.documentItems[0].item.itemCategory.categoryId", equalTo(5)))
                .andExpect(jsonPath("$.documentItems[0].item.itemCategory.categoryName", equalTo("CAT_05")))

                .andExpect(jsonPath("$.documentItems[1].documentItemId", equalTo(2)))
                .andExpect(jsonPath("$.documentItems[1].itemQuantity", equalTo(1)))
                .andExpect(jsonPath("$.documentItems[1].itemPrice", equalTo(1064.28)))
                .andExpect(jsonPath("$.documentItems[1].itemComment", equalTo("DEF -> FGH")))
                .andExpect(jsonPath("$.documentItems[1].item.itemId", equalTo(10)))
                .andExpect(jsonPath("$.documentItems[1].item.itemName", equalTo("Item_10")))
                .andExpect(jsonPath("$.documentItems[1].item.itemCategory.categoryId", equalTo(5)))
                .andExpect(jsonPath("$.documentItems[1].item.itemCategory.categoryName", equalTo("CAT_05")));
    }

    @DisplayName("Test List Unique Document Names")
    @Test
    @Order(8)
    void shouldReturnSearchResults() throws Exception {
        var transactionId = UUID.randomUUID();
        mockMvc.perform(get(DOCUMENTS_URL.concat("/metadata"))
                        .param("property", "documentName")
                        .param("pattern", "insurance")
                        .param("distinct", "true")
                        .header("Transaction-Id", transactionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Transaction-Id", transactionId.toString()))
                .andExpect(jsonPath("$[0]").exists());
    }

    @DisplayName("Test Invalid Document throws exception")
    @Test
    @Order(9)
    void shouldReturnBadRequestForInvalidDocument() throws Exception {
        var dto = new DocumentRequestDto()
                .documentDate(LocalDate.of(2025, Month.JANUARY, 23))
                .documentType(DocumentTypeDto.INVOICE)
                .documentName("Home electricity updated")
                .documentDescription("Home electricity; January invoice updated")
                .invoiceNumber("EL/2025/JAN/13579/UPDATED")
                .accountId(4L)
                .counterpartyId(null)
                .payment(new PaymentDto()
                        .paymentMethod("BANKTRANSFER")
                        .currencyCode("EUR")
                        .exchangeRate(BigDecimal.valueOf(1.1)))
                .version(document.getVersion())
                .documentItems(List.of(new DocumentItemRequestDto()
                        .itemType(DocumentItemTypeDto.EXP)
                        .itemId(5L)
                        .itemName("Item_05")
                        .itemQuantity(BigDecimal.TWO)
                        .itemPrice(BigDecimal.valueOf(199.99))
                        .itemDescription("Payment for: Home electricity")
                        .version(0)));

        mockMvc.perform(post(DOCUMENTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(TRANSACTION_ID, UUID.randomUUID())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Counterparty is required for Bill and Invoice documents"));
    }

    @Order(value = 10)
    @DisplayName("Test Delete Document - When Document Exists")
    @Test
    void testDeleteDocument_WhenDocumentExists() throws Exception {
        var transactionId = UUID.randomUUID().toString();
        var document = Document.builder()
                .withDocumentDate(LocalDate.now())
                .withDocumentType(DocumentType.BILL)
                .withDocumentName("Home water invoice")
                .withDocumentDescription("Water supply invoice")
                .withInvoiceNumber("WATER-123456-INV")
                .withAccount(account)
                .withCounterpartyId(1L)
                .withPaymentMethod("CASH")
                .withCurrencyCode("PLN")
                .withExchangeRate(BigDecimal.ONE)
                .build();
        document.addToDocumentItems(DocumentItem.builder()
                .withDocument(document)
                .withDocumentItemType(DocumentItemType.EXP)
                .withItem(item)
                .withItemComment("WATER-123456-INV")
                .withItemPrice(BigDecimal.valueOf(100))
                .withItemQuantity(BigDecimal.ONE)
                .build());
        var entity = documentRepository.save(document);

        mockMvc.perform(delete(DOCUMENTS_URL.concat("/{documentId}"), entity.getId())
                        .param("version", document.getVersion().toString())
                        .header(ACCEPT_LANGUAGE_HEADER, ACCEPT_LANGUAGE)
                        .header(TRANSACTION_ID, transactionId))
                .andExpect(status().isNoContent())
                .andExpect(openApi().isValid(apiSpecification));
        assertThat(documentRepository.findById(entity.getId())).isEmpty();
    }

    @Order(value = 11)
    @DisplayName("Test Delete Document - When Document Does Not Exists")
    @Test
    void testDeleteDocument_WhenDocumentDoesNotExist() throws Exception {
        var transactionId = UUID.randomUUID().toString();
        mockMvc.perform(delete(DOCUMENTS_URL.concat("/{documentId}"), 95853)
                        .param("version", "1")
                        .header(ACCEPT_LANGUAGE_HEADER, ACCEPT_LANGUAGE)
                        .header(TRANSACTION_ID, transactionId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(openApi().isValid(apiSpecification));
    }

    @DisplayName("Test Delete Document - When Version Is Stale")
    @Test
    void testDeleteDocument_WhenVersionIsStale() throws Exception {
        // 1. Arrange: persist a new document (version 0)
        // 2. Simulate "someone else saved it": change a field and save again,
        //    so the stored version becomes 1
        // 3. Act: DELETE with version=0
        // 4. Assert: 409 + openApi().isValid(...)
        // 5. Assert the side effect: the document STILL exists
    }

    @DisplayName("Test Delete Document - When Version Is Missing")
    @Test
    void testDeleteDocument_WhenVersionIsMissing() throws Exception {
        // DELETE without the version parameter -> expect 400 + openApi().isValid(...)
    }
}
