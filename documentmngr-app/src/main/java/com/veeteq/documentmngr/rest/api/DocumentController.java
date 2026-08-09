package com.veeteq.documentmngr.rest.api;

import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import com.veeteq.documentmngr.rest.dto.DocumentsResponseDto;
import com.veeteq.documentmngr.service.DocumentService;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

import static com.veeteq.documentmngr.rest.api.DocumentController.BASE_URL;

@RestController
@RequestMapping(path = BASE_URL)
@CrossOrigin(origins = {"http://localhost:4200", "*"})
public class DocumentController implements DocumentApi {
    public static final String BASE_URL = "/api";
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);
    private static final String TRANSACTION_ID = "Transaction-Id";

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    public ResponseEntity<Void> createDocument(UUID transactionId, DocumentRequestDto dto, String acceptLanguage) {
        LOGGER.info("Request received to create document: {}. TransactionID: {}", dto, acceptLanguage);

        var document = documentService.createDocument(dto);

        // Build the URI for the newly created account
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest() // Get the current request URI
                .path("/{id}") // Append the account ID to the URI path
                .buildAndExpand(document.getDocumentId())// Replace the path variable with the document ID
                .toUri();

        //Copy request headers to response
        var headers = new HttpHeaders();
        headers.set(TRANSACTION_ID, transactionId.toString());

        // Return the response with status 201 Created and set the Location header
        return ResponseEntity.created(location)
                .headers(headers)
                .build();
    }

    @Override
    @Transactional
    public ResponseEntity<DocumentResponseDto> getDocumentById(UUID transactionId, Long id, String acceptLanguage) {
        LOGGER.info("Request to get single document by its id: {}.", id);

        var headers = new HttpHeaders();
        headers.set(TRANSACTION_ID, transactionId.toString());
        var response = documentService.getDocumentById(id)
                .map(itemDto -> ResponseEntity.ok()
                        .headers(headers)
                        .body(itemDto))
                .orElse(ResponseEntity.notFound()
                        .headers(headers)
                        .build());
        return response;
    }

    @Override
    public ResponseEntity<DocumentsResponseDto> listDocuments(UUID transactionId, String acceptLanguage, Integer pageNumber, Integer pageSize, String orderBy, String orderDirection) {
        LOGGER.info("Request received to list all documents. TransactionID: {}", transactionId);

        var direction = Sort.Direction.fromString(orderDirection);
        var sort = Sort.by(direction, orderBy);
        var pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        var headers = new HttpHeaders();
        headers.set(TRANSACTION_ID, transactionId.toString());

        var result = documentService.listDocuments(pageRequest);
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(result);
    }

    @Override
    public ResponseEntity<DocumentResponseDto> updateDocument(UUID transactionId, Long id, DocumentRequestDto dto, String acceptLanguage) {
        LOGGER.info("Request received to update document with ID: {}. TransactionID: {}", id, transactionId);

        var headers = new HttpHeaders();
        headers.set(TRANSACTION_ID, transactionId.toString());

        var updated = documentService.updateDocument(id, dto)
                .map(documentDto -> ResponseEntity.ok()
                        .headers(headers)
                        .body(documentDto))
                .orElse(ResponseEntity.notFound()
                        .headers(headers)
                        .build());

        return updated;
    }

    @Override
    public ResponseEntity<Void> deleteDocument(Long id, UUID transactionId, String acceptLanguage) {
        return ResponseEntity.noContent().build();
    }
}
