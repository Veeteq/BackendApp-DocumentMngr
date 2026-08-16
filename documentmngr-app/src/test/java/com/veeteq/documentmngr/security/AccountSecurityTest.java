package com.veeteq.documentmngr.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veeteq.documentmngr.config.TestSecurityProblemSupportConfig;
import com.veeteq.documentmngr.exception.ExceptionHandling;
import com.veeteq.documentmngr.rest.api.AccountController;
import com.veeteq.documentmngr.rest.dto.AccountDto;
import com.veeteq.documentmngr.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.veeteq.documentmngr.config.ApiConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import({SecurityConfig.class,
        ExceptionHandling.class,
        TestSecurityProblemSupportConfig.class})
public class AccountSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test Security - Should Reject Create Account Request Without Account Admin Role")
    void shouldRejectCreateAccountWithoutAccountAdminRole() throws Exception {
        var cookie = new MockCookie("cookieParam", "12345");
        var transactionId = UUID.randomUUID().toString();

        var jwt = Jwt.withTokenValue("document-admin-token")
                .header("alg", "RS256")
                .subject("jmclane")
                .claim("roles", List.of("DOCUMENT_ADMIN"))
                .issuer("http://localhost:8282")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(jwtDecoder.decode("document-admin-token")).thenReturn(jwt);

        mockMvc.perform(post(ACCOUNTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .header(AUTHORIZATION, "Bearer document-admin-token")
                        .header(ACCEPT_LANGUAGE_HEADER, ACCEPT_LANGUAGE)
                        .header(TRANSACTION_ID, transactionId)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Test Security - Should Allow Create Account Request With Account Admin Role")
    void shouldAllowCreateAccountWithAccountAdminRole() throws Exception {
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

        var request = createSampleRequest();
        var response = createSampleResponse();
        when(accountService.saveAccount(any(AccountDto.class))).thenReturn(response);

        mockMvc.perform(post(ACCOUNTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .header(AUTHORIZATION, "Bearer account-admin-token")
                        .header(ACCEPT_LANGUAGE_HEADER, ACCEPT_LANGUAGE)
                        .header(TRANSACTION_ID, transactionId)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    private AccountDto createSampleResponse() {
        var response = new AccountDto()
                .accountId(10011L)
                .accountName("Security Test Account")
                .accountDescription("Created from security test")
                .accountCurrency("EUR")
                .accountImageUrl("image.png");
        return response;
    }

    private AccountDto createSampleRequest() {
        var request = new AccountDto()
                .accountName("Security Test Account")
                .accountDescription("Created from security test")
                .accountCurrency("EUR")
                .accountImageUrl("image.png");
        return request;
    }
}