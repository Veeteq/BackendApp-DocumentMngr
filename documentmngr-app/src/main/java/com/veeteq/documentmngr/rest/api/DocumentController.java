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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static com.veeteq.documentmngr.rest.api.DocumentController.BASE_URL;

@RestController
@RequestMapping(path = BASE_URL)
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
        var result = documentService.getDocumentById(id);
        var response = ResponseEntity.ok()
                .headers(headers)
                .body(result);
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
    public ResponseEntity<DocumentsResponseDto> searchDocuments(UUID transactionId, LocalDate documentDate, Integer pageNumber, Integer pageSize, String orderBy, String orderDirection, String acceptLanguage) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ResponseEntity<Set<String>> getMetadata(String property, String pattern, UUID transactionId, Boolean distinct, String acceptLanguage) {
        LOGGER.info("Request received to list {} document with pattern: {}. TransactionID: {}", property, pattern, transactionId);

        var result = documentService.getMetadata(property, pattern, distinct);

        return ResponseEntity
                .ok()
                .headers(h -> h.set(TRANSACTION_ID, transactionId.toString()))
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
    public ResponseEntity<Void> deleteDocument(Long id, Integer version, UUID transactionId, String acceptLanguage) {
        LOGGER.info("Request received to delete document with ID: {}. TransactionID: {}", id, transactionId);

        var headers = new HttpHeaders();
        headers.set(TRANSACTION_ID, transactionId.toString());

        documentService.deleteById(id);

        return ResponseEntity.noContent()
                .headers(headers)
                .build();
    }

}
