package com.veeteq.documentmngr.processor;

import com.veeteq.documentmngr.mapper.DocumentMapper;
import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.repository.ItemRepository;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentTypeDto;
import org.springframework.stereotype.Service;

@Service(value = DocumentProcessor.Type.TRANSFER)
public class TransferDocumentProcessor extends BaseDocumentProcessor {
    private final static long TRANSFER_ITEM_ID = 10;

    //private final AccountRepository accountRepository;
    //private final DocumentMapper documentMapper;
    private final ItemRepository itemRepository;

    public TransferDocumentProcessor(AccountRepository accountRepository, DocumentMapper documentMapper, ItemRepository itemRepository) {
        super(accountRepository, documentMapper);
        this.itemRepository = itemRepository;
    }

    @Override
    public Document process(DocumentRequestDto documentDto) {
        System.out.println("Processing using " + Type.TRANSFER);

        var sourceAccount = accountRepository.findById(documentDto.getAccountId()).orElseThrow();
        var targetAccount = accountRepository.findById(documentDto.getTargetAccountId()).orElseThrow();
        var transferItem = itemRepository.findById(TRANSFER_ITEM_ID).orElseThrow();
        var document = documentMapper.toEntity(documentDto, sourceAccount, targetAccount, transferItem);
        return document;
    }

    @Override
    public Document process(Document document, DocumentRequestDto documentDto) {
        validateRequest(document, documentDto);

        var sourceAccount = accountRepository.findById(documentDto.getAccountId()).orElseThrow();
        var targetAccount = accountRepository.findById(documentDto.getTargetAccountId()).orElseThrow();
        var transferItem = itemRepository.findById(TRANSFER_ITEM_ID).orElseThrow();
        var updated = documentMapper.updateWith(document, documentDto, sourceAccount, targetAccount, transferItem);
        return updated;
    }
}
