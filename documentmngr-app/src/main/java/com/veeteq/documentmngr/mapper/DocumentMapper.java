package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.model.Item;
import com.veeteq.documentmngr.processor.TransferData;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import com.veeteq.documentmngr.rest.dto.DocumentTypeDto;

import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AccountMapper.class, DocumentItemMapper.class, TransferDetailsMapper.class})
@DecoratedWith(DocumentMapperDecorator.class)
public interface DocumentMapper {

    @Mapping(target = "documentId",         source = "id")
    @Mapping(target = "documentDate",       source = "documentDate")
    @Mapping(target = "documentType",       source = "documentType")
    @Mapping(target = "documentName",       source = "documentName")
    @Mapping(target = "documentComment",    source = "documentDescription")
    @Mapping(target = "invoiceNumber",      source = "invoiceNumber")
    @Mapping(target = "account",            source = "account")
    @Mapping(target = "transferDetails",    source = "entity",        qualifiedByName = "transferDetails")
    @Mapping(target = "counterparty",       ignore = true)
    @Mapping(target = "paymentMethod",      source = "paymentMethod")
    @Mapping(target = "currencyCode",       source = "currency")
    @Mapping(target = "exchangeRate",       source = "exchangeRate")
    @Mapping(target = "documentItemsCount", source = "documentItemsCount")
    @Mapping(target = "documentItems",      source = "documentItems", qualifiedByName = "sortedDocumentItems")
    @Mapping(target = "documentAmount",     source = "totalAmount")
    @Mapping(target = "version",            source = "version")
    DocumentResponseDto toDto(Document entity);

    @Mapping(target = "withId",                  ignore = true)
    @Mapping(target = "withDocumentDate",        source = "dto.documentDate")
    @Mapping(target = "withDocumentType",        source = "dto.documentType")
    @Mapping(target = "withDocumentName",        source = "dto.documentName")
    @Mapping(target = "withDocumentDescription", source = "dto.documentDescription")
    @Mapping(target = "withInvoiceNumber",       source = "dto.invoiceNumber")
    @Mapping(target = "withAccount",             expression = "java(account)")
    @Mapping(target = "withCounterpartyId",      source = "dto.counterpartyId")
    @Mapping(target = "withPaymentMethod",       source = "dto.payment.paymentMethod")
    @Mapping(target = "withCurrencyCode",        source = "dto.payment.currencyCode")
    @Mapping(target = "withExchangeRate",        source = "dto.payment.exchangeRate")
    @Mapping(target = "withTransferAmount",      ignore = true)
    @Mapping(target = "withTargetAccount",       ignore = true)
    @Mapping(target = "withTransferItem",        ignore = true)
    @Mapping(target = "withVersion",             ignore = true)
    Document toEntity(DocumentRequestDto dto, Account account);

    @Mapping(target = "withDocumentDate",        source = "dto.documentDate")
    @Mapping(target = "withDocumentType",        source = "dto.documentType")
    @Mapping(target = "withAccount",             expression = "java(transferData.getSourceAccount())")
    @Mapping(target = "withTargetAccount",       expression = "java(transferData.getTargetAccount())")
    @Mapping(target = "withPaymentMethod",       source = "dto.payment.paymentMethod")
    @Mapping(target = "withCurrencyCode",        source = "dto.payment.currencyCode")
    @Mapping(target = "withExchangeRate",        source = "dto.payment.exchangeRate")
    @Mapping(target = "withTransferAmount",      expression = "java(transferData.getTransferAmount())")
    @Mapping(target = "withTransferItem",        expression = "java(transferData.getTransferItem())")
    @Mapping(target = "withDocumentDescription", source = "dto.documentDescription")
    @Mapping(target = "withId",                  ignore = true)
    @Mapping(target = "withDocumentName",        ignore = true)
    @Mapping(target = "withInvoiceNumber",       ignore = true)
    @Mapping(target = "withCounterpartyId",      ignore = true)
    @Mapping(target = "withVersion",             ignore = true)
    Document toEntity(DocumentRequestDto dto, TransferData transferData);

    @Mapping(target = "documentItems", ignore = true)
    Document updateWith(@MappingTarget Document document, DocumentRequestDto dto, Account account);

    @Mapping(target = "documentItems", ignore = true)
    Document updateWith(@MappingTarget Document document, DocumentRequestDto dto, TransferData transferData);

}
