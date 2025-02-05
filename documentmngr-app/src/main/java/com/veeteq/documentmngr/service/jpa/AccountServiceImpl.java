package com.veeteq.documentmngr.service.jpa;

import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.repository.UtilityRepository;
import com.veeteq.documentmngr.repository.UtilityRepository.EntityIdMapping;
import com.veeteq.documentmngr.rest.dto.AccountDto;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UtilityRepository utilityRepository;

    public AccountServiceImpl(AccountRepository accountRepository, UtilityRepository utilityRepository) {
        this.accountRepository = accountRepository;
        this.utilityRepository = utilityRepository;
    }

    @Override
    public List<AccountDto> listAccounts() {
        var retVal = accountRepository.findAll().stream()
                .map(this::toDto).toList();
        return retVal;
    }

    @Override
    public Optional<AccountDto> getAccountById(Long id) {
        var result = accountRepository.findById(id);
        return result.map(this::toDto);
    }

    @Override
    public AccountDto save(AccountDto dto) {
        var account = toEntity(dto);
        account = accountRepository.save(account);
        return toDto(account);
    }

    private AccountDto toDto(Account entity) {
        var dto = new AccountDto()
                .accountId(entity.getId())
                .accountName(entity.getName());
        return dto;
    }

    private Account toEntity(AccountDto dto) {
        var entity = Account.builder()
                .withId(utilityRepository.getNextId(EntityIdMapping.ACCOUNT))
                .withName(dto.getAccountName())
                .withDescription(dto.getAccountDescription())
                .withCurrency(Currency.getInstance(dto.getAccountCurrency()))
                .withImageUrl(dto.getAccountImageUrl())
                .build();
        return entity;
    }
}
