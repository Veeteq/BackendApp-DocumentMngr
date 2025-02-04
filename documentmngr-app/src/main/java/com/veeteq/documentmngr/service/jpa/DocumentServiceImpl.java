package com.veeteq.documentmngr.service.jpa;

import com.veeteq.documentmngr.mapper.DocumentMapper;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.processor.DocumentProcessor;
import com.veeteq.documentmngr.processor.DocumentProcessorFactory;
import com.veeteq.documentmngr.repository.DocumentRepository;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import com.veeteq.documentmngr.service.DocumentService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentProcessorFactory documentProcessorFactory;
    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    public DocumentServiceImpl(DocumentProcessorFactory documentProcessorFactory, DocumentRepository documentRepository, DocumentMapper documentMapper) {
        this.documentProcessorFactory = documentProcessorFactory;
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
    }

    @Override
    public Document createDocument(DocumentRequestDto documentRequestDto) {
        var documentType = DocumentType.findByValue(documentRequestDto.getDocumentType().getValue());
        DocumentProcessor processor = documentProcessorFactory.get(documentType.getProcessorType());
        var document = processor.process(documentRequestDto);
        var savedDocument = documentRepository.save(document);
        return savedDocument;
    }

    @Override
    public Optional<DocumentResponseDto> getDocumentById(Long id) {
        var result = documentRepository.findById(id);
        var response = result.map(documentMapper::toDto);
        return response;
    }
}
