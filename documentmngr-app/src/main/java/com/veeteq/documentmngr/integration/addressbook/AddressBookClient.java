package com.veeteq.documentmngr.integration.addressbook;

import com.veeteq.documentmngr.rest.dto.CounterpartyDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AddressBookClient {

    Optional<CounterpartyDto> getContact(Long contactId);

    List<CounterpartyDto> getContacts(Collection<Long> contactIds);
}