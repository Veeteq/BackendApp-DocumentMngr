package com.veeteq.documentmngr.processor;

import com.veeteq.documentmngr.mapper.DocumentMapper;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.repository.ItemRepository;
import com.veeteq.documentmngr.repository.UtilityRepository;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import org.springframework.stereotype.Service;

@Service(value = DocumentProcessor.Type.BASE)
public class BaseDocumentProcessor implements DocumentProcessor {

    private final AccountRepository accountRepository;
    private final ItemRepository itemRepository;
    private final UtilityRepository utilityRepository;
    private final DocumentMapper documentMapper;

    public BaseDocumentProcessor(AccountRepository accountRepository, ItemRepository itemRepository, UtilityRepository utilityRepository, DocumentMapper documentMapper) {
        this.accountRepository = accountRepository;
        this.itemRepository = itemRepository;
        this.utilityRepository = utilityRepository;
        this.documentMapper = documentMapper;
    }

    @Override
    public Document process(DocumentRequestDto requestDto) {
        System.out.println("Processing using " + Type.BASE);

        var document = documentMapper.toEntity(requestDto, accountRepository);
        return document;
    }

}
