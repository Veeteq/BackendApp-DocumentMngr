package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.Category;
import com.veeteq.documentmngr.model.Item;
import com.veeteq.documentmngr.rest.dto.CategoryDto;
import com.veeteq.documentmngr.rest.dto.ItemDto;
import com.veeteq.documentmngr.rest.dto.ItemsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public ItemDto toDto(Item entity) {
        var categoryDto = toDto(entity.getCategory());
        return new ItemDto()
                .itemId(entity.getId())
                .itemName(entity.getName())
                .itemCategory(categoryDto);
    }

    public ItemsResponseDto toDto(Page<Item> result) {
        return new ItemsResponseDto()
                .data(result.stream().map(this::toDto).collect(Collectors.toList()))
                .pageSize(result.getSize())
                .currentPage(result.getNumber())
                .totalPages(result.getTotalPages())
                .totalItems(result.getTotalElements());
    }

    public CategoryDto toDto(Category entity) {
        return new CategoryDto()
                .categoryId(entity.getId())
                .categoryName(entity.getName());
    }
}
