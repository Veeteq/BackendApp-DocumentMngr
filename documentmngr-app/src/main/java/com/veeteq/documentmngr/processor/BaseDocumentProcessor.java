package com.veeteq.documentmngr.processor;

import com.veeteq.documentmngr.mapper.DocumentMapper;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentTypeDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

@Service(value = DocumentProcessor.Type.BASE)
public class BaseDocumentProcessor implements DocumentProcessor {
    private final static Set<DocumentTypeDto> COUNTERPARTY_REQUIRED_TYPES = EnumSet.of(DocumentTypeDto.BILL, DocumentTypeDto.INVOICE);

    protected final AccountRepository accountRepository;
    protected final DocumentMapper documentMapper;

    public BaseDocumentProcessor(AccountRepository accountRepository, DocumentMapper documentMapper) {
        this.accountRepository = accountRepository;
        this.documentMapper = documentMapper;
    }

    @Override
    public Document process(DocumentRequestDto dto) {
        System.out.println("Processing using " + Type.BASE);
        validateRequest(dto);

        var account = accountRepository.findById(dto.getAccountId()).orElseThrow();
        var document = documentMapper.toEntity(dto, account);
        return document;
    }

    @Override
    public Document process(Document document, DocumentRequestDto documentDto) {
        validateRequest(document, documentDto);

        var account = accountRepository.findById(documentDto.getAccountId()).orElseThrow();
        var updated = documentMapper.updateWith(document, documentDto, account);
        return updated;
    }

    protected void validateRequest(DocumentRequestDto documentDto) {
        Objects.requireNonNull(documentDto, "Document request is required");

        if (documentDto.getDocumentType() == null) throw new IllegalArgumentException("Document type is required");
        if (documentDto.getAccountId() == null) throw new IllegalArgumentException("Account is required");

        var documentType = documentDto.getDocumentType();
        if (documentType != DocumentTypeDto.TRANSFER) {
            if (documentDto.getTransfer() != null) throw new IllegalArgumentException("Transfer data must not be provided for regular documents");
            if (documentDto.getDocumentItems() == null || documentDto.getDocumentItems().isEmpty()) throw new IllegalArgumentException("At least one document item is required for regular documents");

            var documentItems = documentDto.getDocumentItems();
            for (var item : documentItems) {
                if (item.getItemId() == null) throw new IllegalArgumentException("Item ID is required for every document item");
                if (item.getItemType() == null) throw new IllegalArgumentException("Item type is required for every document item");
            }
        }

        if (documentDto.getDocumentType() == DocumentTypeDto.INVOICE && (StringUtils.isBlank(documentDto.getInvoiceNumber()))) throw new IllegalArgumentException("Invoice number is required for Invoice documents");
        if (documentDto.getDocumentType() != DocumentTypeDto.INVOICE && (StringUtils.isNotBlank(documentDto.getInvoiceNumber()))) throw new IllegalArgumentException("Invoice number is only allowed for Invoice documents");

        if (COUNTERPARTY_REQUIRED_TYPES.contains(documentDto.getDocumentType()) && documentDto.getCounterpartyId() == null) throw new IllegalArgumentException("Counterparty is required for Bill and Invoice documents");

        if (documentType != DocumentTypeDto.TRANSFER && StringUtils.isBlank(documentDto.getDocumentName())) throw new IllegalArgumentException("Document name is required for regular documents");

        var payment = documentDto.getPayment();
        if (payment == null) throw new IllegalArgumentException("Payment data is required");
        if (StringUtils.isBlank(payment.getPaymentMethod())) throw new IllegalArgumentException("Payment method is required");
        if (StringUtils.isBlank(payment.getCurrencyCode())) throw new IllegalArgumentException("Currency code is required");
        if (payment.getExchangeRate() == null || payment.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Exchange rate must be greater than zero");

        if (documentType != DocumentTypeDto.TRANSFER) {
            var account = accountRepository.findById(documentDto.getAccountId()).orElseThrow();
            var accountCurrency = account.getCurrency().getCurrencyCode();
            var documentCurrency = payment.getCurrencyCode();
            var exchangeRate = payment.getExchangeRate();
            if (accountCurrency.equals(documentCurrency) && exchangeRate.compareTo(BigDecimal.ONE) != 0)
                throw new IllegalArgumentException("Exchange rate must be 1 when document currency matches account currency");
        }
    }

    protected void validateRequest(Document document, DocumentRequestDto documentDto) {
        validateRequest(documentDto);
        Objects.requireNonNull(document, "Document to update is required");

        if (documentDto.getVersion() == null) throw new IllegalArgumentException("Document version is required for update");

        var sourceType = document.getDocumentType();
        var targetType = DocumentType.findByValue(documentDto.getDocumentType().getValue());
        if (sourceType == DocumentType.TRANSFER && targetType != DocumentType.TRANSFER) throw new IllegalArgumentException("Cannot change a Transfer document into another document type");
        if (sourceType != DocumentType.TRANSFER && targetType == DocumentType.TRANSFER) throw new IllegalArgumentException("Cannot change a regular document into a Transfer");
    }
}
