package com.veeteq.documentmngr.integration.addressbook;

import com.veeteq.documentmngr.integration.addressbook.dto.ABAddressDto;
import com.veeteq.documentmngr.integration.addressbook.dto.ABCompanyDto;
import com.veeteq.documentmngr.integration.addressbook.dto.ABContactDto;
import com.veeteq.documentmngr.integration.addressbook.dto.ABPersonDto;
import com.veeteq.documentmngr.rest.dto.CompanyDto;
import com.veeteq.documentmngr.rest.dto.CounterpartyDto;
import com.veeteq.documentmngr.rest.dto.PersonDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressBookMapper {

    default CounterpartyDto map(ABContactDto source) {
        if (source == null) return null;
        if (source instanceof ABPersonDto person) return map(person);
        if (source instanceof ABCompanyDto company) return map(company);

        throw new IllegalArgumentException("Unsupported AddressBook contact type: " + source.getClass().getName());
    }

    @Mapping(target = "type", constant = "PERSON")
    PersonDto map(ABPersonDto source);

    @Mapping(target = "type", constant = "COMPANY")
    CompanyDto map(ABCompanyDto source);

    AddressDto map(ABAddressDto source);
}