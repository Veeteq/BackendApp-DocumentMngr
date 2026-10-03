package com.veeteq.documentmngr.service.jpa;

import com.veeteq.documentmngr.mapper.DocumentResponseFactory;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.processor.DocumentProcessor;
import com.veeteq.documentmngr.processor.DocumentProcessorFactory;
import com.veeteq.documentmngr.repository.DocumentItemRepository;
import com.veeteq.documentmngr.repository.DocumentRepository;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import com.veeteq.documentmngr.rest.dto.DocumentsResponseDto;
import com.veeteq.documentmngr.service.DocumentService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentProcessorFactory documentProcessorFactory;
    private final DocumentRepository documentRepository;
    private final DocumentItemRepository documentItemRepository;
    private final DocumentResponseFactory documentResponseFactory;

    public DocumentServiceImpl(DocumentProcessorFactory documentProcessorFactory, DocumentRepository documentRepository, DocumentItemRepository documentItemRepository, DocumentResponseFactory documentResponseFactory) {
        this.documentProcessorFactory = documentProcessorFactory;
        this.documentRepository = documentRepository;
        this.documentItemRepository = documentItemRepository;
        this.documentResponseFactory = documentResponseFactory;
    }

    @Override
    @Transactional
    public DocumentResponseDto createDocument(DocumentRequestDto documentDto) {
        var documentType = DocumentType.findByValue(documentDto.getDocumentType().getValue());
        DocumentProcessor processor = documentProcessorFactory.get(documentType.getProcessorType());
        var document = processor.process(documentDto);
        var savedDocument = documentRepository.save(document);

        return documentResponseFactory.create(savedDocument);
    }

    @Override
    public Optional<DocumentResponseDto> getDocumentById(Long id) {
        var result = documentRepository.findById(id);
        var response = result.map(documentResponseFactory::create);
        return response;
    }

    @Override
    @Transactional
    public DocumentsResponseDto listDocuments(Pageable pageable) {
        var page = documentRepository.findAll(pageable);
        var dtos = page.getContent().stream()
                .map(documentResponseFactory::create)
                .toList();
        var retVal = new DocumentsResponseDto()
                .data(dtos)
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages());
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

                    return documentResponseFactory.create(savedDocument);
                });
        return result;
    }

    @Override
    public Set<String> searchDocuments(String property, String pattern, Boolean distinct) {
        Map<String, Function<String, Set<String>>> map = Map.of(
                "documentItemComment", p -> documentRepository.findDistinctComments(p),
                "documentName",        p -> documentRepository.findDistinctNames(p)
        );
        var func = map.get(property);
        if (func == null) throw new IllegalArgumentException("Unsupported document search property: " + property);

        var result = func.apply(pattern);
        return result;
    }

}
