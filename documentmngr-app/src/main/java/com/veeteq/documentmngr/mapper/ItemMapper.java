package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.Category;
import com.veeteq.documentmngr.model.Item;
import com.veeteq.documentmngr.rest.dto.CategoryDto;
import com.veeteq.documentmngr.rest.dto.ItemDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public ItemDto toDto(Item entity) {
        var dto = new ItemDto()
                .itemId(entity.getId())
                .itemName(entity.getName())
                .itemCategory(toDto(entity.getCategory()));
        return dto;
    }

    public List<ItemDto> toDto(Page<Item> result) {
        /* TODO
        var dto = new ItemPageDto()
                .data(result.stream().map(this::toDto).collect(Collectors.toList()))
                .size(result.getSize())
                .page(result.getNumber())
                .totalPages(result.getTotalPages())
                .numberOfElements(result.getNumberOfElements())
                .totalElements(result.getTotalElements());
        return dto;
         */
        return result.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CategoryDto toDto(Category entity) {
        var dto = new CategoryDto()
                .categoryId(entity.getId())
                .categoryName(entity.getName());
        return dto;
    }
}
