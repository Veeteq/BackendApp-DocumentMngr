package com.veeteq.documentmngr.integration.addressbook;

import com.veeteq.documentmngr.rest.dto.CounterpartyDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class AddressBookRestClient implements AddressBookClient {

    private final RestClient restClient;
    private final AddressBookMapper mapper;

    public AddressBookRestClient(RestClient restClient, AddressBookMapper mapper) {
        this.restClient = restClient;
        this.mapper = mapper;
    }

    @Override
    public Optional<CounterpartyDto> getContact(Long contactId) {
        try {
            var response = restClient.get()
                    .uri("/api/addressbook/contacts/{id}", contactId)
                    .retrieve()
                    .body(ContactResponseDto.class);
            var counterparty = mapper.map(response);
            return Optional.ofNullable(counterparty);

        } catch (HttpClientErrorException.NotFound e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<CounterpartyDto> getContacts(Collection<Long> contactIds) {
        return List.of();
    }
}
