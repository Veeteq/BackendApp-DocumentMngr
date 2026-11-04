package com.veeteq.documentmngr.integration.addressbook;

import java.util.List;

public record ContactResponseDto(
        ContactType contactType,
        Long id,
        AddressDto address,
        String displayName,
        String bankAccountNumber,
        List<String> tags,
        Long version,
        String companyName,
        String firstName,
        String lastName) {}