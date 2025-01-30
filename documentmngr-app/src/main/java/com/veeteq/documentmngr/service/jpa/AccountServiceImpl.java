package com.veeteq.documentmngr.service.jpa;

import com.veeteq.documentmngr.rest.dto.AccountDto;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<AccountDto> listAccounts() {
        var retVal = accountRepository.findAll().stream()
                .map(entity -> {
                    var dto = new AccountDto()
                            .accountId(entity.getId())
                            .accountName(entity.getName());
                    return dto;
                }).toList();
        return retVal;
    }

    @Override
    public Optional<AccountDto> getAccountById(Long id) {
        var result = accountRepository.findById(id);
        return result.map(entity -> {
            var dto = new AccountDto()
                    .accountId(entity.getId())
                    .accountName(entity.getName());
            return dto;
        });
    }
}
