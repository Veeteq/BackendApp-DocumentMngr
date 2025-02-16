package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.repository.ItemRepository;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentResponseDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class DocumentMapperDecorator implements DocumentMapper {

    @Autowired
    @Qualifier("delegate")
    private DocumentMapper documentMapper;

    @Autowired
    private DocumentItemMapper documentItemMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public DocumentResponseDto toDto(Document entity) {
        return documentMapper.toDto(entity);
    }

    @Override
    public Document toEntity(DocumentRequestDto dto, AccountRepository accountRepository) {
        var entity = documentMapper.toEntity(dto, accountRepository);
        after(dto, entity);
        return entity;
    }

    @AfterMapping
    public void after(DocumentRequestDto dto, @MappingTarget Document entity) {
        dto.getDocumentItems().stream()
                .map(itemDto -> documentItemMapper.toEntity(itemDto, entity, itemRepository))
                .forEach(entity::addToDocumentItems);
    }
}
