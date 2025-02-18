package com.veeteq.documentmngr.service.jpa;

import com.veeteq.documentmngr.mapper.AccountMapper;
import com.veeteq.documentmngr.repository.UtilityRepository;
import com.veeteq.documentmngr.repository.EntityIdMapping;
import com.veeteq.documentmngr.rest.dto.AccountDto;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.service.AccountService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UtilityRepository utilityRepository;
    private final AccountMapper accountMapper;

    public AccountServiceImpl(AccountRepository accountRepository, UtilityRepository utilityRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.utilityRepository = utilityRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public List<AccountDto> listAccounts() {
        var retVal = accountRepository.findAll().stream()
                .map(accountMapper::toDto).toList();
        return retVal;
    }

    @Override
    public Optional<AccountDto> getAccountById(Long id) {
        var result = accountRepository.findById(id);
        return result.map(accountMapper::toDto);
    }

    @Override
    @Transactional
    public AccountDto saveAccount(AccountDto dto) {
        var account = accountMapper.toEntity(dto);
        account = accountRepository.save(account);
        return accountMapper.toDto(account);
    }

}
