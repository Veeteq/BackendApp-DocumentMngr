package com.veeteq.documentmngr.rest.api;

import com.veeteq.documentmngr.rest.dto.AccountDto;
import com.veeteq.documentmngr.rest.dto.AccountsResponseDto;
import com.veeteq.documentmngr.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

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

    @Override
    public ResponseEntity<AccountsResponseDto> listAccounts(Integer pageNumber, Integer pageSize, String orderBy, String orderDirection) {
        LOGGER.info("Request received to list all accounts");

        var direction = Sort.Direction.fromString(orderDirection);
        var sort = Sort.by(direction, orderBy);
        var pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        var result = accountService.getAccounts(pageRequest);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Void> createAccount(AccountDto dto) {
        LOGGER.info("Request received to create new account");

        var savedAccount = accountService.saveAccount(dto);

        var uriComponents = UriComponentsBuilder.fromPath(BASE_URL.concat("/v1/accounts".concat("/{account_id}")))
                .buildAndExpand(savedAccount.getAccountId());
        URI uri = URI.create(uriComponents.getPath());

        return ResponseEntity.created(uri).build();
    }

    @Override
    public ResponseEntity<AccountDto> getAccountById(Long id) {
        LOGGER.info("Request received to search for account by Id: {}", id);

        var accountDto = accountService.getAccountById(id);
        return ResponseEntity.ok()
                //.headers(headers)
                .body(accountDto);
    }

    @Override
    public ResponseEntity<AccountDto> updateAccount(Long id, AccountDto dto) {
        LOGGER.info("Request received to update account: {}. Account Id: {}", dto, id);

        var updated = accountService.updateAccount(id, dto);
        return ResponseEntity.ok()
                //.headers(headers)
                .body(updated);
    }

    @Override
    public ResponseEntity<Void> deleteAccount(Long id) {
        LOGGER.info("Request received to delete account with Id: {}", id);

        accountService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
