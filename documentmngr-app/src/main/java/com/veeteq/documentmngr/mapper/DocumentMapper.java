package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {AccountMapper.class, DocumentItemMapper.class})
@DecoratedWith(DocumentMapperDecorator.class)
public interface DocumentMapper {

    @Mapping(target = "documentId",   source = "id")
    @Mapping(target = "documentDate", source = "documentDate")
    @Mapping(target = "documentType", source = "documentType")
    @Mapping(target = "documentName", source = "documentName")
    @Mapping(target = "documentComment", source = "documentDescription")
    @Mapping(target = "invoiceNumber", source = "invoiceNumber")
    @Mapping(target = "account",       source = "account")
    //@Mapping(target = "targetAccount", source = "account")
    //@Mapping(target = "counterpartyId", source = "counterpartyId")
    @Mapping(target = "paymentMethod", source = "paymentMethod")
    @Mapping(target = "currencyCode",  source = "currency")
    @Mapping(target = "exchangeRate",  source = "exchangeRate")
    @Mapping(target = "documentItemsCount", source = "documentItemsCount")
    @Mapping(target = "documentItems", source = "documentItems")
    DocumentResponseDto toDto(Document entity);

    @Mapping(target = "withDocumentDate",        source = "dto.date")
    @Mapping(target = "withDocumentType",        source = "dto.documentType")
    @Mapping(target = "withDocumentName",        source = "dto.documentName")
    @Mapping(target = "withDocumentDescription", source = "dto.documentDescription")
    @Mapping(target = "withAccount",             expression  = "java(accountRepository.findById(dto.getAccountId()).orElseThrow())")
    @Mapping(target = "withInvoiceNumber",       source = "dto.invoiceNumber")
    @Mapping(target = "withCounterpartyId",      source = "dto.counterpartyId")
    @Mapping(target = "withPaymentMethod",       source = "dto.paymentMethod")
    @Mapping(target = "withCurrencyCode",        source = "dto.currencyCode")
    @Mapping(target = "withExchangeRate",        source = "dto.exchangeRate")
    @Mapping(target = "withTransferAmount",      source = "dto.transferAmount")
    Document toEntity(DocumentRequestDto dto,  @Autowired AccountRepository accountRepository);

}
