package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.Item;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {AccountMapper.class, DocumentItemMapper.class})
@DecoratedWith(DocumentMapperDecorator.class)
public interface DocumentMapper {

    @Mapping(target = "documentId",         source = "id")
    @Mapping(target = "documentDate",       source = "documentDate")
    @Mapping(target = "documentType",       source = "documentType")
    @Mapping(target = "documentName",       source = "documentName")
    @Mapping(target = "documentComment",    source = "documentDescription")
    @Mapping(target = "invoiceNumber",      source = "invoiceNumber")
    @Mapping(target = "account",            source = "account")
    @Mapping(target = "targetAccount",      source = "targetAccount")
    @Mapping(target = "counterparty",       ignore = true)
    @Mapping(target = "paymentMethod",      source = "paymentMethod")
    @Mapping(target = "currencyCode",       source = "currency")
    @Mapping(target = "exchangeRate",       source = "exchangeRate")
    @Mapping(target = "documentItemsCount", source = "documentItemsCount")
    @Mapping(target = "documentItems",      source = "documentItems")
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
    @Mapping(target = "withPaymentMethod",       source = "dto.paymentMethod")
    @Mapping(target = "withCurrencyCode",        source = "dto.currencyCode")
    @Mapping(target = "withExchangeRate",        source = "dto.exchangeRate")
    @Mapping(target = "withTransferAmount",      source = "dto.transferAmount")
    @Mapping(target = "withTargetAccount",       ignore = true)
    @Mapping(target = "withTransferItem",        ignore = true)
    Document toEntity(DocumentRequestDto dto, Account account);

    @Mapping(target = "withDocumentDate",        source = "dto.documentDate")
    @Mapping(target = "withDocumentType",        source = "dto.documentType")
    @Mapping(target = "withAccount",             expression = "java(sourceAccount)")
    @Mapping(target = "withTargetAccount",       expression = "java(targetAccount)")
    @Mapping(target = "withPaymentMethod",       source = "dto.paymentMethod")
    @Mapping(target = "withCurrencyCode",        source = "dto.currencyCode")
    @Mapping(target = "withExchangeRate",        source = "dto.exchangeRate")
    @Mapping(target = "withTransferAmount",      source = "dto.transferAmount")
    @Mapping(target = "withTransferItem",        expression = "java(transferItem)")
    @Mapping(target = "withId",                  ignore = true)
    @Mapping(target = "withDocumentName",        ignore = true)
    @Mapping(target = "withDocumentDescription", ignore = true)
    @Mapping(target = "withInvoiceNumber",       ignore = true)
    @Mapping(target = "withCounterpartyId",      ignore = true)
    Document toEntity(DocumentRequestDto dto, Account sourceAccount, Account targetAccount, Item transferItem);

    Document updateWith(@MappingTarget Document document, DocumentRequestDto dto, Account account);

    Document updateWith(@MappingTarget Document document, DocumentRequestDto dto, Account sourceAccount, Account targetAccount, Item transferItem);

}