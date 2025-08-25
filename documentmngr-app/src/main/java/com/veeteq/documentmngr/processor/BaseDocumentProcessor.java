package com.veeteq.documentmngr.processor;

import com.veeteq.documentmngr.mapper.DocumentMapper;
import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.repository.ItemRepository;
import com.veeteq.documentmngr.repository.UtilityRepository;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentTypeDto;
import org.springframework.stereotype.Service;

@Service(value = DocumentProcessor.Type.BASE)
public class BaseDocumentProcessor implements DocumentProcessor {

    protected final AccountRepository accountRepository;
    protected final DocumentMapper documentMapper;

    public BaseDocumentProcessor(AccountRepository accountRepository, DocumentMapper documentMapper) {
        this.accountRepository = accountRepository;
        this.documentMapper = documentMapper;
    }

    @Override
    public Document process(DocumentRequestDto documentDto) {
        System.out.println("Processing using " + Type.BASE);
        var account = accountRepository.findById(documentDto.getAccountId()).orElseThrow();
        var document = documentMapper.toEntity(documentDto, account);
        return document;
    }

    @Override
    public Document process(Document document, DocumentRequestDto documentDto) {
        validateRequest(document, documentDto);

        var account = accountRepository.findById(documentDto.getAccountId()).orElseThrow();
        var updated = documentMapper.updateWith(document, documentDto, account);
        return updated;
    }

    protected void validateRequest(Document document, DocumentRequestDto documentDto) {
        var baseDocumentType = document.getDocumentType();
        var targetDocumentType = documentDto.getDocumentType();

        if (baseDocumentType.equals(DocumentType.TRANSFER) && !targetDocumentType.equals(DocumentTypeDto.TRANSFER)) {
            throw new IllegalArgumentException("Cannot update TRANSFER Document to other document type");
        }
    }
}
