package com.veeteq.documentmngr.service;

import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;

import java.util.List;
import java.util.Optional;

public interface DocumentService {

    DocumentResponseDto createDocument(DocumentRequestDto documentDto);

    Optional<DocumentResponseDto> getDocumentById(Long id);

    List<DocumentResponseDto> listDocuments();

    Optional<DocumentResponseDto> updateDocument(Long id, DocumentRequestDto dto);
}
