package com.veeteq.documentmngr.controller;

import com.veeteq.documentmngr.DocumentMngrApp;
import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.rest.api.AccountController;
import com.veeteq.documentmngr.rest.dto.AccountDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.Currency;

import static com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers.openApi;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DocumentMngrApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountControllerTest extends BaseTest {

    @Value("${open.api.spec.url}")
    private String apiSpecification;

    @Order(value = 0)
    @DisplayName("Test List Accounts")
    @Test
    void testListAccounts() throws Exception {
        mockMvc.perform(get(AccountController.BASE_URL.concat("/v1/accounts"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(openApi().isValid(apiSpecification))
                .andExpect(jsonPath("$.pageSize").value(25))
                .andExpect(jsonPath("$.totalItems").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.data.length()", greaterThan(0)));
    }

    @Order(value = 1)
    @DisplayName("Test Get Account By Id")
    @Test
    void testGetAccountById_WhenAccountExist() throws Exception {
        mockMvc.perform(get(AccountController.BASE_URL.concat("/v1/accounts/{accountId}"), account.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(openApi().isValid(apiSpecification))
                .andExpect(jsonPath("$.accountId").value(account.getId()));
    }

    @Order(value = 2)
    @DisplayName("Test Get Account By Id - When Account Does Not Exist")
    @Test
    void testGetAccountById_WhenAccountDoesNotExists() throws Exception {
        mockMvc.perform(get(AccountController.BASE_URL.concat("/v1/accounts/{accountId}"), 7593)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(openApi().isValid(apiSpecification));
    }

    @Order(value = 3)
    @DisplayName("Test Create Account")
    @Test
    void testCreateAccount() throws Exception {
        AccountDto dto = new AccountDto()
                .accountName("Test Account")
                .accountDescription("Account created to test the POST operation on controller")
                .accountCurrency("EUR")
                .accountImageUrl("abc");
        mockMvc.perform(post(AccountController.BASE_URL.concat("/v1/accounts"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                //.andExpect(openApi().isValid(apiSpecification))
                .andExpect(header().exists("Location"));
    }

    @Order(value = 4)
    @DisplayName("Test Update Account")
    @Test
    void testUpdateAccount_Success() throws Exception {
        long accountIdToSearch = 3;
        AccountDto dto = new AccountDto()
                .accountName("Updated Test Account #3")
                .accountDescription("Test Account #3 with updated description")
                .accountCurrency("NOK")
                .accountImageUrl("http://images.com/account3.png");
        mockMvc.perform(put(AccountController.BASE_URL.concat("/v1/accounts/{accountId}"), accountIdToSearch)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(openApi().isValid(apiSpecification))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountId").value(accountIdToSearch))
                .andExpect(jsonPath("$.accountName").value(dto.getAccountName()))
                .andExpect(jsonPath("$.accountDescription").value(dto.getAccountDescription()))
                .andExpect(jsonPath("$.accountCurrency").value(dto.getAccountCurrency()))
                .andExpect(jsonPath("$.accountImageUrl").value(dto.getAccountImageUrl()));
    }

    @Order(value = 5)
    @DisplayName("Test Update Account - When Account Does Not Exist")
    @Test
    void testUpdateAccount_WhenAccountDoesNotExist() throws Exception {
        long accountIdToSearch = 3456;
        AccountDto dto = new AccountDto()
                .accountName("Updated Test Account #3456")
                .accountDescription("Test Account #3456 with updated description")
                .accountCurrency("NOK")
                .accountImageUrl("http://images.com/account3456.png");
        mockMvc.perform(put(AccountController.BASE_URL.concat("/v1/accounts/{accountId}"), accountIdToSearch)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(openApi().isValid(apiSpecification));
    }

    @Order(value = 6)
    @DisplayName("Test Delete Account - When Account Exists")
    @Test
    void testDeleteAccount_WhenAccountExists() throws Exception {
        var account = Account.builder().withId(14L)
                .withName("Test Account #14")
                .withDescription("Test Account #14")
                .withCurrency(Currency.getInstance("RSD"))
                .withImageUrl("http://images.com/account14.png")
                .build();
        var entity = accountRepository.save(account);

        mockMvc.perform(delete(AccountController.BASE_URL.concat("/v1/accounts/{accountId}"), entity.getId()))
                .andExpect(status().isNoContent())
                .andExpect(openApi().isValid(apiSpecification));

        assert accountRepository.findById(entity.getId()).isEmpty();
    }

    @Order(value = 7)
    @DisplayName("Test Delete Account - When Account Does Not Exists")
    @Test
    void testDeleteAccount_WhenAccountDoesNotExist() throws Exception {
        mockMvc.perform(delete(AccountController.BASE_URL.concat("/v1/accounts/{accountId}"), 9585))
                .andExpect(status().isNotFound())
                .andExpect(openApi().isValid(apiSpecification));
    }

    @Order(value = 8)
    @DisplayName("Test Delete Account - When Account Is Referenced By Document")
    @Test
    void testDeleteAccount_WhenReferencedByDocument() throws Exception {
        var entity = accountRepository.findById(3L).get();

        mockMvc.perform(delete(AccountController.BASE_URL.concat("/v1/accounts/{accountId}"), entity.getId()))
                .andExpect(status().isConflict())
                .andExpect(openApi().isValid(apiSpecification));
    }

}
