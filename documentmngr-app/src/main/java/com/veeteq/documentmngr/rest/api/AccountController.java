package com.veeteq.documentmngr.rest.api;

import com.veeteq.documentmngr.rest.dto.AccountDto;
import com.veeteq.documentmngr.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountController implements AccountApi {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    public ResponseEntity<List<AccountDto>> listAccounts() {
        var dtos = accountService.listAccounts();
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<AccountDto> getAccountById(Long id) {
        var dto = accountService.getAccountById(id);

        return accountService.getAccountById(id)
                .map(accountDto -> ResponseEntity.ok()
                        //.headers(headers)
                        .body(accountDto))
                .orElse(ResponseEntity.notFound()
                        //.headers(headers)
                        .build());
    }
}
