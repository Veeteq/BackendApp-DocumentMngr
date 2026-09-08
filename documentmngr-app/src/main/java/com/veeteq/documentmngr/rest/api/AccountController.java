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
import java.util.List;
import java.util.UUID;

import static com.veeteq.documentmngr.rest.api.AccountController.BASE_URL;

@RestController
@RequestMapping(path = BASE_URL)
@CrossOrigin(origins = {"http://localhost:4200", "*"})
public class AccountController implements AccountApi {
    public static final String BASE_URL = "/api";
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class.getSimpleName());
    private static final String TRANSACTION_ID = "Transaction-Id";

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<Void> createAccount(UUID transactionId, String acceptLanguage, AccountDto dto) {
        LOGGER.info("Request received to create new account");

        var savedAccount = accountService.saveAccount(dto);
        var uriComponents = UriComponentsBuilder.fromPath(BASE_URL.concat("/v1/accounts".concat("/{account_id}")))
                .buildAndExpand(savedAccount.getAccountId());
        URI uri = URI.create(uriComponents.getPath());

        return ResponseEntity.created(uri)
                .header(TRANSACTION_ID, transactionId.toString())
                .build();
    }

    @Override
    public ResponseEntity<AccountDto> getAccountById(Long id, UUID transactionId, String acceptLanguage) {
        LOGGER.info("Request received to search for account by Id: {}", id);

        var accountDto = accountService.getAccountById(id);
        return ResponseEntity.ok()
                .header(TRANSACTION_ID, transactionId.toString())
                .body(accountDto);
    }

	@Override
	public ResponseEntity<AccountsResponseDto> listAccounts(UUID transactionId, String acceptLanguage, Integer pageNumber, Integer pageSize, String orderBy, String orderDirection) {
        LOGGER.info("Request received to list all accounts");

        var direction = Sort.Direction.fromString(orderDirection);
        var sort = Sort.by(direction, orderBy);
        var pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        var result = accountService.getAccounts(pageRequest);
        return ResponseEntity.ok()
                .header(TRANSACTION_ID, transactionId.toString())
                .body(result);
	}

    @Override
    public ResponseEntity<List<AccountDto>> searchAccounts(String name, UUID transactionId, String acceptLanguage) {
        LOGGER.info("Request received to search for accounts");

        var result = accountService.searchAccounts(name);
        return ResponseEntity.ok()
                .header(TRANSACTION_ID, transactionId.toString())
                .body(result);
    }

    @Override
	public ResponseEntity<AccountDto> updateAccount(Long id, UUID transactionId, AccountDto dto, String acceptLanguage) {
        LOGGER.info("Request received to update account: {}. Account Id: {}", dto, id);

        var updated = accountService.updateAccount(id, dto);
        return ResponseEntity.ok()
                .header(TRANSACTION_ID, transactionId.toString())
                .body(updated);
    }

    @Override
    public ResponseEntity<Void> deleteAccount(Long id, UUID transactionId, String acceptLanguage) {
        LOGGER.info("Request received to delete account with Id: {}", id);

        accountService.deleteById(id);
        return ResponseEntity.noContent()
                .header(TRANSACTION_ID, transactionId.toString())
                .build();
    }

}
