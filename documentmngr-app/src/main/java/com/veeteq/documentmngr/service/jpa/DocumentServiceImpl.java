package com.veeteq.documentmngr.service.jpa;

import com.veeteq.documentmngr.mapper.DocumentMapper;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.processor.DocumentProcessor;
import com.veeteq.documentmngr.processor.DocumentProcessorFactory;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.repository.DocumentItemRepository;
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

    private final AccountRepository accountRepository;
    private final DocumentProcessorFactory documentProcessorFactory;
    private final DocumentRepository documentRepository;
    private final DocumentItemRepository documentItemRepository;
    private final DocumentMapper documentMapper;

    public DocumentServiceImpl(AccountRepository accountRepository, DocumentProcessorFactory documentProcessorFactory, DocumentRepository documentRepository, DocumentItemRepository documentItemRepository, DocumentMapper documentMapper) {
        this.accountRepository = accountRepository;
        this.documentProcessorFactory = documentProcessorFactory;
        this.documentRepository = documentRepository;
        this.documentItemRepository = documentItemRepository;
        this.documentMapper = documentMapper;
    }

    @Override
    @Transactional
    public DocumentResponseDto createDocument(DocumentRequestDto documentDto) {
        var documentType = DocumentType.findByValue(documentDto.getDocumentType().getValue());
        DocumentProcessor processor = documentProcessorFactory.get(documentType.getProcessorType());
        var document = processor.process(documentDto);
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

    @Override
    @Transactional
    public Optional<DocumentResponseDto> updateDocument(Long id, DocumentRequestDto dto) {

        var result = documentRepository.findById(id)
                .map(document -> {
                    document.clearDocumentItems();
                    document.getDocumentItems().forEach(documentItemRepository::delete);

                    var documentType = DocumentType.findByValue(dto.getDocumentType().getValue());
                    DocumentProcessor processor = documentProcessorFactory.get(documentType.getProcessorType());

                    var updated = processor.process(document, dto);
                    var savedDocument = documentRepository.save(updated);

                    return documentMapper.toDto(savedDocument);
                });
        return result;
    }

/*
    public DocDto update(Long id, DocDto dto) {
        var updated = docRepository.findByIdWithLock(id)
                .map(entity -> {
                    entity.getDocItems().forEach(docItem -> docItemRepository.delete(docItem));
                    return docMapper.updateWith(entity, dto);
                })
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        var saved = docRepository.save(updated);
        return docMapper.toDto(saved);
    }
 */
}
