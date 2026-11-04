package com.veeteq.documentmngr.integration.addressbook;

public record AddressDto(
        String country,
        String postcode,
        String city,
        String street) {}