package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.rest.dto.AccountDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountMapperTest {

    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @DisplayName("Test mapping from Account entity to AccountDto")
    @Test
    public void test_AccountEntity_To_AccountDto() {
        // Arrange
        Account account = Account.builder()
                .withId(1L)
                .withName("Test Account")
                .withDescription("Test Account Description")
                .withCurrency(Currency.getInstance("PLN"))
                .withImageUrl("https://image.com/logo.png")
                .build();

        // Act
        AccountDto accountDto = accountMapper.toDto(account);

        // Assert
        assertEquals(account.getId(),          accountDto.getAccountId());
        assertEquals(account.getName(),        accountDto.getAccountName());
        assertEquals(account.getDescription(), accountDto.getAccountDescription());
        assertEquals(account.getCurrency().getCurrencyCode(), accountDto.getAccountCurrency());
        assertEquals(account.getImageUrl(),    accountDto.getAccountImageUrl());

        assertEquals(5, accountDto.getClass().getDeclaredFields().length);
    }

    @DisplayName("Test mapping from AccountDto to Account entity")
    @Test
    public void test_AccountDto_To_AccountEntity() {
        // Given
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountId(1L);
        accountDto.setAccountName("New account request");
        accountDto.setAccountDescription("New account description request");
        accountDto.setAccountCurrency("CZK");
        accountDto.setAccountImageUrl("https://image.com/logo.png");

        // When
        Account account = accountMapper.toEntity(accountDto);

        // Then
        assertEquals(accountDto.getAccountId(),          account.getId());
        assertEquals(accountDto.getAccountName(),        account.getName());
        assertEquals(accountDto.getAccountDescription(), account.getDescription());
        assertEquals(accountDto.getAccountCurrency(),    account.getCurrency().getCurrencyCode());
        assertEquals(accountDto.getAccountImageUrl(),    account.getImageUrl());

        assertEquals(5, account.getClass().getDeclaredFields().length);
    }

}