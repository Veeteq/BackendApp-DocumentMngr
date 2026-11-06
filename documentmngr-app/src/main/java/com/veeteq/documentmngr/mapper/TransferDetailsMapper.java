package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.rest.dto.TransferDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface TransferDetailsMapper {

    @Mapping(target = "sourceAccount",   source = "account")
    @Mapping(target = "sourceAmount",    source = "transferSourceAmount")
    @Mapping(target = "sourceCurrency",  source = "account.currency")
    @Mapping(target = "targetAccount",   source = "targetAccount")
    @Mapping(target = "targetAmount",    source = "transferTargetAmount")
    @Mapping(target = "targetCurrency",  source = "targetAccount.currency")
    @Mapping(target = "provisionAmount", ignore = true)
    @Mapping(target = "provisionAt",     ignore = true)
    TransferDetailsDto mapTransfer(Document entity);

    @Named("transferDetails")
    default TransferDetailsDto toDto(Document document) {
        if (document == null || document.getDocumentType() != DocumentType.TRANSFER) return null;
        return mapTransfer(document);
    }

}
