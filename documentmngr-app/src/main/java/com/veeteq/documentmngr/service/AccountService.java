package com.veeteq.documentmngr.service;

import com.veeteq.documentmngr.rest.dto.AccountDto;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    List<AccountDto> listAccounts();

    Optional<AccountDto> getAccountById(Long id);

    AccountDto save(AccountDto dto);
}
