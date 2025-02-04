package com.veeteq.documentmngr.service;

import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;

import java.util.Optional;

public interface DocumentService {

    Document createDocument(DocumentRequestDto documentRequestDto);

    Optional<DocumentResponseDto> getDocumentById(Long id);
}
