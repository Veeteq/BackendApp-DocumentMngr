package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.*;
import com.veeteq.documentmngr.rest.dto.DocumentItemResponseDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DocumentMapperOld {

    public DocumentResponseDto toDto(Document entity) {
        var dto = new DocumentResponseDto()
                .documentId(entity.getId())
                .documentDate(entity.getDocumentDate())
                .documentType(entity.getDocumentType() != null ? entity.getDocumentType().getDisplayName() : "N/A")
                .documentName(entity.getDocumentName())
                .currencyCode(entity.getCurrency() != null ? entity.getCurrency().getCurrencyCode() : "PLN")
                .exchangeRate(entity.getExchangeRate())
                .documentItemsCount(entity.getDocumentItems().size());

        Supplier<Stream<DocumentItem>> documentItemsStream = () -> entity.getDocumentItems().stream();
        var documentItems = documentItemsStream.get()
                .map(this::toDto)
                .collect(Collectors.toList());
        dto.documentItems(documentItems);

        var documentAmount = getDocumentAmmount(entity.getDocumentType(), documentItemsStream);
        dto.setDocumentAmount(documentAmount);

        return dto;
    }

    private DocumentItemResponseDto toDto(DocumentItem documentItem) {
        var financialRecord = documentItem.getExpense() != null ? documentItem.getExpense() : documentItem.getIncome();
        var multiplicand = financialRecord instanceof Expense ? BigDecimal.ONE.negate() : BigDecimal.ONE;
        var dto = new DocumentItemResponseDto()
                //.setType(documentItem.getType().getDisplayName())
                .documentItemId(financialRecord.getId())
                //.item(itemMapper.toDto(financialRecord.getItem()))
                //itemQuantity(financialRecord.getCount())
                //.itemPrice(financialRecord.getPrice().multiply(multiplicand))
                .itemComment(financialRecord.getComment());
        return dto;
    }

    private BigDecimal getDocumentAmmount(DocumentType documentType, Supplier<Stream<DocumentItem>> documentItemsStream) {
        BigDecimal documentAmount;

        if (documentType == DocumentType.TRANSFER) {
            documentAmount = documentItemsStream.get()
                    .filter(documentItem -> documentItem.getType() == DocumentItemType.INC)
                    .map(DocumentItem::getIncome)
                    .findFirst()
                    .map(income -> income.getCount().multiply(income.getPrice()).setScale(2, RoundingMode.HALF_UP))
                    .get();
        } else {
            documentAmount = documentItemsStream.get()
                    .map(item -> item.getType() == DocumentItemType.EXP ? item.getExpense() : item.getIncome())
                    .map(item -> {
                        var retVal = item.getCount().multiply(item.getPrice()).setScale(2, RoundingMode.HALF_UP);
                        if (item instanceof Expense) {
                            return retVal.negate();
                        }
                        return retVal;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return documentAmount;
    }
}
