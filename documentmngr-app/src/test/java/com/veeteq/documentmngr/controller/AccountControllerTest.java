package com.veeteq.documentmngr.controller;

import com.veeteq.documentmngr.rest.api.AccountController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AccountControllerTest extends BaseTest {

    @DisplayName("Test List Accounts")
    @Test
    void testListAccounts() throws Exception {
        mockMvc.perform(get(AccountController.BASE_URL.concat("/v1/accounts"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(0)));
    }

    @DisplayName("Test Get Account By Id")
    @Test
    void testGetAccountById() throws Exception {
        mockMvc.perform(get(AccountController.BASE_URL.concat("/v1/accounts/{accountId}"), account.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(account.getId()));
    }
}
