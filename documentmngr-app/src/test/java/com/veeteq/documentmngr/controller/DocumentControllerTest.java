package com.veeteq.documentmngr.controller;

import com.veeteq.documentmngr.rest.api.DocumentController;
import com.veeteq.documentmngr.rest.dto.DocumentItemRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentTypeDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class DocumentControllerTest extends BaseTest {

    @DisplayName("Test List Documents")
    @Test
    void testListDocuments() throws Exception {
        var cookie = new MockCookie("cookieParam", "12345");
        mockMvc.perform(get(DocumentController.BASE_URL.concat("/v1/documents"))
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(0)));
    }

    @DisplayName("Test Get Document By Id")
    @Test
    void testGetDocumentById() throws Exception {
        mockMvc.perform(get(DocumentController.BASE_URL.concat("/v1/documents/{document_id}"), document.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentId").value(document.getId()));
    }

    @DisplayName("Test Create Document")
    @Test
    void testCreateDocument() throws Exception {
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
                        .itemType("Expense")
                        .itemId(5L)
                        .itemName("Item_05")
                        .itemQuantity(BigDecimal.ONE)
                        .itemPrice(BigDecimal.valueOf(199.99))
                        .itemDescription("Payment for: Home electricity")));
        var json = objectMapper.writeValueAsString(dto);

        var cookie = new MockCookie("cookieParam", "12345");
        mockMvc.perform(post(DocumentController.BASE_URL.concat("/v1/documents"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .header("Accept-Language", "en-US")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }
}
