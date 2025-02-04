package com.veeteq.documentmngr.processor;

import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.DocumentItem;
import com.veeteq.documentmngr.model.DocumentItemType;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.repository.ItemRepository;
import com.veeteq.documentmngr.repository.UtilityRepository;
import com.veeteq.documentmngr.repository.UtilityRepository.EntityIdMapping;
import com.veeteq.documentmngr.rest.dto.DocumentItemRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service(value = DocumentProcessor.Type.BASE)
public class BaseDocumentProcessor implements DocumentProcessor {

    private final AccountRepository accountRepository;
    private final ItemRepository itemRepository;
    private final UtilityRepository utilityRepository;

    public BaseDocumentProcessor(AccountRepository accountRepository, ItemRepository itemRepository, UtilityRepository utilityRepository) {
        this.accountRepository = accountRepository;
        this.itemRepository = itemRepository;
        this.utilityRepository = utilityRepository;
    }

    @Override
    public Document process(DocumentRequestDto requestDto) {
        System.out.println("Processing using " + Type.BASE);

        var document = toEntity(requestDto);
        return document;
    }

    private Document toEntity(DocumentRequestDto dto) {
        var account = accountRepository.getReferenceById(dto.getAccountId());
        var documentDate = dto.getDate();
        var entity = Document.builder()
                .withRepository(utilityRepository)
                .withAccount(account)
                .withCounterpartyId(dto.getCounterpartyId())
                .withCurrencyCode(dto.getCurrencyCode())
                .withDocumentType(DocumentType.findByValue(dto.getDocumentType().getValue()))
                .withDocumentName(dto.getDocumentName())
                .withDocumentDescription(dto.getDocumentDescription())
                .withExchangeRate(dto.getExchangeRate())
                .withInvoiceNumber(dto.getInvoiceNumber())
                .withOperationDate(documentDate)
                .withPaymentMethod(dto.getPaymentMethod())
                .build();

        dto.getDocumentItems().stream()
                .map(itemDto -> toEntity(itemDto, entity))
                .forEach(entity::addToDocumentItems);
        return entity;
    }

    private DocumentItem toEntity(DocumentItemRequestDto dto, Document document) {
        var sequenceGenerator = getSequenceGenerator(EntityIdMapping.valueOf(dto.getItemType().toUpperCase()));

        return DocumentItem.builder()
                .withDocument(document)
                .withDocumentItemType(DocumentItemType.findByValue(dto.getItemType()))
                .withItemId(sequenceGenerator.get())
                .withItem(itemRepository.getReferenceById(dto.getItemId()))
                .withItemQuantity(dto.getItemQuantity())
                .withItemPrice(dto.getItemPrice())
                .withItemComment(dto.getItemDescription())
                .build();
    }

    private Supplier<Long> getSequenceGenerator(EntityIdMapping type) {
        return switch (type) {
            case EntityIdMapping.INCOME -> () -> utilityRepository.getNextId(EntityIdMapping.INCOME);
            case EntityIdMapping.EXPENSE -> () -> utilityRepository.getNextId(EntityIdMapping.EXPENSE);
            case EntityIdMapping.DOCUMENT -> () -> utilityRepository.getNextId(EntityIdMapping.DOCUMENT);
            default -> throw new IllegalArgumentException();
        };
    }

}
