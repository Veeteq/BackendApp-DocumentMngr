package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.model.Item;
import com.veeteq.documentmngr.repository.ItemRepository;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class DocumentMapperDecorator implements DocumentMapper {

    @Autowired
    @Qualifier("delegate")
    private DocumentMapper documentMapper;

    @Autowired
    private DocumentItemMapper documentItemMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public DocumentResponseDto toDto(Document entity) {
        return documentMapper.toDto(entity);
    }

    @Override
    public Document toEntity(DocumentRequestDto dto, Account account) {
        var entity = documentMapper.toEntity(dto, account);
        after(dto, entity);
        return entity;
    }

    @AfterMapping
    public void after(DocumentRequestDto dto, @MappingTarget Document entity) {
        dto.getDocumentItems().stream()
                .map(itemDto -> documentItemMapper.toEntity(itemDto, entity, itemRepository))
                .forEach(entity::addToDocumentItems);
    }

    @Override
    public Document updateWith(Document entity, DocumentRequestDto dto, Account account) {
        var document = Document.builder(entity)
                .withDocumentDate(dto.getDocumentDate())
                .withDocumentType(DocumentType.valueOf(dto.getDocumentType().name()))
                .withDocumentName(dto.getDocumentName())
                .withDocumentDescription(dto.getDocumentDescription())
                .withInvoiceNumber(dto.getInvoiceNumber())
                .withAccount(account)
                .withCounterpartyId(dto.getCounterpartyId())
                .withPaymentMethod(dto.getPaymentMethod())
                .withCurrencyCode(dto.getCurrencyCode())
                .withExchangeRate(dto.getExchangeRate())
                .withVersion(dto.getVersion())
                .build();
        after(dto, document);
        return document;
    }

    @Override
    public Document updateWith(Document entity, DocumentRequestDto dto, Account sourceAccount, Account targetAccount, Item transferItem) {
        var document = Document.builder(entity)
                .withDocumentDate(dto.getDocumentDate())
                .withDocumentType(DocumentType.valueOf(dto.getDocumentType().name()))
                .withAccount(sourceAccount)
                .withTargetAccount(targetAccount)
                .withTransferAmount(dto.getTransferAmount())
                .withTransferItem(transferItem)
                .withPaymentMethod(dto.getPaymentMethod())
                .withCurrencyCode(dto.getCurrencyCode())
                .withExchangeRate(dto.getExchangeRate())
                .withVersion(dto.getVersion())
                .build();
        after(dto, document);
        return document;
    }
/*
    @AfterMapping
    public void after(DocumentRequestDto dto, @MappingTarget Document entity) {
        dto.getDocumentItems().stream()
                .map(itemDto -> documentItemMapper.toEntity(itemDto, entity, itemRepository))
                .forEach(entity::addToDocumentItems);
    }
*/
}
