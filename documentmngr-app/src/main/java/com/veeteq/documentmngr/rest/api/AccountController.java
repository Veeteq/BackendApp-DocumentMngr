package com.veeteq.documentmngr.rest.api;

import com.veeteq.documentmngr.rest.dto.AccountDto;
import com.veeteq.documentmngr.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.veeteq.documentmngr.rest.api.AccountController.BASE_URL;

@RestController
@RequestMapping(path = BASE_URL)
@CrossOrigin(origins = {"http://localhost:4200", "*"})
public class AccountController implements AccountApi {
    public static final String BASE_URL = "/api";
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

        return accountService.getAccountById(id)
                .map(accountDto -> ResponseEntity.ok()
                        //.headers(headers)
                        .body(accountDto))
                .orElse(ResponseEntity.notFound()
                        //.headers(headers)
                        .build());
    }
}
