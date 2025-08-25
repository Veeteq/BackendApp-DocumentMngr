package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.DocumentItem;
import com.veeteq.documentmngr.repository.ItemRepository;
import com.veeteq.documentmngr.rest.dto.DocumentItemRequestDto;
import com.veeteq.documentmngr.rest.dto.DocumentItemResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {ItemMapper.class})
public interface DocumentItemMapper {

    @Mapping(target = "documentItemId", source = "entity.financialRecord.id")
    @Mapping(target = "item",           source = "entity.financialRecord.item")
    @Mapping(target = "itemQuantity",   source = "entity.financialRecord.count")
    @Mapping(target = "itemPrice",      source = "entity.financialRecord.price")
    @Mapping(target = "itemComment",    source = "entity.financialRecord.comment")
    @Mapping(target = "version",        source = "entity.version")
    DocumentItemResponseDto toDto(DocumentItem entity);
/*
    @Mapping(target = "withId",               ignore = true)
    @Mapping(target = "withDocument",         expression = "java(document)")
    @Mapping(target = "withDocumentItemType", source = "dto.itemType")
    @Mapping(target = "withItem",             expression  = "java(itemRepository.findById(dto.getItemId()).orElseThrow())")
    @Mapping(target = "withItemQuantity",     source = "dto.itemQuantity")
    @Mapping(target = "withItemPrice",        source = "dto.itemPrice")
    @Mapping(target = "withItemComment",      source = "dto.itemDescription")
    DocumentItem toEntity(DocumentItemRequestDto dto, Document document, @Autowired ItemRepository itemRepository);
*/
    @Mapping(target = "withId",               source = "dto.seqId")
    @Mapping(target = "withDocument",         expression = "java(document)")
    @Mapping(target = "withDocumentItemType", source = "dto.itemType")
    @Mapping(target = "withItem",             expression  = "java(itemRepository.findById(dto.getItemId()).orElseThrow())")
    @Mapping(target = "withItemQuantity",     source = "dto.itemQuantity")
    @Mapping(target = "withItemPrice",        source = "dto.itemPrice")
    @Mapping(target = "withItemComment",      source = "dto.itemDescription")
    @Mapping(target = "withVersion",          source = "dto.version")
    DocumentItem toEntity(DocumentItemRequestDto dto, Document document, @Autowired ItemRepository itemRepository);
}
