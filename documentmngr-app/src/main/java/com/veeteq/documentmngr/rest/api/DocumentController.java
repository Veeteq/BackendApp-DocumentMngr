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

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = {"http://localhost:4200", "*"})
public class DocumentController implements DocumentApi {
    private final static Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    public ResponseEntity<Void> createDocument(Long cookieParam, DocumentRequestDto documentRequestDto, String acceptLanguage) {
        LOGGER.info("Request to create document: {}. TransactionID: {}", documentRequestDto, acceptLanguage);
        var document = documentService.createDocument(documentRequestDto);

        // Build the URI for the newly created account
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest() // Get the current request URI
                .path("/{id}") // Append the account ID to the URI path
                .buildAndExpand(document.getId())// Replace the path variable with the document ID
                .toUri();

        var headers = new HttpHeaders();
        //headers.set(TRANSACTION_ID, transactionId.toString());

        // Return the response with status 201 Created and set the Location header
        return ResponseEntity.created(location).headers(headers).build();
    }

    @Override
    @Transactional
    public ResponseEntity<DocumentResponseDto> getDocumentById(Long id) {
        LOGGER.info("Request to get single document by its id: {}.", id);

        var headers = new HttpHeaders();
        var response = documentService.getDocumentById(id)
                .map(itemDto -> ResponseEntity.ok()
                        //.headers(headers)
                        .body(itemDto))
                .orElse(ResponseEntity.notFound()
                        //.headers(headers)
                        .build());
        return response;
    }

    @Override
    public ResponseEntity<List<DocumentResponseDto>> listDocuments(Long cookieParam, String acceptLanguage) {
        return null;
    }
}
