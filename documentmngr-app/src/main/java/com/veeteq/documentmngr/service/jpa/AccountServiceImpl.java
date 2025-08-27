package com.veeteq.documentmngr.service.jpa;

import com.veeteq.documentmngr.exception.ConflictException;
import com.veeteq.documentmngr.exception.NotFoundException;
import com.veeteq.documentmngr.mapper.AccountMapper;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.repository.DocumentRepository;
import com.veeteq.documentmngr.repository.UtilityRepository;
import com.veeteq.documentmngr.rest.dto.AccountDto;
import com.veeteq.documentmngr.rest.dto.AccountsResponseDto;
import com.veeteq.documentmngr.service.AccountService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UtilityRepository utilityRepository;
    private final DocumentRepository documentRepository;
    private final AccountMapper accountMapper;

    public AccountServiceImpl(AccountRepository accountRepository, UtilityRepository utilityRepository, DocumentRepository documentRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.utilityRepository = utilityRepository;
        this.documentRepository = documentRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public List<AccountDto> listAccounts() {
        var retVal = accountRepository.findAll().stream()
                .map(accountMapper::toDto).toList();
        return retVal;
    }

    @Override
    public AccountsResponseDto getAccounts(Pageable pageable) {
        var result = accountRepository.findAll(pageable);
        return accountMapper.toDto(result);
    }

    @Override
    public AccountDto getAccountById(Long id) {
        var result = accountRepository.findById(id).map(accountMapper::toDto).orElseThrow(NotFoundException::new);
        return result;
    }

    @Override
    @Transactional
    public AccountDto saveAccount(AccountDto dto) {
        var account = accountMapper.toEntity(dto);
        var savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Transactional
    @Override
    public AccountDto updateAccount(Long id, AccountDto dto) {
        return accountRepository.findById(id)
                .map(account -> {
                    var updated = accountMapper.updateWith(dto, account);
                    var savedAccount = accountRepository.save(updated);
                    return accountMapper.toDto(savedAccount);
                }).orElseThrow(NotFoundException::new);
    }

    @Override
    public void deleteById(Long id) {
        accountRepository.findById(id).ifPresentOrElse(account -> {
            if (documentRepository.countByAccount(account) > 0) {
                throw new ConflictException("Account is referenced by Documents");
            }
            accountRepository.delete(account);
        }, () -> {
            throw new NotFoundException("Account not found");
        });
    }

}
