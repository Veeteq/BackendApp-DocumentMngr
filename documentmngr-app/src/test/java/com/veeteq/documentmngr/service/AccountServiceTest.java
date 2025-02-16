package com.veeteq.documentmngr.service;

import com.veeteq.documentmngr.DocumentMngrApp;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.rest.dto.AccountDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = DocumentMngrApp.class)
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @DisplayName("Test List Accounts")
    @Test
    void listAccounts() {
        List<AccountDto> dtoList = accountService.listAccounts();
        assertNotNull(dtoList);
        assertThat(dtoList, is(not(empty())));
    }

    @DisplayName("Test Account by Id")
    @Test
    void getAccountById() {
        var savedAccountDto = accountService.getAccountById(5L).orElseThrow();

        assertNotNull(savedAccountDto);
        assertEquals(5L, savedAccountDto.getAccountId());
        assertEquals("EFG", savedAccountDto.getAccountName());
        assertEquals("EFG Description", savedAccountDto.getAccountDescription());
        assertEquals("GBP", savedAccountDto.getAccountCurrency());
        assertEquals("https://image.com/logo.png", savedAccountDto.getAccountImageUrl());
    }

    @DisplayName("Test Save Account - With Id")
    @Test
    void saveAccount_WithId() {
        var dto = createAccountDto_WithId();
        var savedAccountDto = accountService.saveAccount(dto);

        var entity = accountRepository.findById(savedAccountDto.getAccountId()).orElseThrow();

        assertNotNull(entity);
        assertEquals(dto.getAccountId(), entity.getId());
        assertEquals(dto.getAccountName(), entity.getName());
        assertEquals(dto.getAccountDescription(), entity.getDescription());
        assertEquals(dto.getAccountCurrency(), entity.getCurrency().getCurrencyCode());
        assertEquals(dto.getAccountImageUrl(), entity.getImageUrl());
    }

    @DisplayName("Test Save Account - No Id")
    @Test
    void saveAccount_WithoutId() {
        var dto = createAccountDto_WithoutId();
        var savedAccountDto = accountService.saveAccount(dto);

        var entity = accountRepository.findById(savedAccountDto.getAccountId()).orElseThrow();

        assertNotNull(entity);
        assertNotNull(dto.getAccountId());
        assertEquals(dto.getAccountName(), entity.getName());
        assertEquals(dto.getAccountDescription(), entity.getDescription());
        assertEquals(dto.getAccountCurrency(), entity.getCurrency().getCurrencyCode());
        assertEquals(dto.getAccountImageUrl(), entity.getImageUrl());
    }

    private AccountDto createAccountDto_WithId() {
        var dto = new AccountDto()
                .accountId(100L)
                .accountName("The Zorg Corporation")
                .accountDescription("The Zorg Corporation Billing Account")
                .accountCurrency("USD")
                .accountImageUrl("https://zorgcorportation.org/logo");
        return dto;
    }

    private AccountDto createAccountDto_WithoutId() {
        var dto = new AccountDto()
                .accountName("The Tatooine Industry")
                .accountDescription("The Tatooine Industry Billing Account")
                .accountCurrency("EUR")
                .accountImageUrl("https://tatooineindustry.org/logo");
        return dto;
    }
}
