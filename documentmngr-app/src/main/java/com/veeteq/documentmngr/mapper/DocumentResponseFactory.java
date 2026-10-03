package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.integration.addressbook.AddressBookClient;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentResponseFactory {

    private final DocumentMapper documentMapper;
    private final AddressBookClient addressBookClient;

    public DocumentResponseFactory(DocumentMapper documentMapper, AddressBookClient addressBookClient) {
        this.documentMapper = documentMapper;
        this.addressBookClient = addressBookClient;
    }

    public DocumentResponseDto create(Document document) {
        var dto = documentMapper.toDto(document);

        if (document.getCounterpartyId() != null) {
            addressBookClient
                    .getContact(document.getCounterpartyId())
                    .ifPresent(dto::setCounterparty);
        }

        return dto;
    }

    public List<DocumentResponseDto> create(List<Document> documents) {
        // batch implementation
        return null;
    }
}