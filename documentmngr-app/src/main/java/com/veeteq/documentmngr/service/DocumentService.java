package com.veeteq.documentmngr.service;

import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import com.veeteq.documentmngr.rest.dto.DocumentsResponseDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface DocumentService {

    DocumentResponseDto createDocument(DocumentRequestDto documentDto);

    DocumentResponseDto getDocumentById(Long id);

    DocumentsResponseDto listDocuments(Pageable pageable);

    Optional<DocumentResponseDto> updateDocument(Long id, DocumentRequestDto dto);

    Set<String> getMetadata(String property, String pattern, Boolean distinct);

    DocumentsResponseDto searchDocuments(LocalDate documentDate, Pageable pageable);

    void deleteById(Long id, int version);
}
