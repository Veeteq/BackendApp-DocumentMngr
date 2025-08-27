package com.veeteq.documentmngr.rest.api;

import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import com.veeteq.documentmngr.service.DocumentService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static com.veeteq.documentmngr.rest.api.DocumentController.BASE_URL;

@RestController
@RequestMapping(path = BASE_URL)
@CrossOrigin(origins = {"http://localhost:4200", "*"})
public class DocumentController implements DocumentApi {
    public final static String BASE_URL = "/api";

    private final static Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);
    private final static String TRANSACTION_ID = "Transaction-Id";

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    public ResponseEntity<Void> createDocument(UUID transactionId, DocumentRequestDto dto, String acceptLanguage) {
        LOGGER.info("Request received to create document: {}. TransactionID: {}", dto, transactionId);

        var document = documentService.createDocument(dto);

        // Build the URI for the newly created account
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest() // Get the current request URI
                .path("/{id}") // Append the account ID to the URI path
                .buildAndExpand(document.getDocumentId())// Replace the path variable with the document ID
                .toUri();

        var headers = new HttpHeaders();
        headers.set(TRANSACTION_ID, transactionId.toString());

        // Return the response with status 201 Created and set the Location header
        return ResponseEntity.created(location)
                .headers(headers)
                .build();
    }

    @Override
    @Transactional
    public ResponseEntity<DocumentResponseDto> getDocumentById(UUID transactionId, Long id) {
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
    public ResponseEntity<List<DocumentResponseDto>> listDocuments(UUID transactionId, String acceptLanguage) {
        LOGGER.info("Request received to list all documents. TransactionID: {}", transactionId);

        var headers = new HttpHeaders();
        headers.set(TRANSACTION_ID, transactionId.toString());

        var dtos = documentService.listDocuments();
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(dtos);
    }

    @Override
    public ResponseEntity<DocumentResponseDto> updateDocument(UUID transactionId, Long id, DocumentRequestDto dto) {
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
    public ResponseEntity<Void> deleteDocument(Long id, UUID transactionId) {
        return ResponseEntity.noContent().build();
    }

}
