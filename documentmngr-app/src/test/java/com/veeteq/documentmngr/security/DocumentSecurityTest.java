package com.veeteq.documentmngr.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veeteq.documentmngr.config.TestSecurityProblemSupportConfig;
import com.veeteq.documentmngr.exception.ExceptionHandling;
import com.veeteq.documentmngr.rest.api.DocumentController;
import com.veeteq.documentmngr.rest.api.ItemController;
import com.veeteq.documentmngr.rest.dto.*;
import com.veeteq.documentmngr.service.DocumentService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.veeteq.documentmngr.config.ApiConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
@Import({SecurityConfig.class,
         ExceptionHandling.class,
         TestSecurityProblemSupportConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DocumentSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(value = 0)
    @DisplayName("Test Security - Should Reject Create Document Request Without Token")
    void shouldRejectCreateDocumentWithoutToken() throws Exception {
        var cookie = new MockCookie("cookieParam", "12345");
        var transactionId = UUID.randomUUID().toString();
        mockMvc.perform(post(DOCUMENTS_URL)
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(ACCEPT_LANGUAGE_HEADER, ACCEPT_LANGUAGE)
                        .header(TRANSACTION_ID, transactionId)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(value = 1)
    @DisplayName("Test Security - Should Allow Create Document Request With Valid Token")
    void shouldAllowCreateDocumentWithValidToken() throws Exception {
        var cookie = new MockCookie("cookieParam", "12345");
        var transactionId = UUID.randomUUID().toString();

        var jwt = Jwt.withTokenValue("valid-token")
                .header("alg", "RS256")
                .subject("jmclane")
                .claim("roles", List.of("DOCUMENT_ADMIN"))
                .issuer("http://localhost:8282")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);

        var request = createSampleRequest();
        var response = createSampleResponse();
        when(documentService.createDocument(any(DocumentRequestDto.class))).thenReturn(response);

        mockMvc.perform(post(DOCUMENTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .header(AUTHORIZATION, "Bearer valid-token")
                        .header(ACCEPT_LANGUAGE_HEADER, ACCEPT_LANGUAGE)
                        .header(TRANSACTION_ID, transactionId)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(LOCATION))
                .andExpect(header().exists(TRANSACTION_ID))
                .andExpect(header().string(TRANSACTION_ID, transactionId));

        verify(documentService).createDocument(any(DocumentRequestDto.class));
    }

    @Test
    @Order(value = 2)
    @DisplayName("Test Security - Should Reject Create Document Request With Invalid Token")
    void shouldRejectCreateDocumentWithInvalidToken() throws Exception {
        var cookie = new MockCookie("cookieParam", "12345");
        var transactionId = UUID.randomUUID().toString();

        when(jwtDecoder.decode("invalid-token")).thenThrow(new BadJwtException("Invalid token"));

        mockMvc.perform(post(DOCUMENTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .header(AUTHORIZATION, "Bearer invalid-token")
                        .header(ACCEPT_LANGUAGE_HEADER, ACCEPT_LANGUAGE)
                        .header(TRANSACTION_ID, transactionId)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(value = 3)
    @DisplayName("Test Security - Should Reject Create Document Request Without Document Admin Role")
    void shouldRejectCreateDocumentWithoutDocumentAdminRole() throws Exception {
        var cookie = new MockCookie("cookieParam", "12345");
        var transactionId = UUID.randomUUID().toString();

        var jwt = Jwt.withTokenValue("account-admin-token")
                .header("alg", "RS256")
                .subject("jmclane")
                .claim("roles", List.of("ACCOUNT_ADMIN"))
                .issuer("http://localhost:8282")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(jwtDecoder.decode("account-admin-token")).thenReturn(jwt);

        mockMvc.perform(post(DOCUMENTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .header(AUTHORIZATION, "Bearer account-admin-token")
                        .header(ACCEPT_LANGUAGE_HEADER, ACCEPT_LANGUAGE)
                        .header(TRANSACTION_ID, transactionId)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    private DocumentRequestDto createSampleRequest() {
        var paymentDto = new PaymentDto()
                .paymentMethod("Credit Card")
                .exchangeRate(BigDecimal.ONE);

        var documentItemRequestDto = new DocumentItemRequestDto()
                .itemType(DocumentItemRequestDto.ItemTypeEnum.EXP)
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
                .documentId(12345L)
                .documentName("October Invoice")
                .documentDate(LocalDate.parse("2023-10-01"))
                .documentType("Invoice")
                .documentAmount(new BigDecimal("11.90"));
    }
}
