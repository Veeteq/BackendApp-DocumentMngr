package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.rest.dto.AccountDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Currency;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "withId",          source = "accountId")
    @Mapping(target = "withName",        source = "accountName")
    @Mapping(target = "withCurrency",    source = "accountCurrency")
    @Mapping(target = "withDescription", source = "accountDescription")
    @Mapping(target = "withImageUrl",    source = "accountImageUrl")
    Account toEntity(AccountDto dto);

    @Mapping(target = "accountId",          source = "id")
    @Mapping(target = "accountName",        source = "name")
    @Mapping(target = "accountDescription", source = "description")
    @Mapping(target = "accountCurrency",    source = "currency")
    @Mapping(target = "accountImageUrl",    source = "imageUrl")
    AccountDto toDto(Account entity);

    //void updateWith(AccountDto dto, @MappingTarget Account entity);

    default Account updateWith(AccountDto dto, Account entity) {
        var updated = Account.updater(entity)
                .withName(dto.getAccountName())
                .withDescription(dto.getAccountDescription())
                .withCurrency(Currency.getInstance( dto.getAccountCurrency()))
                .withImageUrl(dto.getAccountImageUrl())
                .build();
        return updated;
    }

}
