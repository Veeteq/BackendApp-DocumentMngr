package com.veeteq.documentmngr.service.jpa;

import com.veeteq.documentmngr.mapper.DocumentMapper;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.processor.DocumentProcessor;
import com.veeteq.documentmngr.processor.DocumentProcessorFactory;
import com.veeteq.documentmngr.repository.DocumentRepository;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import com.veeteq.documentmngr.service.DocumentService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
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
    @Transactional
    public DocumentResponseDto createDocument(DocumentRequestDto documentRequestDto) {
        var documentType = DocumentType.findByValue(documentRequestDto.getDocumentType().getValue());
        DocumentProcessor processor = documentProcessorFactory.get(documentType.getProcessorType());
        var document = processor.process(documentRequestDto);
        var savedDocument = documentRepository.save(document);
        return documentMapper.toDto(savedDocument);
    }

    @Override
    public Optional<DocumentResponseDto> getDocumentById(Long id) {
        var result = documentRepository.findById(id);
        var response = result.map(documentMapper::toDto);
        return response;
    }

    @Override
    @Transactional
    public List<DocumentResponseDto> listDocuments() {
        var retVal = documentRepository.findAll().stream()
                .map(documentMapper::toDto).toList();
        return retVal;
    }
}
