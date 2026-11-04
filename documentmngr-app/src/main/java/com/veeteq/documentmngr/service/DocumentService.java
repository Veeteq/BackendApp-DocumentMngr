package com.veeteq.documentmngr.service;

import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import com.veeteq.documentmngr.rest.dto.DocumentsResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

public interface DocumentService {

    DocumentResponseDto createDocument(DocumentRequestDto documentDto);

    Optional<DocumentResponseDto> getDocumentById(Long id);

    DocumentsResponseDto listDocuments(Pageable pageable);

    Optional<DocumentResponseDto> updateDocument(Long id, DocumentRequestDto dto);

    Set<String> searchDocuments(String property, String pattern, Boolean distinct);
}
