package com.veeteq.documentmngr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veeteq.documentmngr.rest.api.DocumentController;
import com.veeteq.documentmngr.rest.dto.*;
import com.veeteq.documentmngr.rest.dto.DocumentItemRequestDto.ItemTypeEnum;
import com.veeteq.documentmngr.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.veeteq.documentmngr.config.ApiConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
class CtrlTest {
    private static final Long CREATED_DOCUMENT_ID = 12345L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateDocument() throws Exception {
        var transactionId = UUID.randomUUID().toString();
        var request = createSampleRequest();
        var response = createSampleResponse();

        when(documentService.createDocument(any(DocumentRequestDto.class))).thenReturn(response);

        mockMvc.perform(post(DOCUMENTS_URL)
                        .header(ACCEPT_LANGUAGE_HEADER, ACCEPT_LANGUAGE)
                        .header(TRANSACTION_ID, transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string(LOCATION, "http://localhost/api/v1/documents/" + CREATED_DOCUMENT_ID))
                .andExpect(header().exists(TRANSACTION_ID))
                .andExpect(header().string(TRANSACTION_ID, transactionId));

        verify(documentService).createDocument(any(DocumentRequestDto.class));
    }

    private DocumentRequestDto createSampleRequest() {
        var paymentDto = new PaymentDto()
                .paymentMethod("Credit Card")
                .exchangeRate(BigDecimal.ONE);
        var documentItemRequestDto = new DocumentItemRequestDto()
                .itemType(ItemTypeEnum.EXP)
                .itemId(98765L)
                .itemQuantity(new BigDecimal("10.5"))
                .itemPrice(new BigDecimal("99.99"));
        return new DocumentRequestDto()
            .documentDate(LocalDate.parse("2023-10-01"))
            .documentType(DocumentTypeDto.INVOICE)
            .documentName("October Invoice")
            .accountId(67890L)
            .payment(paymentDto)
            .documentItems(List.of(documentItemRequestDto));
    }

    private DocumentResponseDto createSampleResponse() {
        return new DocumentResponseDto()
            .documentId(CREATED_DOCUMENT_ID)
            .documentName("October Invoice")
            .documentDate(LocalDate.parse("2023-10-01"))
            .documentType("Invoice")
            .documentAmount(new BigDecimal("11.90"));
    }
}
