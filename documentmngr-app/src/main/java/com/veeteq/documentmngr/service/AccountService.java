package com.veeteq.documentmngr.service;

import com.veeteq.documentmngr.rest.dto.AccountDto;
import com.veeteq.documentmngr.rest.dto.AccountsResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {

    List<AccountDto> listAccounts();

    AccountsResponseDto getAccounts(Pageable pageable);

    AccountDto getAccountById(Long id);

    AccountDto saveAccount(AccountDto dto);

    AccountDto updateAccount(Long id, AccountDto dto);

    void deleteById(Long id);
}
