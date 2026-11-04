package com.veeteq.documentmngr.integration.addressbook;

import com.veeteq.documentmngr.rest.dto.CounterpartyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressBookMapper {

    @Mapping(target = "counterpartyId",   source = "id")
    @Mapping(target = "counterpartyName", expression = "java(resolveCounterpartyName(source))")
    CounterpartyDto map(ContactResponseDto source);

    default String resolveCounterpartyName(ContactResponseDto source) {
        return switch (source.contactType()) {
            case COMPANY -> source.companyName() + " / " + source.displayName();
            case PERSON -> source.firstName() + " " + source.lastName();
        };
    }

}
