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

    @Mapping(target = "documentItemId", source = "entity.id.sequenceNumber")
    @Mapping(target = "item",           source = "entity.expense.item")
    @Mapping(target = "itemQuantity",   source = "entity.expense.count")
    @Mapping(target = "itemPrice",      source = "entity.expense.price")
    @Mapping(target = "itemComment",    source = "entity.expense.comment")
    DocumentItemResponseDto toDto(DocumentItem entity);

    @Mapping(target = "withDocument",         expression = "java(document)")
    @Mapping(target = "withDocumentItemType", source = "dto.itemType")
    @Mapping(target = "withItem",             expression  = "java(itemRepository.findById(dto.getItemId()).orElseThrow())")
    @Mapping(target = "withItemQuantity",     source = "dto.itemQuantity")
    @Mapping(target = "withItemPrice",        source = "dto.itemPrice")
    @Mapping(target = "withItemComment",      source = "dto.itemDescription")
    DocumentItem toEntity(DocumentItemRequestDto dto, Document document, @Autowired ItemRepository itemRepository);

}
