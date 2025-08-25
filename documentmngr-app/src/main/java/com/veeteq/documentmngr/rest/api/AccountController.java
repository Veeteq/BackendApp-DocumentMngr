package com.veeteq.documentmngr.rest.api;

import com.veeteq.documentmngr.rest.dto.AccountDto;
import com.veeteq.documentmngr.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.veeteq.documentmngr.rest.api.AccountController.BASE_URL;

@RestController
@RequestMapping(path = BASE_URL)
@CrossOrigin(origins = {"http://localhost:4200", "*"})
public class AccountController implements AccountApi {
    public static final String BASE_URL = "/api";

    private final AccountService accountService;
    private final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    public ResponseEntity<List<AccountDto>> listAccounts() {
        var dtos = accountService.listAccounts();
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<Void> createAccount(AccountDto dto) {
        var savedAccount = accountService.saveAccount(dto);

        var uriComponents = UriComponentsBuilder.fromPath(BASE_URL.concat("/v1/accounts".concat("/{account_id}")))
                .buildAndExpand(savedAccount.getAccountId());
        URI uri = URI.create(uriComponents.getPath());

        return ResponseEntity.created(uri).build();
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

    @Override
    public ResponseEntity<AccountDto> updateAccount(Long id, AccountDto dto) {
        LOGGER.info("Request received to update account: {}. Account Id: {}", dto, id);

        var updated = accountService.updateAccount(id, dto)
                .map(accountDto -> ResponseEntity.ok()
                        //.headers(headers)
                        .body(accountDto))
                .orElse(ResponseEntity.notFound()
                        //.headers(headers)
                        .build());
        return updated;
    }
}
