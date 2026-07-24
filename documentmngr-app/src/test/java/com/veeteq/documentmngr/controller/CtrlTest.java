package com.veeteq.documentmngr.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veeteq.documentmngr.rest.api.DocumentController;
import com.veeteq.documentmngr.rest.dto.DocumentItemRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentItemRequestDto.ItemTypeEnum;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import com.veeteq.documentmngr.rest.dto.DocumentTypeDto;
import com.veeteq.documentmngr.rest.dto.PaymentDto;
import com.veeteq.documentmngr.service.DocumentService;

@WebMvcTest(DocumentController.class)
class CtrlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateDocument() throws Exception {
        DocumentRequestDto request = createSampleRequest();
        DocumentResponseDto response = createSampleResponse();

        when(documentService.createDocument(any(DocumentRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/v1/documents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.documentId").value(response.getDocumentId()))
            .andExpect(jsonPath("$.documentName").value(response.getDocumentName()));

        verify(documentService).createDocument(any(DocumentRequestDto.class));
    }

    private DocumentRequestDto createSampleRequest() {
        return new DocumentRequestDto()
            .documentDate(LocalDate.parse("2023-10-01"))
            .documentType(DocumentTypeDto.INVOICE)
            .documentName("October Invoice")
            .accountId(67890L)
            .payment(new PaymentDto().paymentMethod("Credit Card"))
            .documentItems(List.of(
                new DocumentItemRequestDto()
                    .itemType(ItemTypeEnum.EXP)
                    .itemId(98765L)
                    .itemQuantity(new BigDecimal("10.5"))
                    .itemPrice(new BigDecimal("99.99"))
            ));
    }

    private DocumentResponseDto createSampleResponse() {
        return new DocumentResponseDto()
            .documentId(12345L)
            .documentName("October Invoice")
            .documentDate(LocalDate.parse("2023-10-01"))
            .documentType("Invoice")
            .documentAmount(new BigDecimal("11.90"));
    }
}
